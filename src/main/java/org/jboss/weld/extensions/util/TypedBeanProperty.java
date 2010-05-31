package org.jboss.weld.extensions.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A convenience class for working with a typed property (either a field or
 * method) of a JavaBean class.
 * 
 * @author Shane Bryzak
 */
public class TypedBeanProperty<T> extends AbstractBeanProperty
{
   private Class<?> propertyClass;
   
   public TypedBeanProperty(Class<?> cls, Class<?> propertyClass)
   {            
      super(cls);
      
      if (propertyClass == null)
      {
         throw new IllegalArgumentException("propertyClass can not be null.");
      }
      
      this.propertyClass = propertyClass;
   }

   @Override
   protected boolean fieldMatches(Field f)
   {
      return propertyClass.equals(f.getType());
   }

   @Override
   protected boolean methodMatches(Method m)
   {
      return propertyClass.equals(m.getReturnType());
   }   
}
