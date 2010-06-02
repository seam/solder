package org.jboss.weld.extensions.util.properties;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Queries a target class for properties that match certain criteria
 * 
 * @author Shane Bryzak
 */
public class BeanPropertyQuery
{
   private Class<?> targetClass;
   private List<BeanPropertyCriteria> criteria = new ArrayList<BeanPropertyCriteria>();
   
   public BeanPropertyQuery(Class<?> targetClass)
   {
      this.targetClass = targetClass;
   }
   
   public BeanPropertyQuery addCriteria(BeanPropertyCriteria criteria)
   {
      this.criteria.add(criteria);
      return this;
   }
   
   public List<Property<?>> getResultList()
   {
      List<Property<?>> results = new ArrayList<Property<?>>();

      Class<?> cls = targetClass;
      while (!cls.equals(Object.class))
      {
         // First check declared fields
         for (Field field : cls.getDeclaredFields())
         {
            for (BeanPropertyCriteria c : criteria)
            {                     
               if (c.fieldMatches(field))
               {
                  results.add(Properties.createProperty(field));
               }
            }
         }
         
         cls = cls.getSuperclass();
      }

      // Then check public methods (we ignore private methods)
      for (Method method : targetClass.getMethods())
      {
         for (BeanPropertyCriteria c : criteria)
         {
            if (c.methodMatches(method))
            {
               results.add(Properties.createProperty(method));
            }
         }
      }      
      
      return results;
   }
}
