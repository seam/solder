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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dom4j.Namespace;

/**
 * Helper for generating and keeping track of namespaces in a schema
 * 
 * @author Nicklas Karlsson
 * 
 */
public class NamespaceHandler
{
   private static final Set<String> URN_JAVA_EE = new HashSet<String>(Arrays.asList("java.lang", "java.util", "javax.annotation", "javax.inject", "javax.context", "javax.interceptor", "javax.decorator", "javax.event", "javax.ejb", "javax.persistence", "javax.xml.ws", "javax.jms", "javax.sql"));

   private String localPackage;
   private Map<String, Namespace> namespaces = new HashMap<String, Namespace>();

   public Collection<Namespace> getNamespaces()
   {
      return namespaces.values();
   }

   public NamespaceHandler(String localPackage)
   {
      this.localPackage = localPackage;
      addNamespace(new Namespace("", localPackage));
   }

   public String getPrefix(String packageName)
   {
      String shortName = getPackageName(packageName);
      if (namespaces.containsKey(shortName))
      {
         return namespaces.get(shortName).getPrefix();
      }
      else
      {
         throw new IllegalArgumentException("Package name " + packageName + " is not known to namespace handler of package " + localPackage);
      }
   }

   private String getPackageLastPart(String packageName)
   {
      int lastDot = packageName.lastIndexOf(".");
      return lastDot < 0 ? packageName : packageName.substring(lastDot + 1);
   }

   private String getPackageName(String FQCN)
   {
      int lastDot = FQCN.lastIndexOf(".");
      return lastDot < 0 ? "nopak" : FQCN.substring(0, lastDot);
   }

   public void addPackage(String packageName)
   {
      if (namespaces.containsKey(packageName))
      {
         return;
      }
      String prefix = null;
      String URI = "java:urn:" + packageName;
      if (localPackage.equals(packageName))
      {
         prefix = "";
      }
      else if (URN_JAVA_EE.contains(packageName))
      {
         prefix = "ee";
         URI = "java:urn:ee";
      }
      else
      {
         prefix = getAvailablePrefix(packageName);
      }
      namespaces.put(packageName, new Namespace(prefix, URI));
   }

   private String getAvailablePrefix(String packageName)
   {
      int suffix = 1;
      boolean found = false;
      while (true)
      {
         String prefix = getPackageLastPart(packageName) + (suffix == 1 ? "" : String.valueOf(suffix));
         for (Namespace namespace : namespaces.values())
         {
            if (namespace.getPrefix().equals(prefix))
            {
               found = true;
               break;
            }
         }
         if (!found)
         {
            return prefix;
         }
         else
         {
            suffix++;
            found = false;
         }
      }
   }

   public void addNamespace(Namespace namespace)
   {
      namespaces.put(getNamespacePackage(namespace), namespace);
   }

   private String getNamespacePackage(Namespace namespace)
   {
      int urnJava = namespace.getURI().indexOf("urn:java:");
      if (urnJava >= 0)
      {
         return namespace.getURI().substring(urnJava);
      }
      else
      {
         return namespace.getURI();
      }
   }

   @Override
   public String toString()
   {
      return namespaces.toString();
   }
   
}
