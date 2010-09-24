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
 * The ResourceProvider allows dynamic loading of managed resources.
 * 
 * If a input stream is loaded, it will be automatically closed when the InputStream goes 
 * out of scope. If a URL is used to create an input stream, the application is responsible 
 * for closing it. For this reason it is recommended that managed input streams are used
 * where possible.
 * 
 * @author pmuir
 *
 */
public class ResourceProvider implements Serializable
{
   
   private static final long serialVersionUID = -4463427096501401965L;

   private final transient AnnotationInstanceProvider annotationInstanceProvider = new AnnotationInstanceProvider();;

   private final Instance<URL> urlProvider;
   private final Instance<InputStream> inputStreamProvider;

   // Workaround WELD-466
   private final Set<InputStream> streams;

   @Inject
   private ResourceProvider(@Any Instance<InputStream> inputStreamProvider, @Any Instance<URL> urlProvider)
   {
      this.inputStreamProvider = inputStreamProvider;
      this.urlProvider = urlProvider;
      this.streams = new HashSet<InputStream>();
   }
   
   /**
    * <p>Load a resource.</p>
    * 
    * <p>The default search order is:</p>
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
      streams.add(stream);
      return stream;
   }
   
   
   /**
    * <p>Load a resource.</p>
    * 
    * <p>The default search order is:</p>
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
   
   @SuppressWarnings("unused")
   @PreDestroy
   private void cleanup()
   {
      for (InputStream stream : streams)
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
