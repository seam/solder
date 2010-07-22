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
 * Exception thrown when no beans with the given type and qualifiers could be
 * found
 * 
 * @author Stuart Douglas
 * 
 */
public class BeanNotFoundException extends BeanResolutionException
{

   private static final long serialVersionUID = -6513493477765900752L;

   public BeanNotFoundException(Type type, Annotation[] qualifiers)
   {
      super(type, qualifiers, "No bean found with type " + type + "and qualifiers " + qualifiers);

   }

}
