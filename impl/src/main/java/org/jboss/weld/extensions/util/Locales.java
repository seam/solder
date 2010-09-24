package org.jboss.weld.extensions.util;

import java.util.Locale;

public class Locales
{
   
   private Locales() {}
   
   public static Locale toLocale(String localeName)
   {
      if (localeName == null)
      {
         return Locale.getDefault();
      }
      if (localeName.contains("_"))
      {
         String[] split = localeName.split("_");
         if (split.length == 2)
         {
            return new Locale(split[0], split[1]);
         }
         else if (split.length == 3)
         {
            return new Locale(split[0], split[1], split[2]);
         }
      }
      return new Locale(localeName);
   }

}
