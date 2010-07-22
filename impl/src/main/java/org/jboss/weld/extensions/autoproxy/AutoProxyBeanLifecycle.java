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
package org.jboss.weld.extensions.autoproxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;

import javax.enterprise.context.spi.CreationalContext;

import org.jboss.weld.extensions.bean.BeanImpl;
import org.jboss.weld.extensions.bean.BeanLifecycle;

public class AutoProxyBeanLifecycle<T> implements BeanLifecycle<T>
{

   private final Class<? extends T> proxyClass;
   private final Class<? extends InvocationHandler> handler;
   private final Constructor<? extends T> constructor;

   public AutoProxyBeanLifecycle(Class<? extends T> proxyClass, Class<? extends InvocationHandler> handler)
   {
      this.proxyClass = proxyClass;
      this.handler = handler;
      try
      {
         constructor = proxyClass.getConstructor(InvocationHandler.class);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public T create(BeanImpl<T> bean, CreationalContext<T> creationalContext)
   {
      try
      {
         Object hinst = handler.newInstance();
         T instance = constructor.newInstance(hinst);
         creationalContext.push(instance);
         bean.getInjectionTarget().inject(instance, creationalContext);
         bean.getInjectionTarget().postConstruct(instance);
         return instance;
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public void destroy(BeanImpl<T> bean, T instance, CreationalContext<T> creationalContext)
   {
      bean.getInjectionTarget().preDestroy(instance);
      creationalContext.release();
   }

}
