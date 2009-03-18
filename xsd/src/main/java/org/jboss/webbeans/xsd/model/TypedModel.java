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

import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.jboss.webbeans.xsd.NamespaceHandler;

/**
 * The model of a typed member
 * 
 * @author Nicklas Karlsson
 * 
 */
public class TypedModel
{
   protected String type;
   protected boolean primitive;

   public TypedModel(String type, boolean primitive)
   {
      this.type = type;
      this.primitive = primitive;
   }

   public String getType()
   {
      return type;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   public boolean isPrimitive()
   {
      return primitive;
   }

   public void setPrimitive(boolean primitive)
   {
      this.primitive = primitive;
   }

   @Override
   public String toString()
   {
      return type;
   }

   public String getTypePackage()
   {
      if (primitive)
      {
         return "";
      }
      int lastDot = type.lastIndexOf(".");
      return lastDot < 0 ? "nopak" : type.substring(0, lastDot);
   }

   public String getTypeSimpleName()
   {
      int lastDot = type.lastIndexOf(".");
      return lastDot < 0 ? type : type.substring(lastDot + 1);
   }

   @Override
   public boolean equals(Object other)
   {
      TypedModel otherType = (TypedModel) other;
      return type.equals(otherType.getType()) && primitive == otherType.isPrimitive();
   }

   @Override
   public int hashCode()
   {
      return type.hashCode() + (isPrimitive() ? 0 : 1);
   }

   public Element toXSD(NamespaceHandler namespaceHandler)
   {
      if (isPrimitive()) {
         
      } else {
         
      }
      String typeOrRef = isPrimitive() ? ("xs:" + type) : (namespaceHandler.getShortNamespace(type) + ":" + getTypeSimpleName());
      String attrName = isPrimitive() ? "type" : "ref";
      Element item = DocumentFactory.getInstance().createElement("xs:element");
      item.addAttribute(attrName, typeOrRef);
      return item;
   }

   public static TypedModel of(String type, boolean primitive)
   {
      return new TypedModel(type, primitive);
   }

}
