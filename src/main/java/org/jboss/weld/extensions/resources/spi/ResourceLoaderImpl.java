package org.jboss.weld.extensions.resources.spi;

import java.io.InputStream;
import java.net.URL;

import org.jboss.weld.extensions.resources.ResourceProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceLoaderImpl implements ResourceLoader
{
   
   private static final Logger log = LoggerFactory.getLogger("org.jboss.weld.extensions.resources");
   
   private ResourceLoaderImpl() {}

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
   
   private static String getStrippedName(String name)
   {
      return name.startsWith("/") ? name.substring(1) : name;
   }

}
