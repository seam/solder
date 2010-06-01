/**
 * 
 */
package org.jboss.weld.extensions.util.properties;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

class FieldProperty<V> implements Property<V>
{
   

   private static String buildGetFieldValueErrorMessage(Field field, Object obj)
   {
      return String.format("Exception reading [%s] field from object [%s].", field.getName(), obj);
   }
   
   private static String buildSetFieldValueErrorMessage(Field field, Object obj, Object value)
   {
      return String.format("Exception setting [%s] field on object [%s] to value [%s]", field.getName(), obj, value);
   }
   
   private final Field field;

   FieldProperty(Field field)
   {
      this.field = field;
   }
   
   public String getName()
   {
      return field.getName();
   }
   
   public Type getBaseType()
   {
      return field.getGenericType();
   }
   
   public Field getAnnotatedElement()
   {
      return field;
   }
   
   @SuppressWarnings("unchecked")
   public Class<V> getJavaClass()
   {
      return (Class<V>) field.getType();
   }
   
   public V getValue(Object instance)
   {
      field.setAccessible(true);
      try
      {
         return getJavaClass().cast(field.get(instance));
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(buildGetFieldValueErrorMessage(field, instance), e);
      }
      catch (NullPointerException ex)
      {
         NullPointerException ex2 = new NullPointerException(buildGetFieldValueErrorMessage(field, instance));
         ex2.initCause(ex.getCause());
         throw ex2;
      }
   }
   
   public void setValue(Object instance, V value) 
   {
      field.setAccessible(true);
      try
      {
         field.set(instance, value);
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(buildSetFieldValueErrorMessage(field, instance, value), e);
      }
      catch (NullPointerException ex)
      {
         NullPointerException ex2 = new NullPointerException(buildSetFieldValueErrorMessage(field, instance, value));
         ex2.initCause(ex.getCause());
         throw ex2;
      }
   }
   
}