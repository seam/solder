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
package org.jboss.weld.extensions.test.bean.generic.field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class GenericProductTest
{
   @Deployment
   public static Archive<?> deploy()
   {
      return ShrinkWrap.create("test.jar", JavaArchive.class).addPackage(GenericProductTest.class.getPackage());
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

//   @Inject
//   @Foo(1)
//   @WaldoName
//   private String waldoName1;

//   @Inject
//   @Foo(2)
//   @WaldoName
//   private String waldoName2;

   @Test
   public void testGeneric()
   {
      
      // Check injection of product
      assertNotNull(waldo1);
      assertNotNull(waldo2);
      assertNull(waldo1.getName());
      assertNull(waldo2.getName());
      
      assertNotNull(garply1);
      assertNotNull(garply2);
      
      assertEquals("Pete", garply1.getWaldo().getName());
      assertEquals("Stuart", garply2.getWaldo().getName());
   }
}
