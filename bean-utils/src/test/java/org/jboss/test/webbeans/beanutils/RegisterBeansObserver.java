/*
* JBoss, Home of Professional Open Source.
* Copyright 2006, Red Hat Middleware LLC, and individual contributors
* as indicated by the @author tags. See the copyright.txt file in the
* distribution for a full listing of individual contributors. 
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/ 
package org.jboss.test.webbeans.beanutils;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.Extension;

import org.jboss.testharness.impl.packaging.Artifact;
import org.jboss.webbeans.beanutils.spi.Beans;

/**
 * 
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
@Artifact
public class RegisterBeansObserver implements Extension
{
   static List<Beans<?>> beans = new ArrayList<Beans<?>>();
   
   public static void clear()
   {
      beans.clear();
   }
   
   public static void addBeans(Beans<?> beans)
   {
      RegisterBeansObserver.beans.add(beans);
   }
   
   public void observe(@Observes AfterBeanDiscovery afterBeanDiscovery)
   {
      for (Beans<?> beans : RegisterBeansObserver.beans)
      {   
         for(Bean<?> bean : beans.getAllBeans())
            afterBeanDiscovery.addBean(bean);
      }
   }
}
