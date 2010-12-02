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
package org.jboss.seam.solder.bean.generic;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.PassivationCapable;

import org.jboss.seam.solder.bean.ForwardingBean;
import org.jboss.seam.solder.literal.AnyLiteral;

/**
 * A helper class for implementing generic bean functionality
 * 
 * @author Pete Muir
 *
 */
abstract class AbstactGenericBean<T> extends ForwardingBean<T> implements PassivationCapable
{

   private final Bean<T> delegate;
   private final Set<Annotation> qualifiers;
   private final BeanManager beanManager;
   private final String id;
   private final boolean alternative;
   private final Class<?> beanClass;

   protected AbstactGenericBean(Bean<T> delegate, Set<Annotation> qualifiers, Annotation configuration, String id, boolean alternative, Class<?> beanClass, BeanManager beanManager)
   {
      this.delegate = delegate;
      this.beanManager = beanManager;
      this.qualifiers = new HashSet<Annotation>();
      for (Annotation qualifier : qualifiers)
      {
         // Don't add the GenericMarker qualifier, this is a pseudo qualifier,
         // used to remove declared qualifiers
         if (!qualifier.annotationType().equals(GenericMarker.class))
         {
            this.qualifiers.add(qualifier);
         }
      }
      this.qualifiers.add(AnyLiteral.INSTANCE);
      this.id = getClass().getName() + "-" + configuration.toString() + "-" + id;
      this.alternative = alternative;
      this.beanClass = beanClass;
   }
   
   protected BeanManager getBeanManager()
   {
      return beanManager;
   }

   @Override
   protected Bean<T> delegate()
   {
      return delegate;
   }

   @Override
   public Set<Annotation> getQualifiers()
   {
      return qualifiers;
   }

   public String getId()
   {
      return id;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (obj instanceof GenericManagedBean<?>)
      {
         GenericManagedBean<?> that = (GenericManagedBean<?>) obj;
         return this.getId().equals(that.getId());
      }
      else
      {
         return false;
      }
   }

   @Override
   public int hashCode()
   {
      return id.hashCode();
   }

   @Override
   public Class<?> getBeanClass()
   {
      return beanClass;
   }

   @Override
   public boolean isAlternative()
   {
      return alternative;
   }

}