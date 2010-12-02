/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat, Inc., and individual contributors
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
package org.jboss.seam.solder.util.collections;

import java.util.HashSet;
import java.util.Set;

/**
 * A collection of utilities for working with Arrays that goes beyond that in
 * the JDK.
 * 
 * @author Pete Muir
 * 
 */
public class Arrays2
{

   private Arrays2()
   {
   }

   /**
    * Create a set from an array. If the array contains duplicate objects, the
    * last object in the array will be placed in resultant set.
    * 
    * @param <T> the type of the objects in the set
    * @param array the array from which to create the set
    * @return the created sets
    */
   public static <T> Set<T> asSet(T... array)
   {
      Set<T> result = new HashSet<T>();
      for (T a : array)
      {
         result.add(a);
      }
      return result;
   }

}
