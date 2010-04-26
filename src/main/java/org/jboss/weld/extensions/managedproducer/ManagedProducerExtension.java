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
package org.jboss.weld.extensions.managedproducer;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.jboss.weld.extensions.annotations.ManagedProducer;

/**
 * An extension that allows the use of @ManagedProducer methods
 * 
 * these methods work in a similar manner to @Unwrap methods in seam 2
 * 
 * @author stuart
 * 
 */
public class ManagedProducerExtension implements Extension
{

   Set<ManagedProducerBean<?>> beans = new HashSet<ManagedProducerBean<?>>();

   public void processAnnotatedType(@Observes ProcessAnnotatedType<?> type, BeanManager manager)
   {
      for (AnnotatedMethod<?> m : type.getAnnotatedType().getMethods())
      {
         if (m.isAnnotationPresent(ManagedProducer.class))
         {
            // we have a managed producer
            // lets make a not of it and register it later

            beans.add(new ManagedProducerBean(m, manager));
         }
      }
   }

   public void afterBeanDiscovery(@Observes AfterBeanDiscovery afterBean)
   {
      for (ManagedProducerBean<?> b : beans)
      {
         afterBean.addBean(b);
      }
   }

}
