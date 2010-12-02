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

package org.jboss.seam.solder.test.log;

import static org.jboss.seam.solder.test.util.Deployments.baseDeployment;
import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * These tests are basically manual as we have no way to check the output
 * 
 * @author David Allen
 */
@RunWith(Arquillian.class)
public class LoggerInjectionTest
{
   
   @Inject
   Sparrow sparrow;

   @Inject
   Finch finch;
   
   @Inject
   Hawk hawk;
   
   @Inject
   Jay jay;

   @Deployment
   public static Archive<?> deployment()
   {
      return baseDeployment().addPackage(LoggerInjectionTest.class.getPackage());
   }

   @Test
   public void testBasicLogInjection()
   {
      sparrow.generateLogMessage();
   }

   @Test
   public void testCategorySpecifiedLogger()
   {
      finch.generateLogMessage();
   }
   
   @Test
   public void testMessageLogger()
   {
      hawk.generateLogMessage();
   }
   
   @Test
   public void testMessageBundleInjection()
   {
      assertEquals("Spotted 8 jays", jay.getMessage());
   }
   
   
}
