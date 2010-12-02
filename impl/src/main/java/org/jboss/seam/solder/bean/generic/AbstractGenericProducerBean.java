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
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.seam.solder.bean.Beans;
import org.jboss.seam.solder.reflection.Reflections;

/**
 * A helper class for implementing producer methods and fields on generic beans
 * 
 * @author Pete Muir
 *
 */
abstract class AbstractGenericProducerBean<T> extends AbstactGenericBean<T>
{

   private final Type declaringBeanType;
   private final Annotation[] declaringBeanQualifiers;
   private final Class<? extends Annotation> scopeOverride;

   protected AbstractGenericProducerBean(Bean<T> delegate, Annotation genericConfiguration, Set<Annotation> qualifiers, Set<Annotation> declaringBeanQualifiers, Class<? extends Annotation> scopeOverride, String id, boolean alternative, Class<?> beanClass, BeanManager beanManager)
   {
      super(delegate, qualifiers, genericConfiguration, id, alternative, beanClass, beanManager);
      this.declaringBeanType = delegate.getBeanClass();
      this.declaringBeanQualifiers = declaringBeanQualifiers.toArray(Reflections.EMPTY_ANNOTATION_ARRAY);
      this.scopeOverride = scopeOverride;
   }
   
   protected Annotation[] getDeclaringBeanQualifiers()
   {
      return declaringBeanQualifiers;
   }
   
   protected Type getDeclaringBeanType()
   {
      return declaringBeanType;
   }
   
   protected abstract T getValue(Object receiver, CreationalContext<T> creationalContext);

   @Override
   public T create(CreationalContext<T> creationalContext)
   {
      try
      {
         Object receiver = getReceiver(creationalContext);
         T instance = getValue(receiver, creationalContext);
         Beans.checkReturnValue(instance, this, null, getBeanManager());
         return instance;
      }
      finally
      {
         if (getDeclaringBean().getScope().equals(Dependent.class))
         {
            creationalContext.release();
         }
      }
   }
   
   protected Object getReceiver(CreationalContext<T> creationalContext)
   {
      Bean<?> declaringBean = getDeclaringBean();
      return getBeanManager().getReference(declaringBean, declaringBean.getBeanClass(), creationalContext);
   }
   
   protected Bean<?> getDeclaringBean()
   {
      return getBeanManager().resolve(getBeanManager().getBeans(getDeclaringBeanType(), getDeclaringBeanQualifiers()));
   }

   @Override
   public Class<? extends Annotation> getScope()
   {
      if (scopeOverride == null)
      {
         return super.getScope();
      }
      else
      {
         return scopeOverride;
      }
   }

}