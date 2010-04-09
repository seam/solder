package org.jboss.weld.extensions.resources.spi;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.jboss.weld.extensions.resources.ResourceProducer;
import org.jboss.weld.extensions.resources.servlet.ServletResourceExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceLoaderImpl implements ResourceLoader
{

   private final Set<ServletContext> servletContexts;

   private static final Logger log = LoggerFactory.getLogger("org.jboss.weld.extensions.resources");
   
   @Inject
   private ResourceLoaderImpl(ServletResourceExtension extension)
   {
      servletContexts = extension.getServletContexts();
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
      String slashedName = getSlashedName(name);
      for (ServletContext context : servletContexts)
      {
         InputStream stream = context.getResourceAsStream(slashedName);
         if (stream != null)
         {
            log.trace("Loaded resource from ServletContext: " + slashedName);
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
      String slashedName = getSlashedName(name);
      // Try to load from the ServletContext
      for (ServletContext context : servletContexts)
      {
         try
         {
            URL url = context.getResource(slashedName);
            if (url != null)
            {
               log.trace("Loaded resource from ServletContext: " + slashedName);
               return url;
            }
         }
         catch (MalformedURLException e)
         {
            log.error("Malformed URL loading " + name, e);
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
      String slashedName = getSlashedName(name);
      for (ServletContext context : servletContexts)
      {
         try
         {
            URL url = context.getResource(slashedName);
            if (url != null)
            {
               urls.add(url);
            }
         }
         catch (MalformedURLException e)
         {
            log.error("Malformed URL loading " + name, e);
         }
      }
      return urls;
   }

   private static String getStrippedName(String name)
   {
      return name.startsWith("/") ? name.substring(1) : name;
   }

   private static String getSlashedName(String name)
   {
      return name.startsWith("/") ? name : "/" + name;
   }

}
