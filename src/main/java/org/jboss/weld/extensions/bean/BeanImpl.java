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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;

/**
 * An immutable bean.
 * 
 * @author stuart
 * 
 * @param <T>
 */
class BeanImpl<T> implements Bean<T>
{
   private final Class<?> beanClass;
   private final InjectionTarget<T> injectionTarget;
   private final String name;
   private final Set<Annotation> qualifiers;
   private final Class<? extends Annotation> scope;
   private final Set<Class<? extends Annotation>> stereotypes;
   private final Set<Type> types;
   private final boolean alternative;
   private final boolean nullable;
   private final BeanLifecycle<T> beanLifecycle;

   BeanImpl(Class<?> beanClass, InjectionTarget<T> injectionTarget, String name, Set<Annotation> qualifiers, Class<? extends Annotation> scope, Set<Class<? extends Annotation>> stereotypes, Set<Type> types, boolean alternative, boolean nullable, BeanLifecycle<T> beanLifecycle)
   {
      this.beanClass = beanClass;
      this.injectionTarget = injectionTarget;
      this.name = name;
      this.qualifiers = new HashSet<Annotation>(qualifiers);
      this.scope = scope;
      this.stereotypes = new HashSet<Class<? extends Annotation>>(stereotypes);
      this.types = new HashSet<Type>(types);
      this.alternative = alternative;
      this.nullable = nullable;
      this.beanLifecycle = beanLifecycle;
   }

   public Class<?> getBeanClass()
   {
      return beanClass;
   }

   public Set<InjectionPoint> getInjectionPoints()
   {
      return injectionTarget.getInjectionPoints();
   }

   public InjectionTarget<T> getInjectionTarget()
   {
      return injectionTarget;
   }

   public String getName()
   {
      return name;
   }

   public Set<Annotation> getQualifiers()
   {
      return Collections.unmodifiableSet(qualifiers);
   }

   public Class<? extends Annotation> getScope()
   {
      return scope;
   }

   public Set<Class<? extends Annotation>> getStereotypes()
   {
      return Collections.unmodifiableSet(stereotypes);
   }

   public Set<Type> getTypes()
   {
      return Collections.unmodifiableSet(types);
   }

   public boolean isAlternative()
   {
      return alternative;
   }

   public boolean isNullable()
   {
      return nullable;
   }

   public T create(CreationalContext<T> arg0)
   {
      return beanLifecycle.create(this, arg0);
   }

   public void destroy(T arg0, CreationalContext<T> arg1)
   {
      beanLifecycle.destroy(this, arg0, arg1);
   }
   
   @Override
   public String toString()
   {
      return new StringBuilder().append("Custom Bean with bean class ").append(beanClass).append(" and qualifiers ").append(qualifiers).toString();
   }

}
