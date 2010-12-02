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
package org.jboss.seam.solder.resourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;


/**
 * Resource producer allows injecting of resources.
 * 
 * @author Pete Muir
 * 
 * @see Resource
 * @see ResourceProvider
 *
 */
@ApplicationScoped
class ResourceProducer
{
   @Inject
   private ResourceLoaderManager resourceLoaderManager;
   
   @Produces @Resource("")
   InputStream loadResourceStream(InjectionPoint injectionPoint) throws IOException
   {
      String name = getName(injectionPoint);
      return resourceLoaderManager.getResourceAsStream(name);
   }
   
   void closeResourceStream(@Disposes @Resource("") InputStream inputStream) throws IOException
   {
      try
      {
         inputStream.close();
      }
      catch (IOException e)
      {
         // Nothing we can do about this
      }
   }
   
   @Produces @Resource("")
   Collection<InputStream> loadResourcesStream(InjectionPoint injectionPoint) throws IOException
   {
      String name = getName(injectionPoint);
      return resourceLoaderManager.getResourcesAsStream(name);
   }
   
   void closeResourcesStream(@Disposes @Resource("") Collection<InputStream> inputStreams) throws IOException
   {
      try
      {
         for (InputStream is : inputStreams)
         {
            is.close();
         }
      }
      catch (IOException e)
      {
         // Nothing we can do about this
      }
   }
   
   @Produces @Resource("")
   URL loadResource(InjectionPoint injectionPoint)
   {
      String name = getName(injectionPoint);
      return resourceLoaderManager.getResource(name);
   }
   
   @Produces @Resource("")
   Collection<URL> loadResources(InjectionPoint injectionPoint)
   {
      String name = getName(injectionPoint);
      return resourceLoaderManager.getResources(name);
   }
   
   @Produces @Resource("")
   Properties loadPropertiesBundle(InjectionPoint injectionPoint)
   {
      String name = getName(injectionPoint);
      return resourceLoaderManager.getPropertiesBundle(name);
   }
   
   @Produces @Resource("")
   Collection<Properties> loadPropertiesBundles(InjectionPoint injectionPoint)
   {
      String name = getName(injectionPoint);
      return resourceLoaderManager.getPropertiesBundles(name);
   }
   
   private String getName(InjectionPoint ip)
   {
      Set<Annotation> qualifiers = ip.getQualifiers();
      for (Annotation qualifier : qualifiers)
      {
         if (qualifier.annotationType().equals(Resource.class))
         {
            return ((Resource) qualifier).value();
         }
      }
      throw new IllegalArgumentException("Injection point " + ip + " does not have @Resource qualifier");
   }

}
