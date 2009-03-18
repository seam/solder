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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.jboss.webbeans.xsd.NamespaceHandler.SchemaNamespace;
import org.jboss.webbeans.xsd.model.ClassModel;
import org.jboss.webbeans.xsd.model.TypedModel;

/**
 * A schema representation
 * 
 * @author Nicklas Karlsson
 * 
 */
public class Schema
{
   // The name of the package
   private String packageName;
   // The XSD document
   private Document document;
   // The namespace handler
   private NamespaceHandler namespaceHandler;
   // The set of classes to update
   private Set<ClassModel> classModels;

   /**
    * Creates a new package
    * 
    * @param packageName The name of the package
    */
   public Schema(String packageName)
   {
      this.packageName = packageName;
      namespaceHandler = new NamespaceHandler(packageName);
      classModels = new HashSet<ClassModel>();
   }

   /**
    * Gets the name of the package
    * 
    * @return The package name
    */
   public String getPackageName()
   {
      return packageName;
   }

   /**
    * Adds a class model to the working set and adds the referenced types to the namespace handler
    * 
    * @param classModel The class model
    */
   public void addClass(ClassModel classModel)
   {
      classModels.add(classModel);
      for (TypedModel reference : classModel.getTypeReferences())
      {
         namespaceHandler.addPackage(reference.getTypePackage());
      }
   }

   /**
    * Gets the XSD document
    * 
    * @return The XSD document
    */
   public Document getDocument()
   {
      return document;
   }

   /**
    * Sets the XSD document
    * 
    * @param document The XSD document
    */
   public void setDocument(Document document)
   {
      this.document = document;
   }

   /**
    * Checks if a class is still in the package
    * 
    * @param packageElement The package abstraction
    * @param FQN The full name of the class
    * @return True if present, false otherwise
    */
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

   /**
    * Cleans out XSD for files that are no longer present in the package
    * @param packageElement
    */
   private void cleanRemovedClasses(PackageElement packageElement)
   {
      for (Object xsdClass : document.selectNodes("/xs:schema/xs:element"))
      {
         String FQN = packageName + "." + ((Element) xsdClass).attributeValue("name");
         if (!isClassInPackage(packageElement, FQN))
         {
            ((Element) xsdClass).detach();
         }
      }
   }

   /**
    * Updates the namespaces of the XSD document
    */
   private void updateNamespaces()
   {
      // Collects the prefixes of the namspaces referenced in the classes
      List<String> referencedNamespaces = new ArrayList<String>();
      for (Object attribute : document.getRootElement().selectNodes("//@ref"))
      {
         String ref = ((Attribute) attribute).getValue();
         int colon = ref.indexOf(":");
         String namespace = colon < 0 ? "" : ref.substring(0, colon);
         referencedNamespaces.add(namespace);
      }
      
      // Collects the prefixes of the namespaces in the schema document
      List<String> currentNamespaces = new ArrayList<String>();
      for (Object item : document.getRootElement().additionalNamespaces())
      {
         Namespace namespace = (Namespace) item;
         String prefix = namespace.getPrefix();
         if (!("".equals(prefix) || "xsi".equals(prefix)))
         {
            currentNamespaces.add(namespace.getPrefix());
         }
      }
      
      // Removes the namespaces that are no longer referenced
      currentNamespaces.removeAll(referencedNamespaces);
      for (String obsolete : currentNamespaces)
      {
         document.getRootElement().remove(document.getRootElement().getNamespaceForPrefix(obsolete));
      }
      
      // Adds new namespaces if they are not already present
      for (SchemaNamespace schemaNamespace : namespaceHandler.getSchemaNamespaces().values())
      {
         if (document.getRootElement().getNamespaceForPrefix(schemaNamespace.shortNamespace) == null)
         {
            document.getRootElement().addNamespace(schemaNamespace.shortNamespace, schemaNamespace.urn);
         }
      }

   }

   /**
    * Rebuilds the schema document
    * 
    * @param packageElement The package abstraction
    */
   public void rebuild(PackageElement packageElement)
   {
      cleanRemovedClasses(packageElement);
      updateNamespaces();

      for (ClassModel classModel : classModels)
      {
         // Remove old version of class xsd (if present)
         for (Object previousClass : document.selectNodes("/xs:schema/xs:element[@name=\"" + classModel.getSimpleName() + "\"]"))
         {
            ((Element) previousClass).detach();
         }
         document.getRootElement().add(classModel.toXSD(namespaceHandler));
      }
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
