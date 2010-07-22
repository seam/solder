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
   public static Bean<?> resolveBean(Type beanType, Annotation[] qualifiers, BeanManager manager) throws AmbiguousBeanException, BeanResolutionException, BeanNotFoundException
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
    * gets a reference to a bean with the given type and qualifiers
    */
   public static Object getReference(Type beanType, Annotation[] qualifiers, BeanManager manager) throws AmbiguousBeanException, BeanResolutionException, BeanNotFoundException
   {
      Bean<?> bean = resolveBean(beanType, qualifiers, manager);
      CreationalContext<?> context = manager.createCreationalContext(bean);
      return manager.getReference(bean, beanType, context);
   }

   /**
    * gets a reference to a bean with the given type and qualifiers
    */
   public static <T> T getReference(Class<T> beanType, Annotation[] qualifiers, BeanManager manager) throws AmbiguousBeanException, BeanResolutionException, BeanNotFoundException
   {
      return (T) getReference((Type) beanType, qualifiers, manager);
   }
}
