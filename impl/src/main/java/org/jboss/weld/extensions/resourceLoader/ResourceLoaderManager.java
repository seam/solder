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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.weld.extensions.util.Sortable;
import org.jboss.weld.extensions.util.service.ServiceLoader;

/**
 * <p>
 * {@link ResourceLoaderManager} discovers and instantiates all
 * {@link ResourceLoader}s defined. It also provides accesss to these resources,
 * either as {@link URL}s or {@link InputStream}s.
 * </p>
 * 
 * <p>
 * If you are working in a CDI managed environment, you should use
 * {@link ResourceProvider} instead, as it provides automatic, contextual
 * management of resources. If you are outside a CDI managed environment, then
 * instantiating {@link ResourceLoaderManager} provides access to the same
 * resources.
 * </p>
 * 
 * @author Pete Muir
 * @author Stuart Douglas
 * 
 * @see ResourceLoader
 * @see ResourceProvider
 */
public class ResourceLoaderManager
{

   private final List<ResourceLoader> resourceLoaders;

   /**
    * Instantiate a new instance, loading any resource loaders from the service
    * loader, and sorting them by precedence.
    */
   public ResourceLoaderManager()
   {
      resourceLoaders = new ArrayList<ResourceLoader>();
      for (ResourceLoader resourceLoader : ServiceLoader.load(ResourceLoader.class))
      {
         resourceLoaders.add(resourceLoader);
      }
      Collections.sort(resourceLoaders, new Sortable.Comparator());
   }

   /**
    * The discovered {@link ResourceLoader} instances.
    * 
    * @return the resource loaders
    */
   public Iterable<ResourceLoader> getResourceLoaders()
   {
      return Collections.unmodifiableList(resourceLoaders);
   }

   /**
    * <p>
    * Load a resource by name.
    * </p>
    * 
    * <p>
    * The resource loaders will be searched in precedence order, the first
    * result found being returned.
    * </p>
    * 
    * @param name the resource to load
    * @return a URL pointing to the resource, or <code>null</code> if no
    *         resource can be loaded
    * @throws RuntimeException if an error occurs loading the resource
    */
   public URL getResource(String name)
   {
      for (ResourceLoader loader : resourceLoaders)
      {
         URL url = loader.getResource(name);
         if (url != null)
         {
            return url;
         }
      }
      return null;
   }

   /**
    * <p>
    * Load a resource by name.
    * </p>
    * 
    * <p>
    * The resource loaders will be searched in precedence order, the first
    * result found being returned.
    * </p>
    * 
    * @param name the resource to load
    * @return an InputStream providing access to the resource, or
    *         <code>null</code> if no resource can be loaded
    * @throws RuntimeException if an error occurs loading the resource
    */
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

   /**
    * <p>
    * Load all resources known to the resource loader by name.
    * </p>
    * 
    * @param name the resource to load
    * @return a collection of URLs pointing to the resources, or an empty
    *         collection if no resources are found
    * @throws RuntimeException if an error occurs loading the resource
    */
   public Collection<URL> getResources(String name)
   {
      Set<URL> urls = new HashSet<URL>();
      for (ResourceLoader loader : resourceLoaders)
      {
         urls.addAll(loader.getResources(name));
      }
      return urls;
   }

   /**
    * <p>
    * Load all resources known to the resource loader by name.
    * </p>
    * 
    * @param name the resource to load
    * @return a collection of input streams pointing to the resources, or an
    *         empty collection if no resources are found
    * @throws RuntimeException if an error occurs loading the resource
    */
   public Collection<InputStream> getResourcesAsStream(String name)
   {
      Set<InputStream> streams = new HashSet<InputStream>();
      for (ResourceLoader loader : resourceLoaders)
      {
         streams.addAll(loader.getResourcesAsStream(name));
      }
      return streams;
   }

}
