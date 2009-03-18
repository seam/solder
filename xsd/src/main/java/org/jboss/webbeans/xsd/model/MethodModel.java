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

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.jboss.webbeans.xsd.NamespaceHandler;

/**
 * The model of a method
 * 
 * @author Nicklas Karlsson
 * 
 */
public class MethodModel extends NamedModel
{
   private List<TypedModel> parameters = new ArrayList<TypedModel>();

   public MethodModel(String name)
   {
      super(name);
   }

   public List<TypedModel> getParameters()
   {
      return parameters;
   }

   public void addParameter(TypedModel parameter)
   {
      parameters.add(parameter);
   }

   @Override
   public boolean equals(Object other)
   {
      MethodModel otherModel = (MethodModel) other;
      return name.equals(otherModel.getName()) && parameters.equals(otherModel.getParameters());
   }

   @Override
   public int hashCode()
   {
      return name.hashCode() + parameters.hashCode();
   }

   @Override
   public String toString()
   {
      return "\n  " + name + "(" + (parameters.isEmpty() ? "" : parameters) + ")";
   }

   @Override
   public Element toXSD(NamespaceHandler namespaceHandler)
   {
      Element method = super.toXSD(namespaceHandler);
      for (TypedModel parameter : parameters)
      {
         method.add(parameter.toXSD(namespaceHandler));
      }
      return method;
   }

}
