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

import javax.lang.model.element.ExecutableElement;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.jboss.webbeans.xsd.NamespaceHandler;

/**
 * The model of a method
 * 
 * @author Nicklas Karlsson
 *
 */
public class ConstructorModel extends MethodModel
{

   protected ConstructorModel(ExecutableElement executableElement)
   {
      super(executableElement);
      name = null;
   }

   public static ConstructorModel of(ExecutableElement executableElement)
   {
      return new ConstructorModel(executableElement);
   }

   @Override
   public boolean equals(Object other)
   {
      ConstructorModel otherModel = (ConstructorModel) other;
      return parameters.equals(otherModel.getParameters());
   }

   @Override
   public int hashCode()
   {
      return parameters.hashCode();
   }

   @Override
   public Element toXSD(NamespaceHandler namespaceHandler)
   {
      Element constructor = DocumentFactory.getInstance().createElement("xs:sequence");
      for (TypedModel parameter : parameters)
      {
         constructor.add(parameter.toXSD(namespaceHandler));
      }
      return constructor;
   }

}
