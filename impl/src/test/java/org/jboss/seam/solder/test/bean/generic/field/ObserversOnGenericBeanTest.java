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

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ObserversOnGenericBeanTest
{
   
   @Deployment
   public static Archive<?> deployment()
   {
      return baseDeployment().addPackage(ObserversOnGenericBeanTest.class.getPackage());
   }

   @Inject
   private Event<Plugh> plughEvent;

   @Test
   public void testGeneric()
   {
      

      // Check specific observers are invoked
      Plugh plugh1 = new Plugh();
      plughEvent.select(new FooLiteral(1)).fire(plugh1);
      assertEquals("hello1", plugh1.getMessage().value());
      
      Plugh plugh2 = new Plugh();
      plughEvent.select(new FooLiteral(2)).fire(plugh2);
      assertEquals("hello2", plugh2.getMessage().value());
      
      // Check that the base observer is invoked
      Plugh basePlugh = new Plugh();
      plughEvent.fire(basePlugh);
      assertNotNull(basePlugh.getMessage());
      assertEquals("base", basePlugh.getMessage().value());
      
   }
}
