package org.jboss.weld.extensions.util.properties;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A criteria that matches a property based on name
 * 
 * @author Shane Bryzak
 */
public class NamedPropertyCriteria implements BeanPropertyCriteria
{
   private String propertyName;
   
   public NamedPropertyCriteria(String propertyName)
   {
      this.propertyName = propertyName;
   }
   
   public boolean fieldMatches(Field f)
   {
      return propertyName.equals(f.getName());
   }

   public boolean methodMatches(Method m)
   {
      return m.getName().startsWith("get") && 
         Introspector.decapitalize(m.getName().substring(3)).equals(propertyName);
   }
}
