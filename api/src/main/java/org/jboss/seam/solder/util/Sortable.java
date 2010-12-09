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
package org.jboss.seam.solder.util;

/**
 * A Sortable class is given a precedence which is used to decide it's relative
 * order
 * 
 * @author Pete Muir
 * 
 */
public interface Sortable
{

   /**
    * A comparator which can order Sortables
    * 
    * @author Pete Muir
    * 
    */
   public class Comparator implements java.util.Comparator<Sortable>
   {
      public int compare(Sortable arg1, Sortable arg2)
      {
         return -1 * Integer.valueOf(arg1.getPrecedence()).compareTo(Integer.valueOf(arg2.getPrecedence()));
      }
   }

   /**
    * An integer precedence value that indicates how favorable the
    * implementation considers itself amongst alternatives. A higher value is a
    * higher precedence. If two implementations have the save precedence, the
    * order is undetermined.
    */
   public int getPrecedence();

}