package org.jboss.weld.extensions.util.properties;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * A convenience class for working with a typed property (either a field or
 * method) of a JavaBean class.
 * 
 * @author Shane Bryzak
 */
public class TypedBeanProperty<V> extends AbstractBeanProperty<V>
{   
   private static class TypedMatcher<V> implements FieldMatcher, MethodMatcher
   {
      private Class<V> propertyClass;
      
      public TypedMatcher(Class<V> propertyClass)
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
   
   public TypedBeanProperty(Class<?> targetClass, Class<V> propertyClass)
   {            
      super(targetClass, new TypedMatcher<V>(propertyClass), new TypedMatcher<V>(propertyClass));
   }
}
