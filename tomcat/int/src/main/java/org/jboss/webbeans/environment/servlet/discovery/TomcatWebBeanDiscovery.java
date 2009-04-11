/**
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
package org.jboss.webbeans.environment.servlet.discovery;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;

import org.jboss.webbeans.bootstrap.spi.WebBeanDiscovery;
import org.jboss.webbeans.environment.servlet.util.Reflections;
import org.jboss.webbeans.environment.servlet.util.Servlets;

/**
 * The means by which Web Beans are discovered on the classpath. This will only
 * discover simple web beans - there is no EJB/Servlet/JPA integration.
 * 
 * @author Peter Royle
 * @author Pete Muir
 * @author Ales Justin
 */
public abstract class TomcatWebBeanDiscovery implements WebBeanDiscovery
{
   
   private final Set<Class<?>> wbClasses;
   private final Set<URL> wbUrls;
   private final ServletContext servletContext;
   
   public TomcatWebBeanDiscovery(ServletContext servletContext)
   {
      this.wbClasses = new HashSet<Class<?>>();
      this.wbUrls = new HashSet<URL>();
      this.servletContext = servletContext;
      scan();
   }
   
   public Iterable<Class<?>> discoverWebBeanClasses()
   {
      return Collections.unmodifiableSet(wbClasses);
   }
   
   public Iterable<URL> discoverWebBeansXml()
   {
      return Collections.unmodifiableSet(wbUrls);
   }
   
   public Set<Class<?>> getWbClasses()
   {
      return wbClasses;
   }
   
   public Set<URL> getWbUrls()
   {
      return wbUrls;
   }
   
   private void scan()
   {
      Scanner scanner = new URLScanner(Reflections.getClassLoader(), this);
      scanner.scanResources(new String[] { "beans.xml" });
      try
      {
         URL beans = servletContext.getResource("/WEB-INF/beans.xml");
         if (beans != null)
         {
            File webInfClasses = Servlets.getRealFile(servletContext, "/WEB-INF/classes");
            if (webInfClasses != null)
            {
               File[] files = { webInfClasses };
               scanner.scanDirectories(files);
               wbUrls.add(beans);
            }
         }
      }
      catch (MalformedURLException e)
      {
         throw new IllegalStateException("Error loading resources from servlet context ", e);
      }
   }
   
}
