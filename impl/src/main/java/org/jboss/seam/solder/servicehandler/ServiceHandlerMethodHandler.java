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
import java.util.Collections;
import java.util.Map;

import javassist.util.proxy.MethodHandler;

import javax.interceptor.InvocationContext;

/**
 * MethodHandler that forwards calls to abstract methods to the service handler
 * instance
 * 
 * @author Stuart Douglas
 * 
 */
public class ServiceHandlerMethodHandler<T, H> implements MethodHandler
{

   private final ServiceHandlerManager<H> handler;
   private final H handlerInstance;

   public ServiceHandlerMethodHandler(ServiceHandlerManager<H> handler, H handlerInstance)
   {
      this.handler = handler;
      this.handlerInstance = handlerInstance;
   }

   public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable
   {
      if (proceed == null)
      {
         return handler.invoke(handlerInstance, new InvocationContextImpl(self, thisMethod, args));
      }
      return proceed.invoke(self, args);
   }

   private final class InvocationContextImpl implements InvocationContext
   {

      public InvocationContextImpl(Object target, Method method, Object[] params)
      {
         this.target = target;
         this.method = method;
         this.params = params;
      }

      private final Object target;
      private final Method method;
      private final Object[] params;

      public Map<String, Object> getContextData()
      {
         return Collections.emptyMap();
      }

      public Method getMethod()
      {
         return method;
      }

      public Object[] getParameters()
      {
         return params;
      }

      public Object getTarget()
      {
         return target;
      }

      public Object getTimer()
      {
         return null;
      }

      public Object proceed() throws Exception
      {
         throw new UnsupportedOperationException("Cannot call proceed() in AutoProxy invocation handler");
      }

      public void setParameters(Object[] params)
      {
         throw new UnsupportedOperationException("Cannot call setParameters() in AutoProxy invocation handler");
      }

   }

}
