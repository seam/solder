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
package org.jboss.weld.extensions.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Named;

import org.jboss.weld.extensions.literal.AnyLiteral;
import org.jboss.weld.extensions.literal.DefaultLiteral;
import org.jboss.weld.extensions.reflection.annotated.Annotateds;

/**
 * class that can build a bean from an AnnotatedType.
 * 
 * @author stuart
 * 
 */
public class BeanBuilder<T>
{

   private final BeanManager beanManager;
   
   private Class<?> beanClass;
   private String name;
   private Set<Annotation> qualifiers;
   private Class<? extends Annotation> scope;
   private Set<Class<? extends Annotation>> stereotypes;
   private Set<Type> types;
   private Set<InjectionPoint> injectionPoints;
   private boolean alternative;
   private boolean nullable;
   private BeanLifecycle<T> beanLifecycle;
   boolean passivationCapable;
   private String id;
   private String toString;

   public BeanBuilder(BeanManager beanManager)
   {
      this.beanManager = beanManager;
   }

   public BeanBuilder<T> defineBeanFromAnnotatedType(AnnotatedType<T> type)
   {
      this.beanClass = type.getJavaClass();
      InjectionTarget<T> injectionTarget;
      if (!type.getJavaClass().isInterface())
      {
         injectionTarget = beanManager.createInjectionTarget(type);
      }
      else
      {
         injectionTarget = new DummyInjectionTarget<T>();
      }
      this.beanLifecycle = new BeanLifecycleImpl<T>(injectionTarget);
      this.injectionPoints = injectionTarget.getInjectionPoints();
      this.qualifiers = new HashSet<Annotation>();
      this.stereotypes = new HashSet<Class<? extends Annotation>>();
      this.types = new HashSet<Type>();
      for (Annotation annotation : type.getAnnotations())
      {
         if (beanManager.isQualifier(annotation.annotationType()))
         {
            this.qualifiers.add(annotation);
         }
         else if (beanManager.isScope(annotation.annotationType()))
         {
            this.scope = annotation.annotationType();
         }
         else if (beanManager.isStereotype(annotation.annotationType()))
         {
            this.stereotypes.add(annotation.annotationType());
         }
         if (annotation instanceof Named)
         {
            this.name = ((Named) annotation).value();
         }
         if (annotation instanceof Alternative)
         {
            this.alternative = true;
         }
      }
      if (this.scope == null)
      {
         this.scope = Dependent.class;
      }
      for (Class<?> c = type.getJavaClass(); c != Object.class && c != null; c = c.getSuperclass())
      {
         this.types.add(c);
      }
      for (Class<?> i : type.getJavaClass().getInterfaces())
      {
         this.types.add(i);
      }
      if (qualifiers.isEmpty())
      {
         qualifiers.add(DefaultLiteral.INSTANCE);
      }
      qualifiers.add(AnyLiteral.INSTANCE);
      this.id = BeanImpl.class.getName() + ":" + Annotateds.createTypeId(type);
      return this;
   }

   public Bean<T> create()
   {
      if (!passivationCapable)
      {
         return new BeanImpl<T>(beanClass, name, qualifiers, scope, stereotypes, types, alternative, nullable, injectionPoints, beanLifecycle, toString);
      }
      return new PassivationCapableBeanImpl<T>(id, beanClass, name, qualifiers, scope, stereotypes, types, alternative, nullable, injectionPoints, beanLifecycle, toString);
   }

   public Set<Annotation> getQualifiers()
   {
      return qualifiers;
   }

   public BeanBuilder<T> setQualifiers(Set<Annotation> qualifiers)
   {
      this.qualifiers = qualifiers;
      return this;
   }

   public Class<? extends Annotation> getScope()
   {
      return scope;
   }

   public BeanBuilder<T> setScope(Class<? extends Annotation> scope)
   {
      this.scope = scope;
      return this;
   }

   public Set<Class<? extends Annotation>> getStereotypes()
   {
      return stereotypes;
   }

   public BeanBuilder<T> setStereotypes(Set<Class<? extends Annotation>> stereotypes)
   {
      this.stereotypes = stereotypes;
      return this;
   }

   public Set<Type> getTypes()
   {
      return types;
   }

   public BeanBuilder<T> setTypes(Set<Type> types)
   {
      this.types = types;
      return this;
   }

   public boolean isAlternative()
   {
      return alternative;
   }

   public BeanBuilder<T> setAlternative(boolean alternative)
   {
      this.alternative = alternative;
      return this;
   }

   public boolean isNullable()
   {
      return nullable;
   }

   public BeanBuilder<T> setNullable(boolean nullable)
   {
      this.nullable = nullable;
      return this;
   }

   public BeanLifecycle<T> getBeanLifecycle()
   {
      return beanLifecycle;
   }

   public BeanBuilder<T> setBeanLifecycle(BeanLifecycle<T> beanLifecycle)
   {
      this.beanLifecycle = beanLifecycle;
      return this;
   }

   public Class<?> getBeanClass()
   {
      return beanClass;
   }
   
   public BeanBuilder<T> setBeanClass(Class<?> beanClass)
   {
      this.beanClass = beanClass;
      return this;
   }

   public BeanManager getBeanManager()
   {
      return beanManager;
   }

   public String getName()
   {
      return name;
   }

   public BeanBuilder<T> setName(String name)
   {
      this.name = name;
      return this;
   }

   public boolean isPassivationCapable()
   {
      return passivationCapable;
   }

   public BeanBuilder<T> setPassivationCapable(boolean passivationCapable)
   {
      this.passivationCapable = passivationCapable;
      return this;
   }

   public String getId()
   {
      return id;
   }

   public BeanBuilder<T> setId(String id)
   {
      this.id = id;
      return this;
   }
   
   public Set<InjectionPoint> getInjectionPoints()
   {
      return injectionPoints;
   }
   
   public BeanBuilder<T> setInjectionPoints(Set<InjectionPoint> injectionPoints)
   {
      this.injectionPoints = injectionPoints;
      return this;
   }
   
   public BeanBuilder<T> setToString(String toString)
   {
      this.toString = toString;
      return this;
   }
   
   public String getToString()
   {
      return toString;
   }

}
