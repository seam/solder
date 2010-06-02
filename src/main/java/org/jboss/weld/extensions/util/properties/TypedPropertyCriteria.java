package org.jboss.weld.extensions.util.properties;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A criteria that matches a property based on its type
 * 
 * @author Shane Bryzak
 */
public class TypedPropertyCriteria implements BeanPropertyCriteria
{
   private Class<?> propertyClass;
   
   public TypedPropertyCriteria(Class<?> propertyClass)
   {
      this.propertyClass = propertyClass;
   }

   public boolean fieldMatches(Field f)
   {
      return propertyClass.equals(f.getType());
   }

   public boolean methodMatches(Method m)
   {
      return propertyClass.equals(m.getReturnType());
   }
}
