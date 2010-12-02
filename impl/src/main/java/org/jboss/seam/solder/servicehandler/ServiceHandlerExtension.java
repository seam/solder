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
package org.jboss.seam.solder.servicehandler;

import static org.jboss.seam.solder.reflection.AnnotationInspector.getMetaAnnotation;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.jboss.seam.solder.bean.BeanBuilder;
import org.jboss.seam.solder.reflection.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This extension automatically implements interfaces and abstract classes.
 * 
 * @author Stuart Douglas
 * 
 */
public class ServiceHandlerExtension implements Extension
{
   private final Set<Bean<?>> beans = new HashSet<Bean<?>>();
   
   private final static Logger log = LoggerFactory.getLogger(ServiceHandlerExtension.class);

   private final boolean enabled;

   private final Set<Throwable> problems = new HashSet<Throwable>();

   public ServiceHandlerExtension()
   {
      boolean en = true;
      try
      {
         Reflections.classForName("javassist.util.proxy.MethodHandler", ServiceHandlerExtension.class.getClassLoader());
      }
      catch (ClassNotFoundException e)
      {
         en = false;
         log.debug("Javassist not preset, @ServiceHandler is disabled");
      }
      enabled = en;
   }

   <X> void processAnnotatedType(@Observes ProcessAnnotatedType<X> event, BeanManager beanManager)
   {
      ServiceHandler annotation = getMetaAnnotation(event.getAnnotatedType(), ServiceHandler.class);
      if (annotation != null)
      {
         if (!enabled)
         {
            problems.add(new RuntimeException("Javassist not found on the class path, @ServiceHandler requires javassist to work. @ServiceHandler found on " + event.getAnnotatedType()));
         }
         else
         {
            Class<?> handlerClass = annotation.value();
            try
            {
               BeanBuilder<X> builder = new BeanBuilder<X>(beanManager);
               builder.readFromType(event.getAnnotatedType());
               builder.beanLifecycle(new ServiceHandlerBeanLifecycle(event.getAnnotatedType().getJavaClass(), handlerClass, beanManager));
               builder.toString("Generated @ServiceHandler for [" + builder.getBeanClass() + "] with qualifiers [" + builder.getQualifiers() + "] handled by " + handlerClass);
               beans.add(builder.create());
               log.debug("Adding @ServiceHandler bean for [" + builder.getBeanClass() + "] with qualifiers [" + builder.getQualifiers() + "] handled by " + handlerClass);
            }
            catch (IllegalArgumentException e)
            {
               throw new RuntimeException(e);
            }
         }
      }
   }

   void afterBeanDiscovery(@Observes AfterBeanDiscovery event)
   {
      for (Bean<?> bean : beans)
      {
         event.addBean(bean);
      }
      for (Throwable e : problems)
      {
         event.addDefinitionError(e);
      }
      beans.clear();
   }
}
