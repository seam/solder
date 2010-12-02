package org.jboss.seam.solder.resourceLoader.servlet;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Set;

import javax.servlet.ServletContext;

import org.jboss.seam.solder.resourceLoader.ResourceLoader;

/**
 * Implementation of ResourceLoader that can load from the servlet context. It
 * is not used directly but is called by DelegatingResourceLoader
 * 
 * @author stuart
 * 
 */
class ServletContextLoader implements ResourceLoader
{

   private final ServletContext context;

   ServletContextLoader(ServletContext context)
   {
      this.context = context;
   }

   public URL getResource(String resource)
   {
      if (!resource.startsWith("/"))
      {
         resource = "/" + resource;
      }
      try
      {
         return context.getResource(resource);
      }
      catch (MalformedURLException e)
      {
         throw new RuntimeException(e);
      }
   }

   public InputStream getResourceAsStream(String resource)
   {
      if (!resource.startsWith("/"))
      {
         resource = "/" + resource;
      }
      return context.getResourceAsStream(resource);
   }

   public Set<URL> getResources(String name)
   {
      URL resource = getResource(name);
      if (resource != null)
      {
         return singleton(resource);
      }
      return emptySet();
   }
   
   public Collection<InputStream> getResourcesAsStream(String name)
   {
      InputStream resource = getResourceAsStream(name);
      if (resource != null)
      {
         return singleton(resource);
      }
      else
      {
         return emptySet();
      }
   }
   /**
    * <p>
    * This loader has precedence 5, and so is searched after the classpath.
    * </p>
    * @return a precedence of 5
    */
   // NB This is currently not used due to the interference of DelegatingResourceLoader
   public int getPrecedence()
   {
      return 5;
   }

   @Override
   public String toString()
   {
      return getClass().getName() + " [" + context.getContextPath() + "]";
   }

}
