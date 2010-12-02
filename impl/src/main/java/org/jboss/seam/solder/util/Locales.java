package org.jboss.seam.solder.util;

import java.util.Locale;

/**
 * Utilities for working with locales.
 * 
 * @author Pete Muir
 * 
 */
public class Locales
{

   private Locales()
   {
   }

   /**
    * Utility to convert a string using the standard format for specifying a
    * Locale to a {@link Locale} object.
    * 
    * @param localeName the string providing the locale.
    * @return the encoded {@link Locale}
    */
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
