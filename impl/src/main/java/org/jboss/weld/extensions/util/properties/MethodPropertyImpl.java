/**
 * 
 */
package org.jboss.weld.extensions.util.properties;

import static org.jboss.weld.extensions.util.Reflections.invokeMethod;

import java.beans.Introspector;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * A bean property based on the value represented by a getter/setter method pair
 * 
 * @author Pete Muir
 * @author Shane Bryzak
 */
class MethodPropertyImpl<V> implements MethodProperty<V>
{   
   private final Method getterMethod;
   private final String propertyName;
   private final Method setterMethod;

   public MethodPropertyImpl(Method method)
   {
      if (method.getName().startsWith("get"))
      {
         this.propertyName = Introspector.decapitalize(method.getName().substring(3));
      }
      else if (method.getName().startsWith("is"))
      {
         this.propertyName = Introspector.decapitalize(method.getName().substring(2));
      }
      else
      {
         throw new IllegalArgumentException("Invalid accessor method, must start with 'get' or 'is'.  " + "Method: " + method);
      }
      this.getterMethod = getGetterMethod(method.getDeclaringClass(), propertyName);
      this.setterMethod = getSetterMethod(method.getDeclaringClass(), propertyName);      
   }
   
   public String getName()
   {
      return propertyName;
   }
   
   @SuppressWarnings("unchecked")
   public Class<V> getJavaClass()
   {
      return (Class<V>) getterMethod.getReturnType();
   }
   
   public Type getBaseType()
   {
      return getterMethod.getGenericReturnType();
   }
   
   public Method getAnnotatedElement()
   {
      return getterMethod;
   }
   
   public Member getMember()
   {
      return getterMethod;
   }
   
   public V getValue(Object instance)
   {
      return getJavaClass().cast(invokeMethod(getterMethod, instance));
   }
   
   public void setValue(Object instance, V value) 
   {
      invokeMethod(setterMethod, instance, value);
   }
   
   private static Method getSetterMethod(Class<?> clazz, String name)
   {
      Method[] methods = clazz.getMethods();
      for (Method method : methods)
      {
         String methodName = method.getName();
         if (methodName.startsWith("set") && method.getParameterTypes().length == 1)
         {
            if (Introspector.decapitalize(methodName.substring(3)).equals(name))
            {
               return method;
            }
         }
      }
      return null;
   }

   private static Method getGetterMethod(Class<?> clazz, String name)
   {
      for (Method method : clazz.getDeclaredMethods())
      {
         String methodName = method.getName();
         if (method.getParameterTypes().length == 0)
         {
            if (methodName.startsWith("get"))
            {
               if (Introspector.decapitalize(methodName.substring(3)).equals(name))
               {
                  return method;
               }
            }
            else if (methodName.startsWith("is"))
            {
               if (Introspector.decapitalize(methodName.substring(2)).equals(name))
               {
                  return method;
               }
            }
         }
      }
      throw new IllegalArgumentException("no such getter method: " + clazz.getName() + '.' + name);
   }

   public Class<?> getDeclaringClass()
   {
      return getterMethod.getDeclaringClass();
   }
   
   public boolean isReadOnly()
   {
      return setterMethod == null;
   }
   
   @Override
   public String toString()
   {
      StringBuilder builder = new StringBuilder();
      if (isReadOnly())
      {
         builder.append("read-only ").append(setterMethod.toString()).append("; ");
      }
      builder.append(getterMethod.toString());
      return builder.toString();
   }
   
   @Override
   public int hashCode()
   {
      int hash = 1;
      hash = hash * 31 + (setterMethod == null ? 0 : setterMethod.hashCode());
      hash = hash * 31 + getterMethod.hashCode();
      return hash;
   }
   
   @Override
   public boolean equals(Object obj)
   {
      if (obj instanceof MethodPropertyImpl<?>)
      {
         MethodPropertyImpl<?> that = (MethodPropertyImpl<?>) obj;
         if (this.setterMethod == null)
         {
            return that.setterMethod == null && this.getterMethod.equals(that.getterMethod);
         }
         else
         {
            return this.setterMethod.equals(that.setterMethod) && this.getterMethod.equals(that.getterMethod);
         }
      }
      else
      {
         return false;
      }
   }

}