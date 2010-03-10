package org.jboss.weld.test.extensions.resources;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.jboss.weld.extensions.resources.ResourceProvider;

@RequestScoped
public class ResourceClient
{
   
   @Inject ResourceProvider resourceProvider;

   public ResourceProvider getResourceProvider()
   {
      return resourceProvider;
   }
   
}
