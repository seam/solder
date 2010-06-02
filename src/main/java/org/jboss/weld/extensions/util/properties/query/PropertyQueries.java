package org.jboss.weld.extensions.util.properties.query;

public class PropertyQueries
{
   
   private PropertyQueries() {}
   
   public static <V> PropertyQuery<V> createPropertyQuery(Class<?> targetClass)
   {
      return new PropertyQuery<V>(targetClass);
   }

}
