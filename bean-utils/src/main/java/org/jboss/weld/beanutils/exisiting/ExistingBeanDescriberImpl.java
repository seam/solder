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

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.BeanManagerImpl;
import org.jboss.weld.bean.AbstractClassBean;
import org.jboss.weld.bean.NewManagedBean;
import org.jboss.weld.bean.ProducerField;
import org.jboss.weld.bean.ProducerMethod;
import org.jboss.weld.bootstrap.BeanDeployerEnvironment;
import org.jboss.weld.introspector.WeldClass;
import org.jboss.weld.introspector.WeldField;
import org.jboss.weld.introspector.WeldMethod;
import org.jboss.weld.introspector.jlr.WeldClassImpl;
import org.jboss.weld.resources.ClassTransformer;
import org.jboss.weld.beanutils.impl.BeansImpl;
import org.jboss.weld.beanutils.spi.Beans;
import org.jboss.weld.beanutils.spi.ExistingBeanDescriber;

/**
 * 
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
public class ExistingBeanDescriberImpl
{
   /**
    * Takes an existing bean instance and creates the Beans wrapping it.
    * 
    * @param type the annotated type of the bean
    * @param env the bean deployer environment
    * @param beanManager the bean manager used to create the bean
    * @param instance the instance we want to wrap
    * @return Beans instance representing the beans to be installed in the bean manager
    */
   public static <T> Beans<T> describePreinstantiatedBean(ExistingBeanDescriber describer, BeanDeployerEnvironment env, AnnotatedType<T> type, BeanManager beanManager, T instance)
   {
      if (type == null)
         throw new IllegalArgumentException("Null type");
      if (beanManager == null)
         throw new IllegalArgumentException("Null beanManager");
      if (beanManager instanceof BeanManagerImpl == false)
         throw new IllegalArgumentException("BeanManager is not an instance of BeanManagerImpl");
      
      WeldClass<T> clazz = type instanceof WeldClass ? 
            (WeldClass<T>)type :
               WeldClassImpl.of(type, ((BeanManagerImpl)beanManager).getServices().get(ClassTransformer.class));
      
      BeansImpl<T> beans = new BeansImpl<T>();

      ExistingSimpleBean<T> bean = (ExistingSimpleBean<T>)createSimpleBean(clazz, env, (BeanManagerImpl)beanManager, instance); 
      beans.setMainBean(bean);
      beans.setNewBean(createNewBean(clazz, env, (BeanManagerImpl)beanManager));
      
      for (WeldMethod<?, ?> method : clazz.getDeclaredAnnotatedWeldMethods(Produces.class))
      {
         beans.addMethodProducerBean(createProducerMethodBean(bean, env, (BeanManagerImpl)beanManager, (WeldMethod<T, ?>)method));
      }
   
      for (WeldField<?, ?> field : clazz.getDeclaredAnnotatedWeldFields(Produces.class))
      {
         beans.addFieldProducerBean(createProducerFieldBean(bean, env, (BeanManagerImpl)beanManager, (WeldField<T, ?>)field));
      }
      
      return beans;
   }
   
   private static <T> Bean<T> createSimpleBean(WeldClass<T> clazz, 
         BeanDeployerEnvironment env, BeanManagerImpl beanManager, T instance)
   {
      ExistingSimpleBean<T> bean = ExistingSimpleBean.of(clazz, beanManager, instance);
      
      //TODO How to get BeanDeployerEnvironment?
      bean.initialize(env);
      return bean;
   }
   
   private static <T> Bean<T> createNewBean(WeldClass<T> clazz, 
         BeanDeployerEnvironment env, BeanManagerImpl beanManager)
   {
      NewManagedBean<T> bean = NewManagedBean.of(clazz, beanManager);
      
      //TODO How to get BeanDeployerEnvironment?
      bean.initialize(env);
      return bean;
   }
   
   private static <T> Bean<T> createProducerMethodBean(AbstractClassBean<T>  declaringBean, 
         BeanDeployerEnvironment env, BeanManagerImpl beanManager, WeldMethod<T, ?> method)
   {
      ProducerMethod<T> bean = ProducerMethod.of(method, declaringBean, beanManager);
      
      //TODO How to get BeanDeployerEnvironment?
      bean.initialize(env);
      return bean;
   }
   
   
   private static <T> Bean<T> createProducerFieldBean(AbstractClassBean<T>  declaringBean, 
         BeanDeployerEnvironment env, BeanManagerImpl beanManager, WeldField<T, ?> field)
   {
      ProducerField<T> bean = ProducerField.of(field, declaringBean, beanManager);
      
      //TODO How to get BeanDeployerEnvironment?
      bean.initialize(env);
      return bean;
   }
   
}
