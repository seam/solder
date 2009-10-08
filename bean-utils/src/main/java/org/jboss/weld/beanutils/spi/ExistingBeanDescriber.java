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
package org.jboss.weld.beanutils.spi;

import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.bootstrap.BeanDeployerEnvironment;
import org.jboss.weld.beanutils.exisiting.ExistingBeanDescriberImpl;

/**
 * Utilities to create beans that take an existing instance
 * 
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
public class ExistingBeanDescriber
{
   private static final ExistingBeanDescriber EXISTING_BEAN_DESCRIBER = new ExistingBeanDescriber();
   
   private ExistingBeanDescriber()
   {
      
   }
   
   /**
    * Takes an existing bean instance and creates the Beans wrapping it
    * 
    * @param type the annotated type of the bean
    * @param the bean deployer environment
    * @param beanManager the bean manager used to create the bean
    * @param instance the instance we want to wrap
    */
   public static <T> Beans<T> describePreinstantiatedBean(AnnotatedType<T> type, BeanDeployerEnvironment env, BeanManager beanManager, T instance)
   {
      return ExistingBeanDescriberImpl.describePreinstantiatedBean(EXISTING_BEAN_DESCRIBER, env, type, beanManager, instance);
   }
   
}
