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
package org.jboss.seam.solder.unwraps;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.jboss.seam.solder.reflection.Reflections;
import org.jboss.seam.solder.servicehandler.ServiceHandlerExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An extension that allows the use of {@link Unwraps} methods
 * 
 * 
 * @author Stuart Douglas
 * 
 */
public class UnwrapsExtension implements Extension
{

   private final Set<Bean<?>> beans;

   private final static Logger log = LoggerFactory.getLogger(UnwrapsExtension.class);

   private final boolean enabled;

   private final Set<Throwable> problems = new HashSet<Throwable>();

   public UnwrapsExtension()
   {
      this.beans = new HashSet<Bean<?>>();
      boolean en = true;
      try
      {
         Reflections.classForName("javassist.util.proxy.MethodHandler", ServiceHandlerExtension.class.getClassLoader());
      }
      catch (ClassNotFoundException e)
      {
         en = false;
         log.debug("Javassist not preset, @Unwraps is disabled");
      }
      enabled = en;
   }

   void processAnnotatedType(@Observes ProcessAnnotatedType<?> type, BeanManager beanManager)
   {
      for (AnnotatedMethod<?> method : type.getAnnotatedType().getMethods())
      {
         if (method.isAnnotationPresent(Unwraps.class))
         {
            if (!enabled)
            {
               problems.add(new RuntimeException("Javassist not found on the class path, @Unwraps requires javassist to work. @Unwraps found on " + type.getAnnotatedType().getJavaClass().getName() + "." + method.getJavaMember().getName()));
            }
            else
            {
               // we have a managed producer
               // lets make a note of it and register it later
               beans.add(createBean(method, beanManager));
            }
         }
      }
   }

   private static <X> Bean<X> createBean(AnnotatedMethod<X> method, BeanManager beanManager)
   {
      return new UnwrapsProducerBean<X>(method, beanManager);
   }

   void afterBeanDiscovery(@Observes AfterBeanDiscovery afterBean)
   {
      for (Bean<?> b : beans)
      {
         afterBean.addBean(b);
      }
      for (Throwable e : problems)
      {
         afterBean.addDefinitionError(e);
      }
   }

}
