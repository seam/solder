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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.weld.extensions.reflection.AnnotationInstanceProvider;

/**
 * <p>
 * The ResourceProvider allows dynamic loading of managed resources. For
 * example:
 * </p>
 * 
 * <pre>
 * &#64;Inject
 * void readXml(ResourceProvider provider, String fileName) {
 *    InputStream webXml = provider.loadResourceStream(fileName);
 * }
 * </pre>
 * 
 * <p>
 * If you know the name of the resource you are loading at development time you
 * can inject it directly using the {@link Resource} qualifier.
 * </p>
 * 
 * <p>
 * If a input stream is loaded, it will be automatically closed when the
 * InputStream goes out of scope. If a URL is used to create an input stream,
 * the application is responsible for closing it. For this reason it is
 * recommended that managed input streams are used where possible.
 * </p>
 * 
 * @author Pete Muir
 * 
 * @see Resource
 */
public class ResourceProvider implements Serializable
{

   private static final long serialVersionUID = -4463427096501401965L;

   private final transient AnnotationInstanceProvider annotationInstanceProvider = new AnnotationInstanceProvider();;

   private final Instance<URL> urlProvider;
   private final Instance<InputStream> inputStreamProvider;

   private final Instance<Collection<URL>> urlsProvider;
   private final Instance<Collection<InputStream>> inputStreamsProvider;

   // Workaround WELD-466
   private final Set<InputStream> streamsCache;

   @Inject
   private ResourceProvider(@Any Instance<InputStream> inputStreamProvider, @Any Instance<URL> urlProvider, @Any Instance<Collection<InputStream>> inputStreamsProvider, @Any Instance<Collection<URL>> urlsProvider)
   {
      this.inputStreamProvider = inputStreamProvider;
      this.urlProvider = urlProvider;
      this.urlsProvider = urlsProvider;
      this.inputStreamsProvider = inputStreamsProvider;
      this.streamsCache = new HashSet<InputStream>();
   }

   /**
    * <p>
    * Load a resource.
    * </p>
    * 
    * <p>
    * The default search order is:
    * </p>
    * 
    * <ul>
    * <li></li>
    * </ul>
    * 
    * 
    * @param name
    * @return
    */
   public InputStream loadResourceStream(String name)
   {
      if (name == null || name.equals(""))
      {
         throw new IllegalArgumentException("You must specify the name of the resource to load");
      }
      Map<String, Object> values = new HashMap<String, Object>();
      values.put("value", name);
      InputStream stream = inputStreamProvider.select(annotationInstanceProvider.get(Resource.class, values)).get();
      // Workaround WELD-466
      streamsCache.add(stream);
      return stream;
   }

   /**
    * <p>
   public Collection<InputStream> loadResourcesStreams(String name)
   {
      if (name == null || name.equals(""))
      {
         throw new IllegalArgumentException("You must specify the name of the resource to load");
      }
      Map<String, Object> values = new HashMap<String, Object>();
      values.put("value", name);
      Collection<InputStream> streams = inputStreamsProvider.select(annotationInstanceProvider.get(Resource.class, values)).get();
      // Workaround WELD-466
      streamsCache.addAll(streams);
      return streams;
   }
    * Load a resource.
    * </p>
    * 
    * <p>
    * The default search order is:
    * </p>
    * 
    * <ul>
    * <li></li>
    * </ul>
    * 
    * 
    * @param name
    * @return
    */
   public URL loadResource(String name)
   {
      if (name == null || name.equals(""))
      {
         throw new IllegalArgumentException("You must specify the name of the resource to load");
      }
      Map<String, Object> values = new HashMap<String, Object>();
      values.put("value", name);
      return urlProvider.select(annotationInstanceProvider.get(Resource.class, values)).get();
   }

   public Collection<URL> loadResources(String name)
   {
      if (name == null || name.equals(""))
      {
         throw new IllegalArgumentException("You must specify the name of the resource to load");
      }
      Map<String, Object> values = new HashMap<String, Object>();
      values.put("value", name);
      return urlsProvider.select(annotationInstanceProvider.get(Resource.class, values)).get();
   }

   @SuppressWarnings("unused")
   @PreDestroy
   private void cleanup()
   {
      for (InputStream stream : streamsCache)
      {
         try
         {
            stream.close();
         }
         catch (IOException e)
         {
            // Nothing we can do about this
         }
      }
   }

}
