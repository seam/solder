package org.jboss.weld.extensions.util.properties.query;

public class PropertyQueries
{
   
   private PropertyQueries() {}
   
   /**
    * Create a new {@link PropertyQuery}
    * 
    * @param <V>
    * @param targetClass
    * @return
    */
   public static <V> PropertyQuery<V> createPropertyQuery(Class<?> targetClass)
   {
      return new PropertyQuery<V>(targetClass);
   }

}
