/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.webbeans.environment.servlet;

import javax.servlet.ServletContextEvent;

import org.jboss.webbeans.bootstrap.api.Bootstrap;
import org.jboss.webbeans.bootstrap.api.Environments;
import org.jboss.webbeans.bootstrap.spi.WebBeanDiscovery;
import org.jboss.webbeans.context.api.BeanStore;
import org.jboss.webbeans.context.api.helpers.ConcurrentHashMapBeanStore;
import org.jboss.webbeans.environment.servlet.discovery.TomcatWebBeanDiscovery;
import org.jboss.webbeans.environment.servlet.resources.TomcatResourceServices;
import org.jboss.webbeans.environment.servlet.util.Reflections;
import org.jboss.webbeans.environment.tomcat.WebBeansAnnotationProcessor;
import org.jboss.webbeans.log.Log;
import org.jboss.webbeans.log.Logging;
import org.jboss.webbeans.manager.api.WebBeansManager;
import org.jboss.webbeans.resources.spi.ResourceServices;
import org.jboss.webbeans.servlet.api.ServletListener;
import org.jboss.webbeans.servlet.api.helpers.ForwardingServletListener;

/**
 * @author Pete Muir
 */
public class Listener extends ForwardingServletListener
{
   
   private static final Log log = Logging.getLog(Listener.class);
   
   private static final String BOOTSTRAP_IMPL_CLASS_NAME = "org.jboss.webbeans.bootstrap.WebBeansBootstrap";
   private static final String WEB_BEANS_LISTENER_CLASS_NAME = "org.jboss.webbeans.servlet.WebBeansListener";
   private static final String APPLICATION_BEAN_STORE_ATTRIBUTE_NAME = Listener.class.getName() + ".applicationBeanStore";
   
   private final transient Bootstrap bootstrap;
   private final transient ServletListener webBeansListener;
   private WebBeansManager manager;
   
   public Listener() 
   {
      try
      {
         bootstrap = Reflections.newInstance(BOOTSTRAP_IMPL_CLASS_NAME, Bootstrap.class);
      }
      catch (Exception e)
      {
         throw new IllegalStateException("Error loading Web Beans bootstrap, check that Web Beans is on the classpath", e);
      }
      try
      {
         webBeansListener = Reflections.newInstance(WEB_BEANS_LISTENER_CLASS_NAME, ServletListener.class);
      }
      catch (Exception e)
      {
         throw new IllegalStateException("Error loading Web Beans listener, check that Web Beans is on the classpath", e);
      }
   }

   @Override
   public void contextDestroyed(ServletContextEvent sce)
   {
      manager.shutdown();
      super.contextDestroyed(sce);
   }

   @Override
   public void contextInitialized(ServletContextEvent sce)
   {
      BeanStore applicationBeanStore = new ConcurrentHashMapBeanStore();
      sce.getServletContext().setAttribute(APPLICATION_BEAN_STORE_ATTRIBUTE_NAME, applicationBeanStore);
      bootstrap.setEnvironment(Environments.SERVLET);
      bootstrap.getServices().add(WebBeanDiscovery.class, new TomcatWebBeanDiscovery(sce.getServletContext()) {});
      try
      {
    	  bootstrap.getServices().add(ResourceServices.class, new TomcatResourceServices() {});
      }
      catch (NoClassDefFoundError e)
      {
    	 // Support GAE 
    	 log.warn("@Resource injection not available in simple beans");
      }
      bootstrap.setApplicationContext(applicationBeanStore);
      bootstrap.initialize();
      manager = bootstrap.getManager();
      
      boolean tomcat = true;
      try
      {
         Reflections.loadClass("org.apache.AnnotationProcessor", Object.class);
      }
      catch (ClassNotFoundException e) 
      {
         log.info("JSR-299 injection will not be available in Servlets, Filters etc. This facility is only available in Tomcat");
         tomcat = false;
      }
      catch (NoClassDefFoundError e) 
      {
         log.info("JSR-299 injection will not be available in Servlets, Filters etc. This facility is only available in Tomcat");
         tomcat = false;
      }
      
      if (tomcat)
      {
         // Try pushing a Tomcat AnnotationProcessor into the servlet context
         try
         {
            Class<?> clazz = Reflections.loadClass(WebBeansAnnotationProcessor.class.getName(), Object.class);
            Object annotationProcessor = clazz.getConstructor(WebBeansManager.class).newInstance(manager);
            sce.getServletContext().setAttribute(WebBeansAnnotationProcessor.class.getName(), annotationProcessor);
         }
         catch (Exception e) 
         {
            log.error("Unable to create Tomcat AnnotationProcessor. JSR-299 injection will not be available in Servlets, Filters etc.", e);
         }
      }

      
      bootstrap.boot();
      super.contextInitialized(sce);
   }
   
   

   @Override
   protected ServletListener delegate()
   {
      return webBeansListener;
   }
   
}
