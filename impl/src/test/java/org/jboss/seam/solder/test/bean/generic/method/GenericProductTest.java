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
package org.jboss.seam.solder.test.bean.generic.method;

import static org.jboss.seam.solder.test.util.Deployments.baseDeployment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class GenericProductTest
{
   
   @Deployment
   public static Archive<?> deployment()
   {
      return baseDeployment().addPackage(GenericProductTest.class.getPackage());
   }

   @Inject
   @Foo(1)
   private Garply garply1;

   @Inject
   @Foo(2)
   private Garply garply2;
   
   @Inject
   @Foo(1)
   private Waldo waldo1;

   @Inject
   @Foo(2)
   private Waldo waldo2;

   @Inject
   @Foo(1)
   @WaldoName
   private String waldoName1;

   @Inject
   @Foo(2)
   @WaldoName
   private String waldoName2;

   @Inject
   @Foo(1)
   @Formatted
   private String formattedWaldoName1;

   @Inject
   @Foo(2)
   @Formatted
   private String formattedWaldoName2;

   @Test
   public void testGeneric()
   {
      
      // Check injection of product
      assertNotNull(waldo1);
      assertNotNull(waldo2);
      assertEquals("Pete", waldo1.getName());
      assertEquals("Stuart", waldo2.getName());
      
      assertNotNull(garply1);
      assertNotNull(garply2);
      
      assertEquals("Pete", garply1.getWaldo().getName());
      assertEquals("Stuart", garply2.getWaldo().getName());
      
      assertEquals("Pete", waldoName1);
      assertEquals("Stuart", waldoName2);

      assertEquals("[Pete]", formattedWaldoName1);
      assertEquals("[Stuart]", formattedWaldoName2);
   }

   @Test
   public void testDisposerCalled(BeanManager manager)
   {
      Bean<?> bean = manager.resolve(manager.getBeans(String.class, new AnnotationLiteral<WaldoName>()
      {
      }, new FooLiteral(1)));
      CreationalContext<?> ctx = manager.createCreationalContext(bean);
      manager.getReference(bean, String.class, ctx);
      ctx.release();
      Assert.assertTrue(Garply.disposerCalled);
   }
}
