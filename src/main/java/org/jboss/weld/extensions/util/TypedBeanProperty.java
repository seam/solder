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
   private static class TypedMatcher implements FieldMatcher, MethodMatcher
   {
      private Class<?> propertyClass;
      
      public TypedMatcher(Class<?> propertyClass)
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
      
      public boolean matches(Method m)
      {
         return propertyClass.equals(m.getReturnType());
      }        
   }
   
   public TypedBeanProperty(Class<?> cls, Class<?> propertyClass)
   {            
      super(cls, new TypedMatcher(propertyClass), new TypedMatcher(propertyClass));
   }
}
