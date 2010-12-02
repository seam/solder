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
package org.jboss.seam.solder.test.defaultbean;

import static org.jboss.seam.solder.test.util.Deployments.baseDeployment;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.solder.literal.DefaultLiteral;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DefaultBeanTest
{
   @Inject
   OpticalDrive opticalDrive;

   @Inject
   MagneticDrive magneticDrive;

   @Inject
   CPU cpu;

   @Inject
   GPU gpu;

   @Inject
   HardDrive hardDrive;

   @Inject
   HardDriveFactory factory;

   @Inject
   BeanManager manager;

   @Inject
   @SASHardDrive
   HardDrive sasHardDrive;

   @Inject
   @LaptopHardDrive
   HardDrive laptopHardDrive;

   @Deployment
   public static WebArchive deployment()
   {
      return baseDeployment().addPackage(DefaultBeanTest.class.getPackage());
   }

   @Test
   public void testDefaultBean()
   {
      Assert.assertTrue(opticalDrive instanceof DVDDrive);
      Assert.assertTrue(magneticDrive instanceof FloppyDrive);
   }

   @Test
   public void testDefaultProducerMethod()
   {
      Assert.assertEquals("fast", cpu.getSpeed());
      Assert.assertEquals("slow", gpu.getSpeed());
   }

   @Test
   public void testDefaultProducerUsesCorrectDelegate()
   {
      factory.setSize("big");
      Assert.assertEquals("big", hardDrive.size());
   }

   @Test
   public void testDefaultProducerFields()
   {
      Assert.assertEquals("100MB", sasHardDrive.size());
      Assert.assertEquals("200MB", laptopHardDrive.size());
   }

   @Test
   public void testDefaultBeanObserverMethods()
   {
      WriteEvent event = new WriteEvent();
      manager.fireEvent(event, DefaultLiteral.INSTANCE);
      Assert.assertEquals(1, event.getCount());
   }

}
