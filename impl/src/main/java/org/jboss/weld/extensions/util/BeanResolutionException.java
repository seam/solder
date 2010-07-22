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
package org.jboss.weld.extensions.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Superclass for exceptions that result from bean lookup
 * 
 * @author Stuart Douglas
 * 
 */
public class BeanResolutionException extends Exception
{
   private static final long serialVersionUID = -4376259139316630962L;
   private final Type type;
   private final Annotation[] qualifiers;

   public BeanResolutionException(Type type, Annotation[] qualifiers, String message)
   {
      this.type = type;
      this.qualifiers = qualifiers;
   }

   public Type getType()
   {
      return type;
   }

   public Annotation[] getQualifiers()
   {
      return qualifiers;
   }

}
