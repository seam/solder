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
package org.jboss.seam.solder.test.bean.generic.field;

import static org.jboss.seam.solder.test.util.Deployments.baseDeployment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class GenericBeanTest
{
   @Deployment
   public static Archive<?> deployment()
   {
      return baseDeployment().addPackage(GenericBeanTest.class.getPackage());
   }

   @Inject
   @Foo(1)
   private Baz baz1;

   @Inject
   @Foo(2)
   private Baz baz2;

   @Inject
   @Foo(1)
   private Bar bar1;

   @Inject
   @Foo(2)
   private Bar bar2;
   
   @Inject
   @Foo(3)
   private Baz baz3;
   
   @Inject
   @Foo(3)
   private Baz baz3a;
   
   @Inject
   @Foo(4)
   private Baz baz4;
   
   @Inject
   @Foo(4)
   private Baz baz4a;

   @Inject
   @Foo(5)
   private Baz baz5;

   @Test
   public void testGeneric()
   {
      // Check that normal bean injection is working correctly!
      assertNotNull(baz2.getCorge());
      assertEquals(baz2.getCorge().getName(), "fred");
      
      // Test that the generic configuration injection wiring is working for bar
      assertNotNull(bar1.getInjectedMessage());
      assertEquals(bar1.getInjectedMessage().value(), "hello1");
      assertNotNull(bar2.getInjectedMessage());
      assertEquals(bar2.getInjectedMessage().value(), "hello2");
      
      // Check that the generic configuration injection wiring is working for baz
      assertNotNull(baz1.getMessage());
      assertEquals(baz1.getMessage().value(), "hello1");
      assertNotNull(baz2.getMessage());
      assertEquals(baz2.getMessage().value(), "hello2");
      
      assertNotNull(baz5.getMessage());
      assertEquals(baz5.getMessage().value(), "hello5");

      // Check that this isn't affecting annotations on the generic bean without @Inject 
      assertNull(baz1.getBar().getMessage());
      assertNull(baz2.getBar().getMessage());
      
   }
   
   @Test
   public void testScope()
   {
      assertNotSame(baz1.getCorge(), baz2.getCorge());
      assertNotNull(baz3);
      assertNotNull(baz3a);
      assertEquals(baz3.getCorge(), baz3a.getCorge());
      assertNotNull(baz4);
      assertNotNull(baz4a);
      assertEquals(baz4.getCorge(), baz4a.getCorge());
      assertNotSame(baz3.getCorge(), baz4.getCorge());
   }
}
