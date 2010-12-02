package org.jboss.seam.solder.resourceLoader;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource loader that delegates to a static list of resource loaders.
 * 
 * @author Stuart Douglas
 * @deprecated this resource loader can easily leak between application instances
 */
@Deprecated
public class DelegatingResourceLoader implements ResourceLoader
{
   private static final Logger log = LoggerFactory.getLogger("org.jboss.seam.solder.resources");

   // TODO: get rid of the static
   private static final List<ResourceLoader> resourceLoaders = new ArrayList<ResourceLoader>();

   public static void addResourceLoader(ResourceLoader loader)
   {
      resourceLoaders.add(loader);
   }

   public URL getResource(String name)
   {
      for (ResourceLoader loader : resourceLoaders)
      {
         URL resource = loader.getResource(name);
         if (resource != null)
         {
            log.trace("Loaded resource " + name + " from " + loader.toString());
            return resource;
         }
      }
      return null;
   }

   public InputStream getResourceAsStream(String name)
   {
      for (ResourceLoader loader : resourceLoaders)
      {
         InputStream resource = loader.getResourceAsStream(name);
         if (resource != null)
         {
            log.trace("Loaded resource " + name + " from " + loader.toString());
            return resource;
         }
      }
      return null;
   }

   public Set<URL> getResources(String name)
   {
      Set<URL> resources = new HashSet<URL>();
      for (ResourceLoader loader : resourceLoaders)
      {
         resources.addAll(loader.getResources(name));
      }
      return resources;
   }
   
   public Collection<InputStream> getResourcesAsStream(String name)
   {
      Set<InputStream> resources = new HashSet<InputStream>();
      for (ResourceLoader loader : resourceLoaders)
      {
         resources.addAll(loader.getResourcesAsStream(name));
      }
      return resources;
   }

   public int getPrecedence()
   {
      return 5;
   }

}
