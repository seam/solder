/**
 * 
 */
package org.jboss.seam.solder.properties;

import static org.jboss.seam.solder.reflection.Reflections.getFieldValue;
import static org.jboss.seam.solder.reflection.Reflections.setFieldValue;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.security.AccessController;

import org.jboss.seam.solder.reflection.SetAccessiblePriviligedAction;

/**
 * A bean property based on the value contained in a field
 * 
 * @author Pete Muir
 * @author Shane Bryzak
 * 
 */
class FieldPropertyImpl<V> implements FieldProperty<V>
{

   private final Field field;

   FieldPropertyImpl(Field field)
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
   
   public Member getMember()
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
      return getFieldValue(field, instance, getJavaClass());
   }

   public void setValue(Object instance, V value)
   {
      setFieldValue(field, instance, value);
   }

   public Class<?> getDeclaringClass()
   {
      return field.getDeclaringClass();
   }

   public boolean isReadOnly()
   {
      return false;
   }
   
   public void setAccessible()
   {
      AccessController.doPrivileged(new SetAccessiblePriviligedAction(field));
   }

   @Override
   public String toString()
   {
      return field.toString();
   }
   
   @Override
   public int hashCode()
   {
      return field.hashCode();
   }
   
   @Override
   public boolean equals(Object obj)
   {
      return field.equals(obj);
   }
}