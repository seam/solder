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

package org.jboss.webbeans.xsd;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

import org.dom4j.Document;
import org.dom4j.Element;
import org.jboss.webbeans.xsd.NamespaceHandler.SchemaNamespace;
import org.jboss.webbeans.xsd.model.ClassModel;
import org.jboss.webbeans.xsd.model.TypedModel;

/**
 * Package information
 * 
 * @author Nicklas Karlsson
 * 
 */
public class Schema
{
   private String packageName;
   private Document document;
   private NamespaceHandler namespaceHandler;
   private Set<ClassModel> classModels;

   public Schema(String packageName)
   {
      this.packageName = packageName;
      namespaceHandler = new NamespaceHandler(packageName);
      classModels = new HashSet<ClassModel>();
   }

   public String getPackageName()
   {
      return packageName;
   }

   public void addClass(ClassModel classModel)
   {
      classModels.add(classModel);
      for (TypedModel reference : classModel.getTypeReferences())
      {
         namespaceHandler.addPackage(reference.getTypePackage());
      }
   }

   public Document getDocument()
   {
      return document;
   }

   public void setDocument(Document document)
   {
      this.document = document;
   }

   private boolean isClassInPackage(PackageElement packageElement, String FQN)
   {
      for (javax.lang.model.element.Element classElement : packageElement.getEnclosedElements())
      {
         TypeElement typeElement = (TypeElement) classElement;
         if (typeElement.getQualifiedName().toString().equals(FQN))
         {
            return true;
         }
      }
      return false;
   }

   public void rebuild(PackageElement packageElement)
   {
      for (SchemaNamespace schemaNamespace : namespaceHandler.getSchemaNamespaces().values())
      {
         document.getRootElement().addNamespace(schemaNamespace.shortNamespace, schemaNamespace.urn);
      }

      for (Object xsdClass : document.selectNodes("//xs:schema//xs:element"))
      {
         String className = ((Element) xsdClass).attributeValue("name");
         if (!isClassInPackage(packageElement, packageName + "." + className))
         {
            ((Element) xsdClass).detach();
         }
      }

      for (ClassModel classModel : classModels)
      {
         // Remove old version of class xsd (if present)
         for (Object previousClass : document.selectNodes("//xs:schema//xs:element[@name=\"" + classModel.getSimpleName() + "\"]"))
         {
            ((Element) previousClass).detach();
         }
         document.getRootElement().add(classModel.toXSD(namespaceHandler));
      }

      /**
       * XSD: Foo Bar Tar
       * 
       * ClassModels: Foo
       * 
       * Package: Foo Bar
       * 
       * => update Foo, remove Tar
       */

      // System.out.println("Current contents of package " + packageName);
      // for (Element e : packageElement.getEnclosedElements())
      // {
      // System.out.println(e.asType().toString());
      // }
   }

   @Override
   public String toString()
   {
      StringBuilder buffer = new StringBuilder();
      buffer.append("Package: " + packageName + "\n");
      buffer.append("Used namespaces\n");
      for (SchemaNamespace schemaNamespace : namespaceHandler.getSchemaNamespaces().values())
      {
         buffer.append("  " + schemaNamespace + "\n");
      }
      buffer.append("Contained classes:\n");
      for (ClassModel classModel : classModels)
      {
         buffer.append(classModel + "\n");
      }
      return buffer.toString();
   }

}
