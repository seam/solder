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
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedParameter;

/**
 * This implementation of {@link AnnotatedCallable} is not threadsafe and any synchronization should be performed by the client
 * 
 * @author Gavin King
 * @author Pete Muir
 *
 * @param <X>
 */
public abstract class ReannotatedCallable<X> extends ReannotatedMember<X> implements AnnotatedCallable<X>
{

   private final List<ReannotatedParameter<X>> parameters = new ArrayList<ReannotatedParameter<X>>();

   public ReannotatedCallable(ReannotatedType<X> declaringType, List<AnnotatedParameter<X>> params)
   {
      super(declaringType);
      for (AnnotatedParameter<X> param : params)
      {
         parameters.add(new ReannotatedParameter<X>(param, this, param.getPosition()));
      }
   }

   @Override
   protected abstract AnnotatedCallable<X> delegate();

   public List<AnnotatedParameter<X>> getParameters()
   {
      return new ArrayList<AnnotatedParameter<X>>(parameters);
   }

   public ReannotatedParameter<X> getParameter(int pos)
   {
      return parameters.get(pos);
   }

   public <Y extends Annotation> void redefineParameters(Class<Y> annotationType, AnnotationRedefinition<Y> visitor)
   {
      for (ReannotatedParameter<X> param : parameters)
      {
         param.redefine(annotationType, visitor);
      }
   }

   @Override
   public <Y extends Annotation> void redefineAll(Class<Y> annotationType, AnnotationRedefinition<Y> visitor)
   {
      redefine(annotationType, visitor);
      redefineParameters(annotationType, visitor);
   }

}
