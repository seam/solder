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
package org.jboss.webbeans.environment.tomcat;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.webbeans.bootstrap.api.Bootstrap;
import org.jboss.webbeans.bootstrap.api.Environments;
import org.jboss.webbeans.bootstrap.spi.WebBeanDiscovery;
import org.jboss.webbeans.context.api.BeanStore;
import org.jboss.webbeans.context.api.helpers.ConcurrentHashMapBeanStore;
import org.jboss.webbeans.environment.tomcat.discovery.TomcatWebBeanDiscovery;
import org.jboss.webbeans.environment.tomcat.resources.ReadOnlyNamingContext;
import org.jboss.webbeans.environment.tomcat.util.Reflections;
import org.jboss.webbeans.resources.spi.NamingContext;
import org.jboss.webbeans.servlet.WebBeansListener;

/**
 * @author Pete Muir
 */
public class ServletListener extends WebBeansListener implements ServletContextListener
{
   
   private static final String BOOTSTRAP_IMPL_CLASS_NAME = "org.jboss.webbeans.bootstrap.WebBeansBootstrap";
   private static final String APPLICATION_BEAN_STORE_ATTRIBUTE_NAME = ServletListener.class.getName() + ".applicationBeanStore";
   
   private final transient Bootstrap bootstrap;
   
   public ServletListener() 
   {
      try
      {
         bootstrap = Reflections.newInstance(BOOTSTRAP_IMPL_CLASS_NAME, Bootstrap.class);
      }
      catch (Exception e)
      {
         throw new IllegalStateException("Error loading Web Beans bootstrap, check that Web Beans is on the classpath", e);
      }
   }

   public void contextDestroyed(ServletContextEvent sce)
   {
      bootstrap.shutdown();
   }

   public void contextInitialized(ServletContextEvent sce)
   {
      BeanStore applicationBeanStore = new ConcurrentHashMapBeanStore();
      sce.getServletContext().setAttribute(APPLICATION_BEAN_STORE_ATTRIBUTE_NAME, applicationBeanStore);
      bootstrap.setEnvironment(Environments.SE);
      bootstrap.getServices().add(WebBeanDiscovery.class, new TomcatWebBeanDiscovery() {});
      bootstrap.getServices().add(NamingContext.class, new ReadOnlyNamingContext() {});
      bootstrap.setApplicationContext(applicationBeanStore);
      bootstrap.initialize();
      bootstrap.boot();
   }
   
}
