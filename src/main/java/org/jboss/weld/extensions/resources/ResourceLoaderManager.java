package org.jboss.weld.extensions.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.weld.extensions.util.Sortable;
import org.jboss.weld.extensions.util.service.ServiceLoader;

@ApplicationScoped
class ResourceLoaderManager
{
   
   private final List<ResourceLoader> resourceLoaders;
   
   ResourceLoaderManager()
   {
      this.resourceLoaders = new ArrayList<ResourceLoader>();
   }
   
   @PostConstruct
   void init()
   {
      for (ResourceLoader resourceLoader : ServiceLoader.load(ResourceLoader.class))
      {
         resourceLoaders.add(resourceLoader);
      }
      Collections.sort(resourceLoaders, new Sortable.Comparator());
   }
   
   public Iterable<ResourceLoader> getResourceLoaders()
   {
      return resourceLoaders;
   }

}
