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
package org.jboss.test.weld.beanutils.existing;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;

import org.jboss.test.weld.beanutils.AbstractBeanUtilsTest;
import org.jboss.test.weld.beanutils.RegisterBeansObserver;
import org.jboss.weld.beanutils.spi.Beans;
import org.jboss.weld.beanutils.spi.ExistingBeanDescriber;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
public class ExisitingBeanDescriberTest extends AbstractBeanUtilsTest
{
   @BeforeMethod
   public void beforeMethod()
   {
      RegisterBeansObserver.clear();
   }
   
   @Test
   public void testExisitingInstanceInField() throws Exception
   {
      try
      {
         initialiseEnvironment(DefaultFieldReceiver.class);
         DefaultBean bean = new DefaultBean();
         registerBean(bean);
         deployWebBeans();

         DefaultFieldReceiver receiver = assertBean(DefaultFieldReceiver.class);
         assert receiver.getBean() != null;
         assert receiver.getBean() == bean;
      }
      finally
      {
         undeployWebBeans();
      }
   }
   
   @Test
   public void testExistingInstanceInConstructor() throws Exception
   {
      try
      {
         initialiseEnvironment(DefaultConstructorReceiver.class);
         DefaultBean bean = new DefaultBean();
         registerBean(bean);
         deployWebBeans();

         DefaultConstructorReceiver receiver = assertBean(DefaultConstructorReceiver.class);
         assert receiver.getBean() != null;
         assert receiver.getBean() == bean;
      }
      finally
      {
         undeployWebBeans();
      }
   }
   
   @Test
   public void testExistingInstanceFromMethodProducerInField() throws Exception
   {
      try
      {
         initialiseEnvironment(CustomDefaultFieldReceiver.class);
         MethodProducer bean = new MethodProducer();
         registerBean(bean);
         deployWebBeans();

         CustomDefaultFieldReceiver receiver = assertBean(CustomDefaultFieldReceiver.class);
         assert receiver.getDefaultBean() != null;
         assert receiver.getDefaultBean() == bean.getDefaultBean();
         assert receiver.getCustomBean() != null;
         assert receiver.getCustomBean() == bean.getCustomBean();
      }
      finally
      {
         undeployWebBeans();
      }
   }
   
   @Test
   public void testExistingInstanceFromMethodProducerInConstructor() throws Exception
   {
      try
      {
         initialiseEnvironment(CustomDefaultConstructorReceiver.class);
         MethodProducer bean = new MethodProducer();
         registerBean(bean);
         deployWebBeans();

         CustomDefaultConstructorReceiver receiver = assertBean(CustomDefaultConstructorReceiver.class);
         assert receiver.getDefaultBean() != null;
         assert receiver.getDefaultBean() == bean.getDefaultBean();
         assert receiver.getCustomBean() != null;
         assert receiver.getCustomBean() == bean.getCustomBean();
      }
      finally
      {
         undeployWebBeans();
      }
   }
   
   @Test
   public void testExistingInstanceFromFieldProducerInField() throws Exception
   {
      try
      {
         initialiseEnvironment(CustomDefaultFieldReceiver.class);
         FieldProducer bean = new FieldProducer();
         registerBean(bean);
         deployWebBeans();

         CustomDefaultFieldReceiver receiver = assertBean(CustomDefaultFieldReceiver.class);
         assert receiver.getDefaultBean() != null;
         assert receiver.getDefaultBean() == bean.getDefaultBean();
         assert receiver.getCustomBean() != null;
         assert receiver.getCustomBean() == bean.getCustomBean();
      }
      finally
      {
         undeployWebBeans();
      }
   }
   
   @Test
   public void testExistingInstanceFromFieldProducerInConstructor() throws Exception
   {
      try
      {
         initialiseEnvironment(CustomDefaultConstructorReceiver.class);
         FieldProducer bean = new FieldProducer();
         registerBean(bean);
         deployWebBeans();

         CustomDefaultConstructorReceiver receiver = assertBean(CustomDefaultConstructorReceiver.class);
         assert receiver.getDefaultBean() != null;
         assert receiver.getDefaultBean() == bean.getDefaultBean();
         assert receiver.getCustomBean() != null;
         assert receiver.getCustomBean() == bean.getCustomBean();
      }
      finally
      {
         undeployWebBeans();
      }
   }
   
   @Test
   public void testMethodProducerWithInjection() throws Exception
   {
      try
      {
         initialiseEnvironment(CustomDefaultConstructorReceiver.class);
         DefaultMethodProducerWithInjection producer = new DefaultMethodProducerWithInjection();
         registerBean(producer);
         CustomBean bean = new CustomBean();
         registerBean(bean);
         deployWebBeans();

         CustomDefaultConstructorReceiver receiver = assertBean(CustomDefaultConstructorReceiver.class);
         assert receiver.getDefaultBean() != null;
         assert receiver.getDefaultBean() == bean;
         assert receiver.getCustomBean() != null;
         assert receiver.getCustomBean() == bean;
      }
      finally
      {
         undeployWebBeans();
      }
   }
   
   private <T> void registerBean(T instance) throws Exception
   {
      AnnotatedType<T> type = getCurrentManager().createAnnotatedType((Class<T>)instance.getClass());
      Beans<T> beans = ExistingBeanDescriber.describePreinstantiatedBean(type, getBeanDeployerEnvironment(), getCurrentManager(), instance);
      RegisterBeansObserver.addBeans(beans);
   }
   
   private <T> T assertBean(Class<T> type) throws Exception
   {
      Set<Bean<?>> beans = getCurrentManager().getBeans(type);
      assert beans.size() == 1;
      Bean<T> bean = (Bean<T>)beans.iterator().next();
      CreationalContext<T> context = getCurrentManager().createCreationalContext(null);
      T t = bean.create(context);
      return t;
   }
}
