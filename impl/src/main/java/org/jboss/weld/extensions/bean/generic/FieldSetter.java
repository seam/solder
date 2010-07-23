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
package org.jboss.weld.extensions.bean.generic;

import java.lang.reflect.Field;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

/**
 * Class that is responsible for setting the values of generic producer fields
 * 
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 * 
 */
class FieldSetter
{
   private final Field field;
   private final SyntheticQualifier qualifier;
   private final BeanManager beanManager;

   FieldSetter(BeanManager beanManager, Field field, SyntheticQualifier qualifier)
   {
      this.field = field;
      this.field.setAccessible(true);
      this.qualifier = qualifier;
      this.beanManager = beanManager;
   }

   public void set(Object instance, CreationalContext<?> ctx)
   {
      Bean<?> bean = beanManager.resolve(beanManager.getBeans(field.getType(), qualifier));
      if (bean == null)
      {
         throw new UnsatisfiedResolutionException("Could not resolve bean for Generic Producer field " + field.getDeclaringClass() + "." + field.getName() + " Type: " + field.getType() + " Qualifiers:" + qualifier);
      }
      Object value = beanManager.getReference(bean, field.getType(), ctx);
      try
      {
         field.set(instance, value);
      }
      catch (IllegalArgumentException e)
      {
         throw new RuntimeException(e);
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(e);
      }

   }

}
