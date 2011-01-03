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
package org.jboss.seam.solder.serviceHandler;

import org.jboss.seam.solder.reflection.AnnotationInspector;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.jboss.logging.Logger;
import org.jboss.seam.solder.bean.BeanBuilder;
import org.jboss.seam.solder.reflection.Reflections;
import org.jboss.seam.solder.servicehandler.ServiceHandler;

/**
 * This extension automatically implements interfaces and abstract classes.
 * 
 * @author Stuart Douglas
 * @author Walter White
 */
public class ServiceHandlerExtension implements Extension
{
   private static final Logger log = Logger.getLogger(ServiceHandlerExtension.class);

   protected final Set<Bean<?>> beans = new HashSet<Bean<?>>();

   protected final boolean enabled;

   protected final Set<Throwable> problems = new HashSet<Throwable>();

   public ServiceHandlerExtension()
   {
      enabled = isEnabled();
   }

   /**
    * Determines if proxying is enabled.
    */
   protected boolean isEnabled()
   {
      try
      {
         Reflections.classForName("javassist.util.proxy.MethodHandler", ServiceHandlerExtension.class.getClassLoader());
         return (true);
      }
      catch (ClassNotFoundException e)
      {
         log.debug("Javassist not preset, @ServiceHandler is disabled");
      }

      return (false);
   }

   /**
    * Processes the event and builds a bean to proxy the interface or abstract
    * class.
    * 
    * @param event the process annotated type event.
    * @param beanManager the bean manager.
    */
   <X> void processAnnotatedType(@Observes ProcessAnnotatedType<X> event, BeanManager beanManager)
   {
      if (!enabled)
      {
         problems.add(new RuntimeException("Javassist not found on the class path, @ServiceHandler requires javassist to work. @ServiceHandler found on " + event.getAnnotatedType()));
      }
      else
      {
         final Class<?> handlerClass = getHandlerClass(event);
         buildBean(event, beanManager, handlerClass);
      }
   }

   /**
    * Gets the handler type either from the ServiceHandlerType annotation or the
    * ServiceHandler annotation.
    * 
    * @param event the process annotated type event.
    */
   protected <X> Class<?> getHandlerClass(ProcessAnnotatedType<X> event)
   {
      final ServiceHandlerType annotation = AnnotationInspector.getMetaAnnotation(event.getAnnotatedType(), ServiceHandlerType.class);
      if (annotation != null)
      {
         return (annotation.value());
      }

      ServiceHandler deprecatedAnnotation = deprecatedAnnotation = AnnotationInspector.getMetaAnnotation(event.getAnnotatedType(), ServiceHandler.class);

      log.info(event.getAnnotatedType().getJavaClass().getName() + " inherits deprecated @ServiceHandler meta-annotation. Please use @org.jboss.seam.solder.serviceHandler.ServiceHandlerType instead.");
      return (deprecatedAnnotation.value());
   }

   /**
    * Builds the bean that will do the intercepting / proxying.
    * 
    * @param event the process annotated type event.
    * @param beanManager the bean manager.
    * @param handler class, the class that will be proxying the interfaces.
    */
   protected <X> void buildBean(ProcessAnnotatedType<X> event, BeanManager beanManager, final Class<?> handlerClass)
   {
      try
      {
         final BeanBuilder<X> builder = new BeanBuilder<X>(beanManager);
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
