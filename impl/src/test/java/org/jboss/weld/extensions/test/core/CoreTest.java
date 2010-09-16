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
package org.jboss.weld.extensions.test.core;

import static org.jboss.weld.extensions.test.util.Deployments.baseDeployment;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.weld.extensions.literal.DefaultLiteral;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stuart Douglas
 * 
 */
@RunWith(Arquillian.class)
public class CoreTest
{
   
   @Deployment
   public static Archive<?> deployment()
   {
      return baseDeployment().addPackage(CoreTest.class.getPackage());
   }

   @Inject
   RaceTrack raceTrack;

   @Test
   public void testExact()
   {
      assert raceTrack.getDog() instanceof Greyhound;
   }

   @Test
   public void testBeanInstalled(BeanManager manager)
   {
      Set<Bean<?>> beans = manager.getBeans(InstalledService.class, DefaultLiteral.INSTANCE);
      Bean<?> bean = manager.resolve(beans);
      CreationalContext<?> ctx = manager.createCreationalContext(bean);
      manager.getReference(bean, InstalledService.class, ctx);
   }

   @Test
   public void testBeanNotInstalled(BeanManager manager)
   {
      Set<Bean<?>> beans = manager.getBeans(OptionalService.class, DefaultLiteral.INSTANCE);
      Assert.assertEquals(0, beans.size());
   }

}
