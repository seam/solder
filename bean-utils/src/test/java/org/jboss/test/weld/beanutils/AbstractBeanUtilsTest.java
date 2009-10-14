/*
* JBoss, Home of Professional Open Source.
* Copyright 2006, Red Hat Middleware LLC, and individual contributors
* as indicated by the @author tags. See the copyright.txt file in the
* distribution for a full listing of individual contributors. 
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/ 
package org.jboss.test.weld.beanutils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.bootstrap.BeanDeployerEnvironment;
import org.jboss.weld.bootstrap.BeanDeployment;
import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;

/**
 * 
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractBeanUtilsTest
{
   /**
    * Contains the web beans deployment
    */
   MockBULifecycle lifecycle;
   
   boolean deployedWebBeans;

   /**
    * Initialises the lifecycle with the classes to deploy once deployWebBeans() is called
    * 
    * @param classes the classes to deploy
    */
   protected void initialiseEnvironment(Class<?>...classes)
   {
      lifecycle = new MockBULifecycle();
      lifecycle.getDeployment().getArchive().setBeanClasses(Arrays.asList(classes));
      lifecycle.initialize();
   }
   
   /**
    * Deploys classes as web beans and sets {@link #lifecycle} to point to the created lifecycle
    * 
    * @throws Exception if an error occured
    */
   protected void deployWebBeans() throws Exception
   {
      if (lifecycle == null)
         throw new IllegalStateException("Lifecycle needs to be initialised by calling initialiseEnvironment()");
      lifecycle.beginApplication();
      lifecycle.beginSession();
      lifecycle.beginRequest();
      deployedWebBeans = true;
   }
   
   
   /**
    * Undeploys the webbeans deployed if {@link #lifecycle} is set
    */
   protected void undeployWebBeans() throws Exception
   {
      lifecycle = null;
      if (lifecycle != null)
      {
         if (deployedWebBeans)
         {
            lifecycle.endRequest();
            lifecycle.endSession();
            lifecycle.endApplication();
         }
         lifecycle = null;
      }
   }


   /**
    * Gets the current bean manager used. 
    * 
    * @return the bean manager
    */
   protected BeanManager getCurrentManager() throws Exception
   {
      if (lifecycle == null)
         throw new IllegalStateException("Lifecycle needs to be initialised by calling initialiseEnvironment()");

      return lifecycle.getBootstrap().getManager(lifecycle.getDeployment().getArchive());
   }
   
   protected BeanDeployerEnvironment getBeanDeployerEnvironment() throws Exception
   {
      if (lifecycle == null)
         throw new IllegalStateException("Lifecycle needs to be initialised by calling initialiseEnvironment()");

      WeldBootstrap bootstrap = lifecycle.getBootstrap();
     
      //TODO: Hack
      Field field = WeldBootstrap.class.getDeclaredField("beanDeployments");
      field.setAccessible(true);
      Map<BeanDeploymentArchive, BeanDeployment> deployments = (Map<BeanDeploymentArchive, BeanDeployment>)field.get(bootstrap);
      
      assert deployments != null;
      assert deployments.size() == 1;
      BeanDeployment deployment = deployments.entrySet().iterator().next().getValue();
      
      return deployment.getBeanDeployer().getEnvironment();
      
   }
}
