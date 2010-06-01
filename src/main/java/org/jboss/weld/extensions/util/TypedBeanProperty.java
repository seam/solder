package org.jboss.weld.extensions.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A convenience class for working with a typed property (either a field or
 * method) of a JavaBean class.
 * 
 * @author Shane Bryzak
 */
public class TypedBeanProperty extends AbstractBeanProperty
{   
   private static class TypedFieldMatcher implements FieldMatcher
   {
      private Class<?> propertyClass;
      
      public TypedFieldMatcher(Class<?> propertyClass)
      {
         if (propertyClass == null)
         {
            throw new IllegalArgumentException("propertyClass can not be null.");
         }
         
         this.propertyClass = propertyClass;
      }
      
      public boolean matches(Field f)
      {
         return propertyClass.equals(f.getType());
      }      
   }
   
   private static class TypedMethodMatcher implements MethodMatcher
   {
      private Class<?> propertyClass;
      
      public TypedMethodMatcher(Class<?> propertyClass)
      {
         if (propertyClass == null)
         {
            throw new IllegalArgumentException("propertyClass can not be null.");
         }
         
         this.propertyClass = propertyClass;
      }
      
      public boolean matches(Method m)
      {
         return propertyClass.equals(m.getReturnType());
      }      
   }
   
   public TypedBeanProperty(Class<?> cls, Class<?> propertyClass)
   {            
      super(cls, new TypedFieldMatcher(propertyClass), new TypedMethodMatcher(propertyClass));
   }
}
