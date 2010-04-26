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
package org.jboss.weld.extensions.util.reannotated;

import java.lang.reflect.Method;

import javax.enterprise.inject.spi.AnnotatedMethod;

/**
 * This implementation of {@link AnnotatedMethod} is not threadsafe and any synchronization must be performed by the client
 * 
 * @author Gavin King
 *
 * @param <X>
 */
public class ReannotatedMethod<X> extends ReannotatedCallable<X> implements AnnotatedMethod<X>
{

   private final AnnotatedMethod<X> method;

   ReannotatedMethod(ReannotatedType<X> declaringType, AnnotatedMethod<X> method)
   {
      super(declaringType, method.getParameters());
      this.method = method;
   }

   @Override
   protected AnnotatedMethod<X> delegate()
   {
      return method;
   }

   @Override
   public Method getJavaMember()
   {
      return method.getJavaMember();
   }

   @Override
   public boolean isStatic()
   {
      return method.isStatic();
   }

}
