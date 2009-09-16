package org.jboss.webbeans.environment.servlet.services;

import javax.servlet.ServletContext;

import org.jboss.webbeans.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.webbeans.servlet.api.ServletServices;

public class ServletServicesImpl implements ServletServices
{
   
   private final BeanDeploymentArchive beanDeploymentArchive;

   public ServletServicesImpl(BeanDeploymentArchive beanDeploymentArchive)
   {
      this.beanDeploymentArchive = beanDeploymentArchive;
   }

   public BeanDeploymentArchive getBeanDeploymentArchive(ServletContext ctx)
   {
      return beanDeploymentArchive;
   }

   public void cleanup() {}

}
