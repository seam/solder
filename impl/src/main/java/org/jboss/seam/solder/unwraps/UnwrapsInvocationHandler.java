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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javassist.util.proxy.MethodHandler;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.seam.solder.bean.ImmutableInjectionPoint;

public class UnwrapsInvocationHandler implements MethodHandler
{
   
   final private BeanManager manager;
   final private AnnotatedMethod<?> annotatedMethod;
   final private Method method;
   final private Bean<?> bean;
   final private InjectionPoint[] injectionPoints;
   final private Bean<?> mainClassBean;
   final private InjectionPoint injectionPoint;


   public UnwrapsInvocationHandler(BeanManager manager, AnnotatedMethod<?> method, Bean<?> bean, InjectionPoint injectionPoint, Set<Annotation> declaringBeanQualifiers)
   {
      this.manager = manager;
      this.method = method.getJavaMember();
      this.annotatedMethod = method;
      this.bean = bean;
      injectionPoints = new InjectionPoint[this.method.getTypeParameters().length];
      for (int i = 0; i < injectionPoints.length; ++i)
      {
         injectionPoints[i] = new ImmutableInjectionPoint(method.getParameters().get(i), manager, bean, false, false);
      }
      Type mainType = method.getDeclaringType().getBaseType();
      HashSet<Annotation> mainClassQualifiers = new HashSet<Annotation>(declaringBeanQualifiers);
      Set<Bean<?>> beans = manager.getBeans(mainType, mainClassQualifiers.toArray(new Annotation[0]));
      if (beans.isEmpty())
      {
         throw new UnsatisfiedResolutionException("Could not find declaring bean for @Unwrap method " + method.getDeclaringType().getJavaClass() + "." + this.method.getName());
      }
      else if (beans.size() > 1)
      {
         throw new AmbiguousResolutionException("Could not find declaring bean for @Unwrap method " + method.getDeclaringType().getJavaClass() + "." + this.method.getName());
      }
      mainClassBean = beans.iterator().next();
      this.injectionPoint = injectionPoint;
   }

   public Object invoke(Object proxy, Method thisMethod, Method m, Object[] args) throws Throwable
   {
      CreationalContext<?> ctx = manager.createCreationalContext(bean);
      Object[] params = new Object[method.getParameterTypes().length];
      for (int i = 0; i < this.method.getParameterTypes().length; ++i)
      {
         if (InjectionPoint.class.isAssignableFrom(this.method.getParameterTypes()[i]))
         {
            params[i] = injectionPoint;
         }
         else
         {
            params[i] = manager.getInjectableReference(injectionPoints[i], ctx);
         }
      }
      Object base = manager.getReference(mainClassBean, annotatedMethod.getDeclaringType().getJavaClass(), ctx);
      
      Object result = method.invoke(base, params);
      Object ret = thisMethod.invoke(result, args);
      ctx.release();
      return ret;
   }

}
