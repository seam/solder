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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Helper for generating and keeping track of namespaces in a schema
 * 
 * @author Nicklas Karlsson
 * 
 */
public class NamespaceHandler
{
   // The set of reserved EE packages
   private static final Set<String> URN_JAVA_EE = new HashSet<String>(Arrays.asList("java.lang", "java.util", "javax.annotation", "javax.inject", "javax.context", "javax.interceptor", "javax.decorator", "javax.event", "javax.ejb", "javax.persistence", "javax.xml.ws", "javax.jms", "javax.sql"));

   // The local package of the scema
   private String localPackage;

   // Duplicate shortname counters
   private Map<String, Integer> counters = new HashMap<String, Integer>();
   // Namespace infos
   private Map<String, SchemaNamespace> schemaNamespaces = new HashMap<String, SchemaNamespace>();

   /**
    * Creats a new namespace generator
    * 
    * @param localPackage The local package
    */
   public NamespaceHandler(String localPackage)
   {
      this.localPackage = localPackage;
      addPackage(localPackage);
   }

   /**
    * Data for a package namespace
    * 
    * @author Nicklas Karlsson
    * 
    */
   private class SchemaNamespace
   {
      // The package name
      String packageName;
      // The full namespace
      String namespace;
      // The namespace abbreviation
      String shortNamespace;
      // Is this a EE reserved package?
      boolean ee;

      public SchemaNamespace(String packageName, String shortNamespace, boolean ee)
      {
         this.packageName = packageName;
         this.shortNamespace = shortNamespace;
         // Skip ":" for default namespace
         String colon = "".equals(shortNamespace) ? "" : ":";
         // Hardcode "ee" for EE reserved packages
         String url = ee ? "ee" : packageName;
         this.namespace = "xmlns" + colon + shortNamespace + "=\"urn:java:" + url + "\"";
         this.ee = ee;
      }
   }

   /**
    * Gets all used namespaces for the schema
    * 
    * @return The used namespaces
    */
   public Set<String> getUsedNamespaces()
   {
      Set<String> usedNamespaces = new HashSet<String>();
      for (SchemaNamespace schemaNamespace : schemaNamespaces.values())
      {
         usedNamespaces.add(schemaNamespace.namespace);
      }
      return usedNamespaces;
   }

   /**
    * Gets a namespace abbreviation for a package
    * 
    * @param packageName The name of the package
    * @return The namespace abbreviation
    */
   public String getShortNamespace(String packageName)
   {
      if (schemaNamespaces.containsKey(packageName))
      {
         return schemaNamespaces.get(packageName).shortNamespace;
      }
      else
      {
         throw new IllegalArgumentException("Package name " + packageName + " is not known to namespace handler of package " + localPackage);
      }
   }

   /**
    * Gets the short name (last part) of a package
    * 
    * @param packageName The package name to parse
    * @return The short name
    */
   private String getShortName(String packageName)
   {
      int lastDot = packageName.lastIndexOf(".");
      return lastDot < 0 ? packageName : packageName.substring(lastDot + 1);
   }

   // TODO testing, remove
   public static void main(String[] params)
   {
      NamespaceHandler ng = new NamespaceHandler("com.acme.foo");
      System.out.println(ng.getShortNamespace("com.acme.foo"));
      System.out.println(ng.getShortNamespace("com.acme.foo.foo"));
      System.out.println(ng.getShortNamespace("com.acme.foo.foo.foo"));
      System.out.println(ng.getShortNamespace("java.util"));
      for (String ns : ng.getUsedNamespaces())
      {
         System.out.println(ns);
      }
   }

   public void addPackage(String packageName)
   {
      if (schemaNamespaces.containsKey(packageName))
      {
         return;
      }
      String shortNamespace = "";
      boolean ee = false;
      if (localPackage.equals(packageName))
      {
         // Nothing to do but want to hit this case first for performance
      }
      else if (URN_JAVA_EE.contains(packageName))
      {
         shortNamespace = "ee";
         ee = true;
      }
      else
      {
         String shortName = getShortName(packageName);
         Integer count = counters.get(shortName);
         String countString = "";
         if (count == null)
         {
            count = new Integer(1);
            counters.put(shortName, count);
         }
         else
         {
            count++;
            countString = String.valueOf(count);
         }
         shortNamespace = getShortName(packageName) + countString;
      }
      schemaNamespaces.put(packageName, new SchemaNamespace(packageName, shortNamespace, ee));
   }

}
