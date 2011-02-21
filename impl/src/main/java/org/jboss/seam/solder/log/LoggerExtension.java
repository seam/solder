/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.seam.solder.log;

import java.util.Collection;
import java.util.HashSet;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessProducerMethod;

import org.jboss.seam.solder.bean.NarrowingBeanBuilder;
import org.jboss.seam.solder.literal.MessageBundleLiteral;
import org.jboss.seam.solder.logging.MessageBundle;
import org.jboss.seam.solder.logging.MessageLogger;

/**
 * Adds LoggerProducers to the deployment, and detects and installs beans for any
 * typed loggers defined.
 * 
 * @author Pete Muir
 */
public class LoggerExtension implements Extension
{
   static
   {
      // We now have tooling, so this isn't necessary. We need to:
      // 1. document this setting as a development-time convenience
      // 2. remove this global assignment
      System.setProperty("jboss.i18n.generate-proxies", "true");
   }

   private final Collection<AnnotatedType<?>> messageLoggerTypes;
   private final Collection<AnnotatedType<?>> messageBundleTypes;
   private Bean<Object> loggerProducerBean;
   private Bean<Object> bundleProducerBean;

   LoggerExtension()
   {
      this.messageLoggerTypes = new HashSet<AnnotatedType<?>>();
      this.messageBundleTypes = new HashSet<AnnotatedType<?>>();
   }

   void detectInterfaces(@Observes ProcessAnnotatedType<?> event, BeanManager beanManager)
   {
      AnnotatedType<?> type = event.getAnnotatedType();
      if (type.isAnnotationPresent(MessageLogger.class))
      {
         messageLoggerTypes.add(type);
      }
      if (type.isAnnotationPresent(MessageBundle.class))
      {
         messageBundleTypes.add(type);
      }
   }
   
   // according to the Java EE 6 javadoc (the authority according to the powers that be),
   // this is the correct order of type parameters
   void detectProducers(@Observes ProcessProducerMethod<Object, LoggerProducers> event)
   {
      captureProducers(event.getAnnotatedProducerMethod(), event.getBean());
   }

   // according to JSR-299 spec, this is the correct order of type parameters
   @Deprecated
   void detectProducersInverted(@Observes ProcessProducerMethod<LoggerProducers, Object> event)
   {
      captureProducers(event.getAnnotatedProducerMethod(), event.getBean());
   }
   
   @SuppressWarnings("unchecked")
   void captureProducers(AnnotatedMethod<?> method, Bean<?> bean)
   {
      if (method.isAnnotationPresent(TypedLogger.class))
      {
         this.loggerProducerBean = (Bean<Object>) bean;
      }
      if (method.isAnnotationPresent(TypedMessageBundle.class))
      {
         this.bundleProducerBean = (Bean<Object>) bean;
      }
   }

   void installBeans(@Observes AfterBeanDiscovery event, BeanManager beanManager)
   {
      for (AnnotatedType<?> type : messageLoggerTypes)
      {
         event.addBean(createMessageLoggerBean(loggerProducerBean, type, beanManager));
      }
      for (AnnotatedType<?> type : messageBundleTypes)
      {
         event.addBean(createMessageBundleBean(bundleProducerBean, type, beanManager));
      }
   }
   
   private static <T> Bean<T> createMessageLoggerBean(Bean<Object> delegate, AnnotatedType<T> type, BeanManager beanManager)
   {
      return new NarrowingBeanBuilder<T>(delegate, beanManager).readFromType(type).types(type.getBaseType(), Object.class).create();
   }
   
   private static <T> Bean<T> createMessageBundleBean(Bean<Object> delegate, AnnotatedType<T> type, BeanManager beanManager)
   {
      return new NarrowingBeanBuilder<T>(delegate, beanManager).readFromType(type).types(type.getBaseType(), Object.class).addQualifier(MessageBundleLiteral.INSTANCE).create();
   }
   
   void cleanup(@Observes AfterDeploymentValidation event)
   {
      // defensively clear the set to help with gc
      this.messageLoggerTypes.clear();
      this.messageBundleTypes.clear();
   }

}
