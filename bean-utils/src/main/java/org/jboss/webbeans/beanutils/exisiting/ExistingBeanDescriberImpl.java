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
package org.jboss.webbeans.beanutils.exisiting;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.webbeans.BeanManagerImpl;
import org.jboss.webbeans.bean.AbstractClassBean;
import org.jboss.webbeans.bean.NewSimpleBean;
import org.jboss.webbeans.bean.ProducerFieldBean;
import org.jboss.webbeans.bean.ProducerMethodBean;
import org.jboss.webbeans.beanutils.impl.BeansImpl;
import org.jboss.webbeans.beanutils.spi.Beans;
import org.jboss.webbeans.beanutils.spi.ExistingBeanDescriber;
import org.jboss.webbeans.bootstrap.BeanDeployerEnvironment;
import org.jboss.webbeans.introspector.WBClass;
import org.jboss.webbeans.introspector.WBField;
import org.jboss.webbeans.introspector.WBMethod;
import org.jboss.webbeans.introspector.jlr.WBClassImpl;
import org.jboss.webbeans.resources.ClassTransformer;

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
      
      WBClass<T> clazz = type instanceof WBClass ? 
            (WBClass<T>)type :
               WBClassImpl.of(type, ((BeanManagerImpl)beanManager).getServices().get(ClassTransformer.class));
      
      BeansImpl<T> beans = new BeansImpl<T>();

      ExistingSimpleBean<T> bean = (ExistingSimpleBean<T>)createSimpleBean(clazz, env, (BeanManagerImpl)beanManager, instance); 
      beans.setMainBean(bean);
      beans.setNewBean(createNewBean(clazz, env, (BeanManagerImpl)beanManager));
      
      for (WBMethod<?, ?> method : clazz.getDeclaredWBAnnotatedMethods(Produces.class))
      {
         beans.addMethodProducerBean(createProducerMethodBean(bean, env, (BeanManagerImpl)beanManager, (WBMethod<T, ?>)method));
      }
   
      for (WBField<?, ?> field : clazz.getDeclaredAnnotatedWBFields(Produces.class))
      {
         beans.addFieldProducerBean(createProducerFieldBean(bean, env, (BeanManagerImpl)beanManager, (WBField<T, ?>)field));
      }
      
      return beans;
   }
   
   private static <T> Bean<T> createSimpleBean(WBClass<T> clazz, BeanDeployerEnvironment env, BeanManagerImpl beanManager, T instance)
   {
      ExistingSimpleBean<T> bean = ExistingSimpleBean.of(clazz, beanManager, instance);
      
      //TODO How to get BeanDeployerEnvironment?
      bean.initialize(env);
      return bean;
   }
   
   private static <T> Bean<T> createNewBean(WBClass<T> clazz, BeanDeployerEnvironment env, BeanManagerImpl beanManager)
   {
      NewSimpleBean<T> bean = NewSimpleBean.of(clazz, beanManager);
      
      //TODO How to get BeanDeployerEnvironment?
      bean.initialize(env);
      return bean;
   }
   
   private static <T> Bean<T> createProducerMethodBean(AbstractClassBean<T>  declaringBean, BeanDeployerEnvironment env, BeanManagerImpl beanManager, WBMethod<T, ?> method)
   {
      ProducerMethodBean<T> bean = ProducerMethodBean.of(method, declaringBean, beanManager);
      
      //TODO How to get BeanDeployerEnvironment?
      bean.initialize(env);
      return bean;
   }
   
   
   private static <T> Bean<T> createProducerFieldBean(AbstractClassBean<T>  declaringBean, BeanDeployerEnvironment env, BeanManagerImpl beanManager, WBField<T, ?> field)
   {
      ProducerFieldBean<T> bean = ProducerFieldBean.of(field, declaringBean, beanManager);
      
      //TODO How to get BeanDeployerEnvironment?
      bean.initialize(env);
      return bean;
   }
   
}
