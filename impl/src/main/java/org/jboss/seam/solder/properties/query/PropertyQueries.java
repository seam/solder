package org.jboss.seam.solder.properties.query;

/**
 * Utilities for working with property queries
 * 
 * @author Shane Bryzak
 * @author Pete Muir
 * 
 * @see PropertyQuery
 */
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
   public static <V> PropertyQuery<V> createQuery(Class<?> targetClass)
   {
      return new PropertyQuery<V>(targetClass);
   }

}
