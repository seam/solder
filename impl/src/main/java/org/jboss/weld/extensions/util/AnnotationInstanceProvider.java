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
package org.jboss.weld.extensions.util;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Provide an instance of an annotation with member values
 * 
 * @author Stuart Douglas
 * @author Pete Muir
 * 
 */
public class AnnotationInstanceProvider
{

   private final ConcurrentMap<Class<?>, Class<?>> cache;
   
   public AnnotationInstanceProvider()
   {
      cache = new ConcurrentHashMap<Class<?>, Class<?>>();
   }

   /**
    * Returns an instance of the given annotation type with member values
    * specified in the map.
    */
   public <T extends Annotation> T get(Class<T> annotation, Map<String, ?> values)
   {
      if (annotation == null)
      {
         throw new IllegalArgumentException("Must specify an annotation");
      }
      Class<?> clazz = cache.get(annotation);
      // Not safe against data race, but doesn't matter, we can recompute and
      // get the same value
      if (clazz == null)
      {
         // create the proxy class
         clazz = Proxy.getProxyClass(annotation.getClassLoader(), annotation, Serializable.class);
         cache.put(annotation, clazz);
      }
      AnnotationInvocationHandler handler = new AnnotationInvocationHandler(values, annotation);
      // create a new instance by obtaining the constructor via relection
      try
      {
         return annotation.cast(clazz.getConstructor(new Class[] { InvocationHandler.class }).newInstance(new Object[] { handler }));
      }
      catch (IllegalArgumentException e)
      {
         throw new IllegalStateException("Error instantiating proxy for annotation. Annotation type: " + annotation, e);
      }
      catch (InstantiationException e)
      {
         throw new IllegalStateException("Error instantiating proxy for annotation. Annotation type: " + annotation, e);
      }
      catch (IllegalAccessException e)
      {
         throw new IllegalStateException("Error instantiating proxy for annotation. Annotation type: " + annotation, e);
      }
      catch (InvocationTargetException e)
      {
         throw new IllegalStateException("Error instantiating proxy for annotation. Annotation type: " + annotation, e.getCause());
      }
      catch (SecurityException e)
      {
         throw new IllegalStateException("Error accessing proxy constructor for annotation. Annotation type: " + annotation, e);
      }
      catch (NoSuchMethodException e)
      {
         throw new IllegalStateException("Error accessing proxy constructor for annotation. Annotation type: " + annotation, e);
      }
   }
}
