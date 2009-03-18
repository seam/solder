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

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
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

   public String getPackageName()
   {
      return packageName;
   }

   public void setPackageName(String packageName)
   {
      this.packageName = packageName;
   }

   public Set<String> getNamespaces()
   {
      return namespaceHandler.getUsedNamespaces();
   }

   public void rebuild(PackageElement packageElement)
   {
      for (String namespace : namespaceHandler.getUsedNamespaces())
      {
         document.getRootElement().addNamespace("x", namespace);
      }
      for (ClassModel classModel : classModels)
      {
         org.dom4j.Element classElement = DocumentFactory.getInstance().createElement("element");
         classElement.addAttribute("name", classModel.getSimpleName());
         document.getRootElement().add(classElement);
      }
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
      for (String namespace : namespaceHandler.getUsedNamespaces())
      {
         buffer.append("  " + namespace + "\n");
      }
      buffer.append("Contained classes:\n");
      for (ClassModel classModel : classModels)
      {
         buffer.append(classModel + "\n");
      }
      return buffer.toString();
   }

}
