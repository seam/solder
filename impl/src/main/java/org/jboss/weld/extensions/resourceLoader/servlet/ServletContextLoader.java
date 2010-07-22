package org.jboss.weld.extensions.resourceLoader.servlet;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;

import javax.servlet.ServletContext;

import org.jboss.weld.extensions.resourceLoader.ResourceLoader;

/**
 * Implementation of ResourceLoader that can load from the servlet context. It
 * is not used directly but is called by DelegatingResourceLoader
 * 
 * @author stuart
 * 
 */
public class ServletContextLoader implements ResourceLoader
{

   private final ServletContext context;

   public ServletContextLoader(ServletContext context)
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
      URL r = getResource(name);
      if (r != null)
      {
         return Collections.singleton(r);
      }
      return Collections.emptySet();
   }

   // not used
   public int getPrecedence()
   {
      return 0;
   }

   @Override
   public String toString()
   {
      return getClass().getName() + " [" + context.getContextPath() + "]";
   }

}
