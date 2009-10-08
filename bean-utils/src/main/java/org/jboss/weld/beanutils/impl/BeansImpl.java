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
package org.jboss.weld.beanutils.impl;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;

import org.jboss.weld.beanutils.spi.Beans;

/**
 * 
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
public class BeansImpl<T> implements Beans<T>
{
   private Bean<T> mainBean;
   
   private Bean<T> newBean;
   
   private Set<Bean<T>> methodProducerBeans;
   
   private Set<Bean<T>> fieldProducerBeans;
   
   public Set<Bean<T>> getFieldProducerBeans()
   {
      return fieldProducerBeans;
   }
   
   public void addFieldProducerBean(Bean<T> bean)
   {
      if (fieldProducerBeans == null)
         fieldProducerBeans = new HashSet<Bean<T>>();
      
      fieldProducerBeans.add(bean);
   }
   
   public Bean<T> getMainBean()
   {
      return mainBean;
   }

   public void setMainBean(Bean<T> bean)
   {
      mainBean = bean;
   }

   public Set<Bean<T>> getMethodProducerBeans()
   {
      return methodProducerBeans;
   }

   public void addMethodProducerBean(Bean<T> bean)
   {
      if (methodProducerBeans == null)
         methodProducerBeans = new HashSet<Bean<T>>();
      
      methodProducerBeans.add(bean);
   }
   
   public Bean<T> getNewBean()
   {
      return newBean;
   }

   public void setNewBean(Bean<T> bean)
   {
      newBean = bean;
   }
   
   public Set<Bean<T>> getAllBeans()
   {
      Set<Bean<T>> beans = new HashSet<Bean<T>>();

      beans.add(mainBean);
      beans.add(newBean);
      if (fieldProducerBeans != null)
         beans.addAll(fieldProducerBeans);
      if (methodProducerBeans != null)
         beans.addAll(methodProducerBeans);
      
      return beans;
   }
}
