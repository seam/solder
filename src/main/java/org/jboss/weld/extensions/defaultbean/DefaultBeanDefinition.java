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
package org.jboss.weld.extensions.defaultbean;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;

class DefaultBeanDefinition
{
   private final Class<?> type;
   private final Set<? extends Annotation> qualifiers;
   private final Bean<?> defaultBean;

   public DefaultBeanDefinition(Class<?> type, Set<? extends Annotation> qualifiers, Bean<?> defaultBean)
   {
      this.type = type;
      this.qualifiers = new HashSet<Annotation>(qualifiers);
      this.defaultBean = defaultBean;
   }

   public boolean matches(Bean<?> bean)
   {
      if (bean.getTypes().contains(type))
      {
         for (Annotation a : qualifiers)
         {
            if (!bean.getQualifiers().contains(a))
            {
               return false;
            }
         }
         return true;
      }
      return false;
   }

   public Bean<?> getDefaultBean()
   {
      return defaultBean;
   }

   public Class<?> getType()
   {
      return type;
   }

   public Set<? extends Annotation> getQualifiers()
   {
      return Collections.unmodifiableSet(qualifiers);
   }

}
