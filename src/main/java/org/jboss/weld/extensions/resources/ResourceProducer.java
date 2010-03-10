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
import org.jboss.weld.extensions.resources.spi.ResourceLoader;

public class ResourceProducer
{
   
   private final ResourceLoader loader;
   
   @Inject
   private ResourceProducer(ResourceLoader loader)
   {
      this.loader = loader;
   }
   
   @Produces @Resource("")
   protected InputStream loadResourceStream(InjectionPoint injectionPoint) throws IOException
   {
      return loader.getResourceAsStream(getName(injectionPoint));
   }
   
   protected void closeResourceStream(@Disposes @Resource("") InputStream inputStream) throws IOException
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
   protected URL loadResource(InjectionPoint injectionPoint)
   {
      return loader.getResource(getName(injectionPoint));
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
