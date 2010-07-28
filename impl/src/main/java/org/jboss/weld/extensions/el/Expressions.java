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
package org.jboss.weld.extensions.el;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * Bean that can be used to evaluate EL expressions
 * 
 * @author Dan Allen
 * @author Stuart Douglas
 * 
 */
public class Expressions
{
   @Inject
   private Instance<ELContext> elContext;
   @Inject
   private ExpressionFactory expressionFactory;

   public ELContext getELContext()
   {
      return elContext.get();
   }

   public <T> T evaluateValueExpression(String expression, Class<T> expectedType)
   {
      ELContext ctx = elContext.get();
      return (T) expressionFactory.createValueExpression(ctx, expression, expectedType).getValue(ctx);
   }

   public Object evaluateValueExpression(String expression)
   {
      return evaluateValueExpression(expression, Object.class);
   }

   public <T> T invokeMethodExpression(String expression, Class<T> expectedReturnType, Object[] args, Class<?>[] argTypes)
   {
      ELContext ctx = elContext.get();
      return (T) expressionFactory.createMethodExpression(ctx, expression, expectedReturnType, argTypes).invoke(ctx, args);
   }

   public <T> T invokeMethodExpression(String expression, Class<T> expectedReturnType)
   {
      return invokeMethodExpression(expression, expectedReturnType, new Object[0], new Class[0]);
   }

   public Object invokeMethodExpression(String expression)
   {
      return invokeMethodExpression(expression, Object.class, new Object[0], new Class[0]);
   }

   public Object invokeMethodExpression(String expression, Object... args)
   {
      return invokeMethodExpression(expression, Object.class, args, new Class[args.length]);
   }

   public <T> T resolveName(String name, Class<T> expectedType)
   {
      return evaluateValueExpression(toExpression(name), expectedType);
   }

   public Object resolveName(String name)
   {
      return resolveName(name, Object.class);
   }

   private String toExpression(String name)
   {
      return "#{" + name + "}";
   }
}