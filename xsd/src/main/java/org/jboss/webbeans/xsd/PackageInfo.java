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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.jboss.webbeans.xsd.helpers.NamespaceGenerator;
import org.jboss.webbeans.xsd.model.TypedModel;

/**
 * Package information
 * 
 * @author Nicklas Karlsson
 * 
 */
public class PackageInfo
{
   private Document schema;
   private String packageName;
   private Map<String, Set<String>> typeReferences;
   private NamespaceGenerator namespaceGenerator;

   public PackageInfo(String packageName)
   {
      this.packageName = packageName;
      typeReferences = new HashMap<String, Set<String>>();
      namespaceGenerator = new NamespaceGenerator(packageName);
   }

   public void addTypeReferences(Set<TypedModel> references)
   {
      for (TypedModel reference : references)
      {
         Set<String> typeNames = typeReferences.get(reference.getTypePackage());
         if (typeNames == null)
         {
            typeNames = new HashSet<String>();
            typeReferences.put(reference.getTypePackage(), typeNames);
         }
         typeNames.add(reference.getType());
      }
   }

   public Document getSchema()
   {
      return schema;
   }

   public void setSchema(Document schema)
   {
      this.schema = schema;
   }

   public String getPackageName()
   {
      return packageName;
   }

   public void setPackageName(String packageName)
   {
      this.packageName = packageName;
   }

   public Map<String, Set<String>> getTypeReferences()
   {
      return typeReferences;
   }

   // TODO: dummy, remove
   public void refreshNamespaces()
   {
      for (String p : typeReferences.keySet())
      {
         if (!"".equals(p))
         {
            String dummy = namespaceGenerator.getShortNamespace(p);
         }
      }
   }

   public Set<String> getNamespaces()
   {
      return namespaceGenerator.getUsedNamespaces();
   }

}
