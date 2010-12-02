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
package org.jboss.seam.solder.servicehandler;

import java.lang.reflect.Method;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.jboss.seam.solder.reflection.Reflections;
import org.jboss.seam.solder.reflection.annotated.AnnotatedTypeBuilder;

/**
 * Manages the handler class for the service handler extension. This class is
 * responsible for managing the lifecycle of the handler class instances
 * 
 * @author Stuart Douglas
 * 
 */
class ServiceHandlerManager<T>
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
   ServiceHandlerManager(Class<T> handlerClass, BeanManager beanManager) throws IllegalArgumentException
   {
      this.handlerClass = handlerClass;
      handlerMethod = getHandlerMethod(handlerClass);
      //now create the InjectionTarget
      AnnotatedTypeBuilder<T> typeBuilder = new AnnotatedTypeBuilder<T>().readFromType(handlerClass);
      injectionTarget = beanManager.createInjectionTarget(typeBuilder.create());
   }

   T create(CreationalContext<T> ctx)
   {
      T instance = injectionTarget.produce(ctx);
      injectionTarget.inject(instance, ctx);
      injectionTarget.postConstruct(instance);
      return instance;
   }

   Object invoke(Object instance, InvocationContext ctx) throws Exception
   {
      return handlerMethod.invoke(instance, ctx);
   }

   Class<?> getHandlerClass()
   {
      return handlerClass;
   }

   private static Method getHandlerMethod(Class<?> handlerClass)
   {
      //search for the handler method
      for (Method m : Reflections.getAllDeclaredMethods(handlerClass))
      {
         if (m.isAnnotationPresent(AroundInvoke.class))
         {
            if (m.getParameterTypes().length != 1 || m.getParameterTypes()[0] != InvocationContext.class)
            {
               throw new IllegalArgumentException("Could not find suitable AroundInvoke method on class " + handlerClass + " methods denoted @AroundInvoke must have a single InvokationContext parameter");
            }
            return m;
         }
      }
      throw new IllegalArgumentException("Could not find suitable AroundInvoke method on class " + handlerClass);
   }

}
