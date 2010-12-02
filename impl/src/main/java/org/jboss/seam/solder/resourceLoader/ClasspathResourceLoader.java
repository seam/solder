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
package org.jboss.seam.solder.resourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ClasspathResourceLoader implements ResourceLoader
{

   private static final Logger log = LoggerFactory.getLogger("org.jboss.seam.solder.resources");
   
   ClasspathResourceLoader()
   {
   }

   public InputStream getResourceAsStream(String name)
   {
      // Always use the strippedName, classloader always assumes no starting /
      String strippedName = getStrippedName(name);
      // Try to load from the TCCL
      if (Thread.currentThread().getContextClassLoader() != null)
      {
         InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(strippedName);
         if (stream != null)
         {
            log.trace("Loaded resource from context classloader: " + strippedName);
            return stream;
         }
      }
      // Try to load from the extension's classloader
      else
      {
         InputStream stream = ResourceProducer.class.getResourceAsStream(strippedName);
         if (stream != null)
         {
            log.trace("Loaded resource from Seam classloader: " + strippedName);
            return stream;
         }
      }
      return null;
   }

   public URL getResource(String name)
   {
      // Always use the strippedName, classloader always assumes no starting /
      String strippedName = getStrippedName(name);
      // Try to load from the TCCL
      if (Thread.currentThread().getContextClassLoader() != null)
      {
         URL url = Thread.currentThread().getContextClassLoader().getResource(strippedName);
         if (url != null)
         {
            log.trace("Loaded resource from context classloader: " + strippedName);
            return url;
         }
      }
      // Try to load from the extension's classloader
      else
      {
         URL url = ResourceProducer.class.getResource(strippedName);
         if (url != null)
         {
            log.trace("Loaded resource from Seam classloader: " + strippedName);
            return url;
         }
      }
      return null;
   }
   

   public Set<URL> getResources(String name)
   {
      Set<URL> urls = new HashSet<URL>();
      // Always use the strippedName, classloader always assumes no starting /
      String strippedName = getStrippedName(name);
      // Try to load from the TCCL
      if (Thread.currentThread().getContextClassLoader() != null)
      {
         try
         {
            Enumeration<URL> urlEnum = Thread.currentThread().getContextClassLoader().getResources(strippedName);
            while (urlEnum.hasMoreElements())
            {
               urls.add(urlEnum.nextElement());
            }
         }
         catch (IOException e)
         {
            // we are probably not going to recover from an IOException
            throw new RuntimeException(e);
         }
      }
      // Try to load from the extension's classloader
      else
      {
         try
         {
            Enumeration<URL> urlEnum = ResourceProducer.class.getClassLoader().getResources(strippedName);
            while (urlEnum.hasMoreElements())
            {
               urls.add(urlEnum.nextElement());
            }
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
      return urls;
   }
   
   public Collection<InputStream> getResourcesAsStream(String name)
   {
      Set<InputStream> resources = new HashSet<InputStream>();
      // Always use the strippedName, classloader always assumes no starting /
      String strippedName = getStrippedName(name);
      // Try to load from the TCCL
      if (Thread.currentThread().getContextClassLoader() != null)
      {
         try
         {
            Enumeration<URL> urlEnum = Thread.currentThread().getContextClassLoader().getResources(strippedName);
            while (urlEnum.hasMoreElements())
            {
               resources.add(urlEnum.nextElement().openStream());
            }
         }
         catch (IOException e)
         {
            // we are probably not going to recover from an IOException
            throw new RuntimeException(e);
         }
      }
      // Try to load from the extension's classloader
      else
      {
         try
         {
            Enumeration<URL> urlEnum = ResourceProducer.class.getClassLoader().getResources(strippedName);
            while (urlEnum.hasMoreElements())
            {
               resources.add(urlEnum.nextElement().openStream());
            }
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
      return resources;
   }
   
   public int getPrecedence()
   {
      return 10;
   }

   private static String getStrippedName(String name)
   {
      return name.startsWith("/") ? name.substring(1) : name;
   }

}
