package org.jboss.weld.extensions.resources;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Set;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.jboss.weld.extensions.Resource;

/**
 * Resource producer allows injecting of resources
 * 
 * @author pmuir
 *
 */
class ResourceProducer
{
   
   private final ResourceLoaderManager resourceLoaderManager;
   
   @Inject
   ResourceProducer(ResourceLoaderManager resourceLoaderManager)
   {
      this.resourceLoaderManager = resourceLoaderManager;
   }
   
   @Produces @Resource("")
   InputStream loadResourceStream(InjectionPoint injectionPoint) throws IOException
   {
      String name = getName(injectionPoint);
      for (ResourceLoader loader : resourceLoaderManager.getResourceLoaders())
      {
         InputStream is = loader.getResourceAsStream(name);
         if (is != null)
         {
            return is;
         }
      }
      return null;
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
   URL loadResource(InjectionPoint injectionPoint)
   {
      String name = getName(injectionPoint);
      for (ResourceLoader loader : resourceLoaderManager.getResourceLoaders())
      {
         URL url = loader.getResource(name);
         if (url != null)
         {
            return url;
         }
      }
      return null;
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
