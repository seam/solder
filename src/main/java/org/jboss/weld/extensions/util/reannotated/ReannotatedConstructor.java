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

import java.lang.reflect.Constructor;

import javax.enterprise.inject.spi.AnnotatedConstructor;

/**
 * This implementation of {@link AnnotatedConstructor} is not threadsafe and any synchronization must be performed by the client
 * 
 * @author Gavin King
 *
 * @param <X>
 */
public class ReannotatedConstructor<X> extends ReannotatedCallable<X> implements AnnotatedConstructor<X>
{

   private final AnnotatedConstructor<X> constructor;

   ReannotatedConstructor(ReannotatedType<X> declaringType, AnnotatedConstructor<X> constructor)
   {
      super(declaringType, constructor.getParameters());
      this.constructor = constructor;
   }

   @Override
   protected AnnotatedConstructor<X> delegate()
   {
      return constructor;
   }

   @Override
   public Constructor<X> getJavaMember()
   {
      return constructor.getJavaMember();
   }

   @Override
   public boolean isStatic()
   {
      return constructor.isStatic();
   }

}
