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
package org.jboss.weld.extensions.annotatedType;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * A type closure builder
 * 
 * @author Stuart Douglas
 * 
 */
class TypeClosureBuilder
{

   private final Set<Type> types;
   
   TypeClosureBuilder()
   {
      this.types = new HashSet<Type>();
   }

   TypeClosureBuilder add(Type type)
   {
      types.add(type);
      return this;
   }

   TypeClosureBuilder add(Class<?> beanType)
   {
      for (Class<?> c = beanType; c != Object.class && c != null; c = c.getSuperclass())
      {
         types.add(c);
      }
      for (Class<?> i : beanType.getInterfaces())
      {
         types.add(i);
      }
      return this;
   }

   TypeClosureBuilder addInterfaces(Class<?> beanType)
   {
      for (Class<?> i : beanType.getInterfaces())
      {
         types.add(i);
      }
      return this;
   }

   Set<Type> getTypes()
   {
      return types;
   }

}
