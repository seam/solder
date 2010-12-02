/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.solder.unwraps;

import static org.jboss.seam.solder.reflection.Reflections.cast;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;

import org.jboss.seam.solder.bean.Beans;
import org.jboss.seam.solder.literal.DefaultLiteral;

/**
 * Bean implementation that produces a JDK proxy
 * 
 * when a method is invoked on the proxy it calls the Unwraps producer method
 * and invokes the method on the returned object
 * 
 * @author Stuart Douglas
 * 
 */
public class UnwrapsProducerBean<M> implements Bean<M>
{

   final private Class<?> beanClass;

   final private String name;

   final private Set<Annotation> qualifiers;

   final private Set<Annotation> declaringClassQualifiers;

   final private Set<Type> types;

   final private Class<M> proxyClass;

   final private BeanManager manager;

   final private AnnotatedMethod<?> method;

   private final static Annotation[] defaultQualifiers = { DefaultLiteral.INSTANCE };

   public UnwrapsProducerBean(AnnotatedMethod<?> method, BeanManager manager)
   {
      this(method, resolveQualifiers(method, manager), resolveQualifiers(method.getDeclaringType(), manager), manager);
   }

   public UnwrapsProducerBean(AnnotatedMethod<?> method, Set<Annotation> methodQualifiers, Set<Annotation> beanQualifiers, BeanManager manager)
   {
      this.method = method;
      beanClass = method.getDeclaringType().getJavaClass();
      // get the name
      if (method.isAnnotationPresent(Named.class))
      {
         name = method.getAnnotation(Named.class).value();
      }
      else
      {
         name = null;
      }
      // get the qualifiers
      qualifiers = new HashSet<Annotation>(methodQualifiers);
      declaringClassQualifiers = new HashSet<Annotation>(beanQualifiers);
      // get the bean types
      types = new HashSet<Type>();
      Set<Class<?>> classes = new HashSet<Class<?>>();
      for (Type t : method.getTypeClosure())
      {
         if (t instanceof Class<?>)
         {
            Class<?> c = (Class<?>) t;
            types.add(c);
            classes.add(c);
         }
         else if (t instanceof ParameterizedType)
         {
            types.add(t);
         }
      }
      // build the properties
      Class<?>[] iarray = new Class[classes.size()];
      int count = 0;
      this.manager = manager;
      for (Class<?> c : classes)
      {
         iarray[count++] = c;
      }
      ProxyFactory f = new ProxyFactory();
      Class<?> retType = method.getJavaMember().getReturnType();
      if (retType.isInterface())
      {
         f.setSuperclass(Object.class);
         Class<?>[] ifaces = { retType };
         f.setInterfaces(ifaces);
      }
      else
      {
         f.setSuperclass(retType);
      }

      f.setFilter(new MethodFilter()
      {
         public boolean isHandled(Method m)
         {
            // ignore finalize()
            return !m.getName().equals("finalize");
         }
      });
      proxyClass = cast(f.createClass());
   }

   private static Set<Annotation> resolveQualifiers(Annotated method, BeanManager manager)
   {
      Set<Annotation> qualifiers = Beans.getQualifiers(manager, method.getAnnotations());
      if (qualifiers.isEmpty())
      {
         qualifiers.add(DefaultLiteral.INSTANCE);
      }
      return qualifiers;
   }

   public Class<?> getBeanClass()
   {
      return beanClass;
   }

   public Set<InjectionPoint> getInjectionPoints()
   {
      return Collections.emptySet();
   }

   public String getName()
   {
      return name;
   }

   public Set<Annotation> getQualifiers()
   {
      return qualifiers;
   }

   /**
    * the proxies that are injected all have Dependant scope
    */
   public Class<? extends Annotation> getScope()
   {
      return Dependent.class;
   }

   public Set<Class<? extends Annotation>> getStereotypes()
   {
      return Collections.emptySet();
   }

   public Set<Type> getTypes()
   {
      return types;
   }

   public boolean isAlternative()
   {
      return false;
   }

   public boolean isNullable()
   {
      return false;
   }

   public M create(CreationalContext<M> creationalContext)
   {
      Set<Bean<?>> beans = manager.getBeans(InjectionPoint.class, defaultQualifiers);
      Bean<?> injectionPointBean = (Bean<?>) beans.iterator().next();
      InjectionPoint injectionPoint = (InjectionPoint) manager.getReference(injectionPointBean, InjectionPoint.class, creationalContext);
      UnwrapsInvocationHandler hdl = new UnwrapsInvocationHandler(manager, this.method, this, injectionPoint, declaringClassQualifiers);
      try
      {
         M obj = proxyClass.newInstance();
         ((ProxyObject) obj).setHandler(hdl);
         creationalContext.push(obj);
         return obj;
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public void destroy(M instance, CreationalContext<M> creationalContext)
   {
      creationalContext.release();
   }

}
