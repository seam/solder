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

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;

import org.jboss.weld.extensions.bean.ForwardingInjectionTarget;
import org.jboss.weld.extensions.util.Synthetic;
import org.jboss.weld.extensions.util.properties.Property;

/**
 * {@link InjectionTarget} wrapper used for beans that have generic producer fields
 * 
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 * 
 * @param <T>
 */
public class ProducerInjectionTarget<T> extends ForwardingInjectionTarget<T>
{
   private final InjectionTarget<T> delegate;
   
   private final BeanManager beanManager;
   private final List<Property<Object>> properties;
   private final Map<Member, Annotation> producers;
   private final Synthetic.Provider syntheticProvider;

   public ProducerInjectionTarget(InjectionTarget<T> delegate, BeanManager beanManager, List<Property<Object>> properties, Map<Member, Annotation> producers, Synthetic.Provider syntheticProvider)
   {
      this.delegate = delegate;
      this.beanManager = beanManager;
      this.properties = properties;
      this.producers = producers;
      this.syntheticProvider = syntheticProvider;
   }
   
   @Override
   protected InjectionTarget<T> delegate()
   {
      return delegate;
   }

   @Override
   public void inject(T instance, CreationalContext<T> ctx)
   {
      for (Property<Object> property: properties)
      {
         Synthetic qualifier = syntheticProvider.get(producers.get(property.getMember()));
         Bean<?> bean = beanManager.resolve(beanManager.getBeans(property.getBaseType(), qualifier));
         if (bean == null)
         {
            throw new UnsatisfiedResolutionException("Could not resolve bean for Generic Producer " + property.toString() + ". Type: " + property.getJavaClass() + " Qualifiers:" + qualifier);
         }
         Object value = beanManager.getReference(bean, property.getBaseType(), ctx);
         property.setValue(instance, value);
      }
      delegate().inject(instance, ctx);
   }

}
