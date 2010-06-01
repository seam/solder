package org.jboss.weld.extensions.util.properties;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Base class for bean property wrappers. Non-deterministic in the case their
 * are multiple annotated properties.
 * 
 * Threadsafe.
 * 
 * @author Shane Bryzak
 */
public class AbstractBeanProperty<V>
{

   static class Matched<T>
   {
      
      private T matched;

      void setMatched(T matched)
      {
         this.matched = matched;
      }
      
      T getMatched()
      {
         return matched;
      }
      
   }

   /**
    * Subclasses should provide an implementation of FieldMatcher to determine
    * whether a Field contains the bean property
    */
   static interface FieldMatcher
   {
      boolean matches(Field field);
   }

   /**
    * Subclasses should provide an implementation of MethodMatcher to determine
    * whether a method provides the bean property
    */
   static interface MethodMatcher
   {
      boolean matches(Method method);
   }

   /**
    * Property field
    */
   private final Property<V> property;

   /**
    * 
    * @param targetClass
    */
   public AbstractBeanProperty(Class<?> targetClass, FieldMatcher fieldMatcher, MethodMatcher methodMatcher)
   {

      // First check declared fields
      for (Field field : targetClass.getDeclaredFields())
      {
         if (fieldMatcher.matches(field))
         {
            this.property = Properties.createProperty(field);
            return;
         }
      }

      // Then check public fields, in case it's inherited
      for (Field field : targetClass.getFields())
      {
         if (fieldMatcher.matches(field))
         {
            this.property = Properties.createProperty(field);
            return;
         }
      }

      // Then check public methods (we ignore private methods)
      for (Method method : targetClass.getMethods())
      {
         if (methodMatcher.matches(method))
         {
            this.property = Properties.createProperty(method);
            return;
         }
      }
      this.property = null;
   }

   /**
    * Returns true if the property has been successfully located, otherwise
    * returns false.
    * 
    * @return
    */
   public boolean exists()
   {
      return property != null;
   }

   public Property<V> getProperty()
   {
      return property;
   }

}
