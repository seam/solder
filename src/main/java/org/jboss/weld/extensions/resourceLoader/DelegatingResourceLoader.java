package org.jboss.weld.extensions.resourceLoader;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource loader that delegates to a static list of resource loaders.
 * 
 * @author Stuart Douglas
 * 
 */
public class DelegatingResourceLoader implements ResourceLoader
{
   private static final Logger log = LoggerFactory.getLogger("org.jboss.weld.extensions.resources");

   // TODO: get rid of the static
   private static final List<ResourceLoader> resourceLoaders = new ArrayList<ResourceLoader>();

   public static void addResourceLoader(ResourceLoader loader)
   {
      resourceLoaders.add(loader);
   }

   public URL getResource(String resource)
   {
      for (ResourceLoader r : resourceLoaders)
      {
         URL res = r.getResource(resource);
         if (res != null)
         {
            log.trace("Loaded resource " + resource + " from " + r.toString());
            return res;
         }
      }
      return null;
   }

   public InputStream getResourceAsStream(String resource)
   {
      for (ResourceLoader r : resourceLoaders)
      {
         InputStream res = r.getResourceAsStream(resource);
         if (res != null)
         {
            log.trace("Loaded resource " + resource + " from " + r.toString());
            return res;
         }
      }
      return null;
   }

   public Set<URL> getResources(String name)
   {
      Set<URL> ret = new HashSet<URL>();
      for (ResourceLoader r : resourceLoaders)
      {
         ret.addAll(r.getResources(name));
      }
      return ret;
   }

   public int getPrecedence()
   {
      return 5;
   }

}
