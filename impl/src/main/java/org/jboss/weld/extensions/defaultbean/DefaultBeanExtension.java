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

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;

import org.jboss.weld.extensions.bean.BeanBuilder;
import org.jboss.weld.extensions.literal.DefaultLiteral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registers beans annotated @DefaultBean
 * 
 * @author Stuart Douglas
 * 
 */
public class DefaultBeanExtension implements Extension
{

   private static final Logger log = LoggerFactory.getLogger(DefaultBeanExtension.class);

   private final Set<DefaultBeanDefinition> defaultBeans;

   private boolean beanDiscoveryOver = false;

   private final List<Bean<?>> processedBeans;
   
   DefaultBeanExtension()
   {
      this.defaultBeans = new HashSet<DefaultBeanDefinition>();
      this.processedBeans = new LinkedList<Bean<?>>();
   }

   /**
    * Adds a default bean with the {@link Default} qualifier
    */
   private void addDefaultBean(Class<?> type, Bean<?> bean)
   {
      defaultBeans.add(new DefaultBeanDefinition(type, Collections.singleton(DefaultLiteral.INSTANCE), bean));
   }

   <X> void processAnnotatedType(@Observes ProcessAnnotatedType<X> event, BeanManager beanManager)
   {
      if (event.getAnnotatedType().isAnnotationPresent(DefaultBean.class))
      {
         DefaultBean annotation = event.getAnnotatedType().getAnnotation(DefaultBean.class);
         event.veto();
         BeanBuilder<X> builder = new BeanBuilder<X>(beanManager);
         builder.defineBeanFromAnnotatedType(event.getAnnotatedType());
         builder.setTypes(Collections.<Type>singleton(annotation.type()));
         builder.setToString("@DefaultBean of type [" + annotation.type() + "] with qualifiers [" + builder.getQualifiers() + "]");
         addDefaultBean(annotation.type(), builder.create());
      }
   }

   void processBean(@Observes ProcessBean<?> event)
   {
      if (beanDiscoveryOver)
      {
         return;
      }
      processedBeans.add(event.getBean());
   }

   void afterBeanDiscovery(@Observes AfterBeanDiscovery event)
   {
      beanDiscoveryOver = true;
      if (defaultBeans.size() > 0)
      {
         for (Bean<?> processedBean : processedBeans)
         {
            Iterator<DefaultBeanDefinition> it = defaultBeans.iterator();
            while (it.hasNext())
            {
               DefaultBeanDefinition definition = it.next();
               if (definition.matches(processedBean))
               {
                  log.debug("Preventing install of default bean " + definition.getDefaultBean());
                  it.remove();
               }
            }
         }
      }
      
      for (DefaultBeanDefinition defaultBean : defaultBeans)
      {
         log.debug("Installing default bean " + defaultBean.getDefaultBean());
         event.addBean(defaultBean.getDefaultBean());
      }
   }
   
   void afterDeploymentValidation(@Observes AfterDeploymentValidation event)
   {
      this.defaultBeans.clear();
      this.processedBeans.clear();
   }

}
