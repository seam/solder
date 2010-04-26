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

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;

import javax.enterprise.inject.spi.AnnotatedMember;

/**
 * This implementation of {@link AnnotatedMember} is not threadsafe and any synchronization must be performed by the client
 * 
 * @author Gavin King
 *
 * @param <X>
 */
public abstract class ReannotatedMember<X> extends Reannotated implements AnnotatedMember<X>
{

   private final ReannotatedType<X> declaringType;

   ReannotatedMember(ReannotatedType<X> declaringType)
   {
      this.declaringType = declaringType;
   }

   public ReannotatedType<X> getDeclaringType()
   {
      return declaringType;
   }

   @Override
   protected abstract AnnotatedMember<X> delegate();

   public Member getJavaMember()
   {
      return delegate().getJavaMember();
   }

   public boolean isStatic()
   {
      return delegate().isStatic();
   }

   @Override
   public Class<?> getJavaClass()
   {
      return getJavaMember().getDeclaringClass();
   }

   public abstract <Y extends Annotation> void redefineAll(Class<Y> annotationType, AnnotationRedefinition<Y> visitor);

}
