/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.solder.test.util;

import static org.jboss.seam.solder.test.util.Deployments.baseDeployment;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.solder.reflection.AnnotationInspector;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class AnnotationInspectorTest
{
   @Deployment
   public static Archive<?> deployment()
   {
      return baseDeployment().addPackage(AnnotationInspectorTest.class.getPackage());
   }

   @Inject
   BeanManager beanManager;

   @Test
   public void testAnnotationOnElement() throws Exception
   {
      assert AnnotationInspector.isAnnotationPresent(Animals.class.getMethod("dog"), Animal.class, false, beanManager);
      assert AnnotationInspector.getAnnotation(Animals.class.getMethod("dog"), Animal.class, false, beanManager).species().equals("Dog");
   }

   @Test
   public void testAnnotationOnStereotype() throws Exception
   {
      assert AnnotationInspector.isAnnotationPresent(Animals.class.getMethod("cat"), Animal.class, true, beanManager);
      assert AnnotationInspector.getAnnotation(Animals.class.getMethod("cat"), Animal.class, true, beanManager).species().equals("Cat");
   }

}
