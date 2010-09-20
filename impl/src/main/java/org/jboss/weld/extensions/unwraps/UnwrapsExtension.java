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
package org.jboss.weld.extensions.unwraps;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

/**
 * An extension that allows the use of {@link Unwraps} methods
 * 
 * 
 * @author Stuart Douglas
 * 
 */
public class UnwrapsExtension implements Extension
{

   final private Set<UnwrapsProducerBean<?>> beans = new HashSet<UnwrapsProducerBean<?>>();

   public void processAnnotatedType(@Observes ProcessAnnotatedType<?> type, BeanManager manager)
   {
      for (AnnotatedMethod<?> m : type.getAnnotatedType().getMethods())
      {
         if (m.isAnnotationPresent(Unwraps.class))
         {
            // we have a managed producer
            // lets make a note of it and register it later
            beans.add(new UnwrapsProducerBean(m, manager));
         }
      }
   }

   public void afterBeanDiscovery(@Observes AfterBeanDiscovery afterBean)
   {
      for (UnwrapsProducerBean<?> b : beans)
      {
         afterBean.addBean(b);
      }
   }

}
