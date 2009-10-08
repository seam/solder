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

package org.jboss.weld.xsd;

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
   // The packages that belong to urn:java:ee
   private static final Set<String> URN_JAVA_EE = new HashSet<String>(Arrays.asList("java.lang", "java.util", "javax.annotation", "javax.inject", "javax.context", "javax.interceptor", "javax.decorator", "javax.event", "javax.ejb", "javax.persistence", "javax.xml.ws", "javax.jms", "javax.sql"));

   // The package of the schema
   private String localPackage;
   // The namespaces currently in use mapped by package name
   private Map<String, Namespace> namespaces = new HashMap<String, Namespace>();

   /**
    * Creates a new namespace handler
    * 
    * @param localPackage The local package
    */
   public NamespaceHandler(String localPackage)
   {
      this.localPackage = localPackage;
      addNamespace(new Namespace("", localPackage));
   }
   
   /**
    * Gets the used namespaces
    * 
    * @return The namespaces
    */
   public Collection<Namespace> getNamespaces()
   {
      return namespaces.values();
   }

   /**
    * Gets the prefix for a package
    * 
    * @param packageName The package name
    * @return The prefix
    */
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

   /**
    * Gets the last part of a package name
    * 
    * @param packageName The package name
    * @return The part after the last dot
    */
   private String getPackageLastPart(String packageName)
   {
      int lastDot = packageName.lastIndexOf(".");
      return lastDot < 0 ? packageName : packageName.substring(lastDot + 1);
   }

   /**
    * Gets the package name of a FQCN
    * 
    * @param FQCN The FQCN
    * @return The package part
    */
   private String getPackageName(String FQCN)
   {
      int lastDot = FQCN.lastIndexOf(".");
      return lastDot < 0 ? "nopak" : FQCN.substring(0, lastDot);
   }

   /**
    * Adds a package to the namespace handler
    * 
    * @param packageName The package
    */
   public void addPackage(String packageName)
   {
      // Already handled, exit early
      if (namespaces.containsKey(packageName))
      {
         return;
      }
      String prefix = null;
      String URI = "java:urn:" + packageName;
      // The local package, prefix is blank and uri is package name
      if (localPackage.equals(packageName))
      {
         prefix = "";
      }
      // EE stuff, prefix is ee and urn is ee
      else if (URN_JAVA_EE.contains(packageName))
      {
         prefix = "ee";
         URI = "java:urn:ee";
      }
      // Another package, get available prefix
      else
      {
         prefix = getAvailablePrefix(packageName);
      }
      namespaces.put(packageName, new Namespace(prefix, URI));
   }

   /**
    * Gets an available namespace prefix for a package name
    * 
    * @param packageName The package name to search prefix for
    * @return An available prefix
    */
   private String getAvailablePrefix(String packageName)
   {
      int suffix = 1;
      boolean found = false;
      /**
       * If we search for a prefix for com.acme.foo, we iterate over the namespaces and see
       * if "foo" is present, if so, we try with foo2 etc until it found to be available
       */
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

   /**
    * Adds a namespace to the map of known namespaces
    * 
    * @param namespace The namespace to add
    */
   public void addNamespace(Namespace namespace)
   {
      namespaces.put(getNamespacePackage(namespace), namespace);
   }

   /**
    * Gets the package from a namespace
    * 
    * @param namespace The namespace to examine
    * @return The package name
    */
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
   
   public static void main(String[] x) {
      NamespaceHandler nh = new NamespaceHandler("foo");
      nh.addPackage("org.jboss.webbeans.xsd.test.test.test");
      nh.addPackage("org.jboss.webbeans.xsd.test.test.test");
      nh.addPackage("org.jboss.webbeans.xsd.test.test");
      nh.addPackage("org.jboss.webbeans.xsd.test.test");
   }

}
