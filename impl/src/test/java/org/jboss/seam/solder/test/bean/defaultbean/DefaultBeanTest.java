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
package org.jboss.seam.solder.test.bean.defaultbean;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.solder.bean.Beans;
import org.jboss.seam.solder.bean.defaultbean.DefaultBeanExtension;
import org.jboss.seam.solder.bean.generic.GenericBeanExtension;
import org.jboss.seam.solder.beanManager.BeanManagerAware;
import org.jboss.seam.solder.core.Client;
import org.jboss.seam.solder.core.CoreExtension;
import org.jboss.seam.solder.el.Resolver;
import org.jboss.seam.solder.literal.DefaultLiteral;
import org.jboss.seam.solder.logging.Category;
import org.jboss.seam.solder.logging.TypedMessageLoggerExtension;
import org.jboss.seam.solder.messages.Messages;
import org.jboss.seam.solder.reflection.Synthetic;
import org.jboss.seam.solder.resourceLoader.ResourceLoader;
import org.jboss.seam.solder.serviceHandler.ServiceHandlerExtension;
import org.jboss.seam.solder.support.SolderMessages;
import org.jboss.seam.solder.test.properties.ClassToIntrospect;
import org.jboss.seam.solder.unwraps.UnwrapsExtension;
import org.jboss.seam.solder.util.Sortable;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.logging.ParameterConverter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

/**
 * This test verifies that {@link @GenericBean} works as expected.
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 *
 */
@RunWith(Arquillian.class)
public class DefaultBeanTest
{
   @Inject
   private Airplane airplane;
   @Inject
   private Vehicle vehicle;
   
   @Deployment
   public static WebArchive createDeployment()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war");
      war.addPackage(DefaultBeanTest.class.getPackage());
      war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      war.addAsLibrary(createSeamSolder());
      return war;
   }
   
   /**
    * Seam Solder
    * TODO: there must be a better way to get Solder jar
    */
   public static JavaArchive createSeamSolder()
   {
      JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "solder.jar");
      
      jar.addPackages(true, Beans.class.getPackage()); // .bean
      jar.addPackages(true, BeanManagerAware.class.getPackage()); // .beanManager
      jar.addPackages(true, Client.class.getPackage()); // .core
      jar.addPackages(true, Resolver.class.getPackage()); // .el
      jar.addPackages(true, DefaultLiteral.class.getPackage()); // .literal
      jar.addPackages(true, Category.class.getPackage()); // .log
      jar.addPackages(true, Messages.class.getPackage()); // .logging
      jar.addPackages(true, ClassToIntrospect.class.getPackage()); // .properties
      jar.addPackages(true, SolderMessages.class.getPackage()); // .messages
      jar.addPackages(true, Synthetic.class.getPackage()); // .reflection
      jar.addPackages(true, ResourceLoader.class.getPackage()); // .resourceLoader 
      jar.addPackages(true, ServiceHandlerExtension.class.getPackage()); // .serviceHandler
      jar.addPackages(true, UnwrapsExtension.class.getPackage()); // .unwraps
      jar.addPackages(true, Sortable.class.getPackage()); // .util
      jar.addPackages(false, ParameterConverter.class.getPackage()); // org.jboss.logging
      
      jar.addAsServiceProvider(Extension.class, GenericBeanExtension.class, DefaultBeanExtension.class, CoreExtension.class, UnwrapsExtension.class, TypedMessageLoggerExtension.class, ServiceHandlerExtension.class);
      jar.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
      return jar;
   }
   
   @Test
   public void testDefaultProducerUsed()
   {
      assertEquals("Cessna 172", airplane.getName());
   }
   
   @Test
   public void testDefaultProducerNotUsed()
   {
      assertEquals("Seat Ibiza", vehicle.getName());
   }
}
