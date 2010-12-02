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
package org.jboss.seam.solder.test.bean.generic.alternative;

import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.solder.test.util.MavenArtifactResolver;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class GenericBeanAlternativeTest
{
   @Deployment
   public static WebArchive deployment()
   {
      WebArchive archive = ShrinkWrap.create(WebArchive.class, "test.war").addLibrary(MavenArtifactResolver.resolve("org.jboss.seam.solder", "seam-solder"));
      archive.addPackage(GenericBeanAlternativeTest.class.getPackage());
      archive.addWebResource("org/jboss/seam/solder/test/bean/generic/alternative/beans.xml", "beans.xml");
      return archive;
   }

   @Inject
   @Big
   Pow bigPow;

   @Inject
   @Small
   Pow smallPow;

   @Inject
   @Big
   Bop bigBop;

   @Inject
   @Small
   Bop smallBop;

   @Test
   public void testGenericAlternatives()
   {
      Assert.assertEquals("Alternative Big Bam", bigPow.getName());
      Assert.assertEquals("Small Bam", smallPow.getName());
   }

   @Test
   public void testGenericProducerMethodAlternatives()
   {
      Assert.assertEquals("Alternative Big Bam", bigBop.getName());
      Assert.assertEquals("Small Bam", smallBop.getName());
   }

}
