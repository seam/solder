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
package org.jboss.webbeans.beanutils;

import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.webbeans.BeanManagerImpl;
import org.jboss.webbeans.bean.SimpleBean;
import org.jboss.webbeans.introspector.WBClass;
import org.jboss.webbeans.introspector.jlr.WBClassImpl;
import org.jboss.webbeans.resources.ClassTransformer;

/**
 * 
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
public class BeanDescriber<T>
{
   public static <T> Bean<T> describeBean(AnnotatedType<T> type, BeanManager beanManager)
   {
      if (type == null)
         throw new IllegalArgumentException("Null type");
      if (beanManager == null)
         throw new IllegalArgumentException("Null beanManager");
      if (beanManager instanceof BeanManagerImpl == false)
         throw new IllegalArgumentException("BeanManager is not an instance of BeanManagerImpl");
      
      WBClass<T> clazz = type instanceof WBClass ? 
            (WBClass<T>)type :
               WBClassImpl.of(type, ((BeanManagerImpl)beanManager).getServices().get(ClassTransformer.class));
      
      Bean<T> bean = SimpleBean.of(clazz, (BeanManagerImpl)beanManager);
      
      //TODO How to get BeanDeployerEnvironment?
      ((SimpleBean<T>)bean).initialize(null);
      return bean;
   }
}
