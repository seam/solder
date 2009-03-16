/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
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

package org.jboss.webbeans.xsd.model;


/**
 * The model of a method or constrcutor parameter
 * 
 * @author  Nicklas Karlsson
 *
 */
public class ParameterModel extends FieldModel
{
   
   @Override
   public boolean equals(Object other)
   {
      TypedModel otherModel = (TypedModel) other;
      return type.equals(otherModel.getType());
   }

   @Override
   public int hashCode()
   {
      return type.hashCode();
   }   
   
   @Override
   public String toString()
   {
      String annotationString = (annotations.isEmpty()) ? "" : "@" + annotations + ": ";
      return "\n    " + annotationString + type + " " + name;
   }   

}
