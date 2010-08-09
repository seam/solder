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
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * An immutable bean which provides basic defaulting and checking of arguments.
 * 
 * @author stuart
 * @author Pete Muir
 * 
 */
public class BeanImpl<T> extends AbstractImmutableBean<T> implements Bean<T>
{

   final BeanLifecycle<T> beanLifecycle;

   /**
    * Create a new, immutable bean. All arguments passed as collections are
    * defensively copied.
    * 
    * @param beanClass The Bean class, may not be null
    * @param name The bean name
    * @param qualifiers The bean's qualifiers, if null, a singleton set of
    *           {@link Default} is used
    * @param scope The bean's scope, if null, the default scope of
    *           {@link Dependent} is used
    * @param stereotypes The bean's stereotypes, if null, an empty set is used
    * @param types The bean's types, if null, the beanClass and {@link Object}
    *           will be used
    * @param alternative True if the bean is an alternative
    * @param nullable True if the bean is nullable
    * @param injectionPoints the bean's injection points, if null an empty set
    *           is used
    * @param beanLifecycle Handler for {@link #create(CreationalContext)} and
    *           {@link #destroy(Object, CreationalContext)}
    * @param toString the string representation of the bean, if null the built
    *           in representation is used, which states the bean class and
    *           qualifiers
    * 
    * @throws IllegalArgumentException if the beanClass is null
    */
   public BeanImpl(Class<?> beanClass, String name, Set<Annotation> qualifiers, Class<? extends Annotation> scope, Set<Class<? extends Annotation>> stereotypes, Set<Type> types, boolean alternative, boolean nullable, Set<InjectionPoint> injectionPoints, BeanLifecycle<T> beanLifecycle, String toString)
   {
      super(beanClass, name, qualifiers, scope, stereotypes, types, alternative, nullable, injectionPoints, toString);
      this.beanLifecycle = beanLifecycle;
   }

   public T create(CreationalContext<T> arg0)
   {
      return beanLifecycle.create(this, arg0);
   }

   public void destroy(T arg0, CreationalContext<T> arg1)
   {
      beanLifecycle.destroy(this, arg0, arg1);
   }

}
