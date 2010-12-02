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
package org.jboss.seam.solder.test.unwraps;

import static org.jboss.seam.solder.test.util.Deployments.baseDeployment;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class UnwrapsTest
{

   @Deployment
   public static Archive<?> deployment()
   {
      return baseDeployment().addPackage(UnwrapsTest.class.getPackage());
   }

   @Inject
   ManagedReciever bean;

   @Inject
   @Named("lion")
   Lion lion;

   @Inject
   LionTamer lionTamer;

   @Test
   public void testUnwrapsInjectionPoint()
   {
      assert bean.getBean1().getValue().equals("bean1") : " value: " + bean.getBean1().getValue();
      assert bean.getBean2().getValue().equals("bean2") : " value: " + bean.getBean2().getValue();
   }

   @Test
   public void testUnwraps()
   {
      assert lion.getName().equals("lion one");
      lionTamer.changeLion();
      assert lion.getName().equals("lion two");
   }

}
