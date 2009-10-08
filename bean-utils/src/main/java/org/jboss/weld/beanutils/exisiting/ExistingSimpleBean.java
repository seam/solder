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
package org.jboss.weld.beanutils.exisiting;

import javax.enterprise.context.spi.CreationalContext;

import org.jboss.weld.BeanManagerImpl;
import org.jboss.weld.bean.ManagedBean;
import org.jboss.weld.introspector.WeldClass;

/**
 * used to create a bean implementation for an existing instance
 * 
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
public class ExistingSimpleBean<T> extends ManagedBean<T>
{
   
   T instance;
   
   /**
    * Constructor
    * 
    * @param <T> The type
    * @param clazz The class
    * @param manager the current manager
    * @param instance the existing instance
    * @return A Web Bean
    */
   protected ExistingSimpleBean(WeldClass<T> type, BeanManagerImpl manager, T instance)
   {
      super(type, null, manager);
      this.instance = instance;
   }
   
   /**
    * Creates a simple, annotation defined Web Bean
    * 
    * @param <T> The type
    * @param clazz The class
    * @param manager the current manager
    * @return A Web Bean
    */
   public static <T> ExistingSimpleBean<T> of(WeldClass<T> clazz, BeanManagerImpl manager, T instance)
   {
      return new ExistingSimpleBean<T>(clazz, manager, instance);
   }
   
   /**
    * Return the instance rather than creating a new bean
    * @param creationalContext the creational context
    * @return the instance
    */
   @Override
   public T create(CreationalContext<T> creationalContext)
   {
      creationalContext.push(instance);
      return instance;
   }

   /**
    * Return the instance rather than creating a new bean
    * @param creationalContext the creational context
    * @return the instance
    */
   @Override
   public T produce(CreationalContext<T> ctx)
   {
      ctx.release();
      return instance;
   }
}
