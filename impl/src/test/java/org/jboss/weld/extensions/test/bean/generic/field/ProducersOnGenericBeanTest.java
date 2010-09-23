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

import static org.jboss.weld.extensions.test.util.Deployments.baseDeployment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ProducersOnGenericBeanTest
{

   @Deployment
   public static Archive<?> deployment()
   {
      return baseDeployment().addPackage(ProducersOnGenericBeanTest.class.getPackage());
   }
   
   @Inject
   @Qux
   @Foo(1)
   private String bar1Message;

   @Inject
   @Qux
   @Foo(2)
   private String bar2Message;

   @Inject
   @Foo(1)
   private Message baz1Message;

   @Inject
   @Foo(2)
   private Message baz2Message;
   
   @Inject
   @Foo(1) @Wibble
   private String wibble1;
   
   @Inject
   @Foo(2) @Wibble
   private String wibble2;
   
   @Inject
   @Foo(1)
   private Map<String, String> map;
   
   
   @Test
   public void testGeneric()
   {
      
      // Check that producer methods on generic beans are working
      assertNotNull(bar1Message);
      assertEquals("barhello1", bar1Message);
      assertNotNull(bar2Message);
      assertEquals( "barhello2", bar2Message);
      
      assertNotNull(baz1Message);
      assertEquals("hello1", baz1Message.value());
      assertNotNull(baz2Message);
      assertEquals( "hello2", baz2Message.value());
      
      assertNotNull(wibble1);
      assertEquals("billhello1", wibble1);
      assertNotNull(wibble2);
      assertEquals("billhello2", wibble2);
   }
   
   @Test
   // WELDX-133
   public void testProducerSuperclass()
   {
      assertTrue(map instanceof HashMap<?, ?>);
   }
   
}
