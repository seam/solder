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
package org.jboss.weld.extensions.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.extensions.literal.DefaultLiteral;

/**
 * Utility class to resolve and acquire references to Beans
 * 
 * @author Stuart Douglas
 * 
 */
public class BeanResolver
{
   private BeanResolver()
   {
   }

   /**
    * Resolves a bean
    * 
    */
   public static Bean<?> resolveBean(Type beanType, BeanManager manager, Annotation... qualifiers) throws AmbiguousBeanException, BeanNotFoundException
   {
      Set<Bean<?>> beans = manager.getBeans(beanType, qualifiers);
      if (beans.size() == 0)
      {
         throw new BeanNotFoundException(beanType, qualifiers);
      }
      if (beans.size() != 1)
      {
         throw new AmbiguousBeanException(beanType, qualifiers, beans);
      }
      return beans.iterator().next();
   }

   /**
    * Resolves a bean with the qualifier @Default
    * 
    */
   public static Bean<?> resolveDefaultBean(Type beanType, BeanManager manager) throws AmbiguousBeanException, BeanNotFoundException
   {
      Annotation[] qualifiers = new Annotation[1];
      qualifiers[0] = DefaultLiteral.INSTANCE;
      Set<Bean<?>> beans = manager.getBeans(beanType, qualifiers);
      if (beans.size() == 0)
      {
         throw new BeanNotFoundException(beanType, qualifiers);
      }
      if (beans.size() != 1)
      {
         throw new AmbiguousBeanException(beanType, qualifiers, beans);
      }
      return beans.iterator().next();
   }

   /**
    * gets a reference to a bean with the given type and qualifiers
    */
   public static Object getReference(Type beanType, BeanManager manager, Annotation... qualifiers) throws AmbiguousBeanException, BeanNotFoundException
   {
      Bean<?> bean = resolveBean(beanType, manager, qualifiers);
      CreationalContext<?> context = manager.createCreationalContext(bean);
      return manager.getReference(bean, beanType, context);
   }

   /**
    * gets a reference to a bean with the given type and qualifiers
    */
   public static <T> T getReference(Class<T> beanType, BeanManager manager, Annotation... qualifiers) throws AmbiguousBeanException, BeanNotFoundException
   {
      return (T) getReference((Type) beanType, manager, qualifiers);
   }

   /**
    * gets a reference to a bean with the given type and qualifier @Default
    */
   public static Object getDefaultReference(Type beanType, BeanManager manager) throws AmbiguousBeanException, BeanNotFoundException
   {
      Bean<?> bean = resolveDefaultBean(beanType, manager);
      CreationalContext<?> context = manager.createCreationalContext(bean);
      return manager.getReference(bean, beanType, context);
   }

   /**
    * gets a reference to a bean with the given type and qualifier @Default
    */
   public static <T> T getDefaultReference(Class<T> beanType, BeanManager manager) throws AmbiguousBeanException, BeanNotFoundException
   {
      return (T) getDefaultReference((Type) beanType, manager);
   }

}
