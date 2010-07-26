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
package org.jboss.weld.extensions.servicehandler;

import java.lang.reflect.Method;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.jboss.weld.extensions.annotated.AnnotatedTypeBuilder;
import org.jboss.weld.extensions.util.Reflections;

/**
 * Manages the handler for the auto proxy extension. This class is responsible
 * for managing the lifecycle of the handler class instances
 * 
 * @author Stuart Douglas
 * 
 */
public class ServiceHandlerManager<T>
{
   private final Class<T> handlerClass;
   private final Method handlerMethod;
   private final InjectionTarget<T> injectionTarget;

   /**
    * Creates a wrapper around an AutoProxy handler class
    * 
    * @param handlerClass The handler class
    * @throws IllegalArgumentException if the handler class is does not have a
    *            suitable @AroundInvoke method
    */
   public ServiceHandlerManager(Class<T> handlerClass, BeanManager beanManager) throws IllegalArgumentException
   {
      this.handlerClass = handlerClass;
      Set<Method> methods = Reflections.getAllMethods(handlerClass);
      Method handler = null;
      //search for the handler method
      for (Method m : methods)
      {
         if (m.isAnnotationPresent(AroundInvoke.class))
         {
            if (m.getParameterTypes().length != 1 || m.getParameterTypes()[0] != InvocationContext.class)
            {
               throw new IllegalArgumentException("Could not find suitable AroundInvoke method on class " + handlerClass + " methods denoted @AroundInvoke must have a single InvokationContext parameter");
            }
            handler = m;
            break;
         }
      }
      if (handler == null)
      {
         throw new IllegalArgumentException("Could not find suitable AroundInvoke method on class " + handlerClass);
      }
      handlerMethod = handler;
      //now create the InjectionTarget
      AnnotatedTypeBuilder<T> typeBuilder = new AnnotatedTypeBuilder<T>().readFromType(handlerClass);
      injectionTarget = beanManager.createInjectionTarget(typeBuilder.create());
   }

   public T create(CreationalContext<T> ctx)
   {
      T instance = injectionTarget.produce(ctx);
      injectionTarget.inject(instance, ctx);
      injectionTarget.postConstruct(instance);
      return instance;
   }

   public void dispose(T instance)
   {
      injectionTarget.dispose(instance);
   }

   public Object invoke(Object instance, InvocationContext ctx) throws Exception
   {
      return handlerMethod.invoke(instance, ctx);
   }

   public Class<?> getHandlerClass()
   {
      return handlerClass;
   }

}
