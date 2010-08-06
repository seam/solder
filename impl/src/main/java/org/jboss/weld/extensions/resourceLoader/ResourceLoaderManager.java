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
package org.jboss.weld.extensions.resourceLoader;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.weld.extensions.util.Sortable;
import org.jboss.weld.extensions.util.service.ServiceLoader;

/**
 * Class that is resposible for loading {@link ResourceProvider} implements from
 * the servive loader and using them to load resources
 * 
 * @author pmuir
 * @author Stuart Douglas
 * 
 */
public class ResourceLoaderManager
{

   private final List<ResourceLoader> resourceLoaders;

   public ResourceLoaderManager()
   {
      resourceLoaders = new ArrayList<ResourceLoader>();
      for (ResourceLoader resourceLoader : ServiceLoader.load(ResourceLoader.class))
      {
         resourceLoaders.add(resourceLoader);
      }
      Collections.sort(resourceLoaders, new Sortable.Comparator());
   }

   public Iterable<ResourceLoader> getResourceLoaders()
   {
      return Collections.unmodifiableList(resourceLoaders);
   }

   public URL getResource(String resource)
   {
      for (ResourceLoader loader : resourceLoaders)
      {
         URL url = loader.getResource(resource);
         if (url != null)
         {
            return url;
         }
      }
      return null;
   }

   public InputStream getResourceAsStream(String name)
   {
      for (ResourceLoader loader : resourceLoaders)
      {
         InputStream is = loader.getResourceAsStream(name);
         if (is != null)
         {
            return is;
         }
      }
      return null;
   }

   public Set<URL> getResources(String name)
   {
      Set<URL> urls = new HashSet<URL>();
      for (ResourceLoader loader : resourceLoaders)
      {
         urls.addAll(loader.getResources(name));
      }
      return urls;
   }

}
