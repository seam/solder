package org.jboss.webbeans.xsd.helpers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NamespaceGenerator
{
   private static final Set<String> URN_JAVA_EE = new HashSet<String>(Arrays.asList("java.lang", "java.util", "javax.annotation", "javax.inject", "javax.context", "javax.interceptor", "javax.decorator", "javax.event", "javax.ejb", "javax.persistence", "javax.xml.ws", "javax.jms", "javax.sql"));

   public Map<String, Integer> counters = new HashMap<String, Integer>();

   public String getNamespace(String packageName)
   {
      String shortName = getShortName(packageName);
      if (URN_JAVA_EE.contains(getBasePackage(packageName)))
      {
         return "xmlns=\"urn:java:ee\"";
      }
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
      return "xmlns:" + shortName + countString + "=\"java:urn:" + packageName + "\"";
   }

   private String getBasePackage(String packageName)
   {
      return packageName.substring(0, getShortName(packageName).length());
   }

   private String getShortName(String packageName)
   {
      int lastDot = packageName.lastIndexOf(".");
      return lastDot < 0 ? packageName : packageName.substring(lastDot + 1);
   }

}
