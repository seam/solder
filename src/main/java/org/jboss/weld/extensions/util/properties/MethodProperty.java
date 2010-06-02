/**
 * 
 */
package org.jboss.weld.extensions.util.properties;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * A bean property based on the value represented by a getter/setter method pair
 * 
 * @author Pete Muir
 * @author Shane Bryzak
 */
class MethodProperty implements Property
{   
   private final Method getterMethod;
   private final String propertyName;
   private final Method setterMethod;

   public MethodProperty(Method method)
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
   
   public Class<?> getPropertyClass()
   {
      return getterMethod.getReturnType();
   }
   
   public Type getBaseType()
   {
      return getterMethod.getGenericReturnType();
   }
   
   public <A extends Annotation> A getAnnotation(Class<A> annotationClass)
   {
      return getterMethod.getAnnotation(annotationClass);
   }
   
   public Object getValue(Object instance)
   {
      return getPropertyClass().cast(invokeMethod(getterMethod, instance));
   }
   
   public void setValue(Object instance, Object value) 
   {
      invokeMethod(setterMethod, instance, value);
   }
   
   private static String buildInvokeMethodErrorMessage(Method method, Object obj, Object... args)
   {
      StringBuilder message = new StringBuilder(String.format("Exception invoking method [%s] on object [%s], using arguments [", method.getName(), obj));
      if (args != null)
         for (int i = 0; i < args.length; i++)
            message.append((i > 0 ? "," : "") + args[i]);
      message.append("]");
      return message.toString();
   }
   
   private static Object invokeMethod(Method method, Object obj, Object... args)
   {
      try
      {
         return method.invoke(obj, args);
      }
      catch (IllegalAccessException ex)
      {
         throw new RuntimeException(buildInvokeMethodErrorMessage(method, obj, args), ex);
      }
      catch (IllegalArgumentException ex)
      {
         throw new IllegalArgumentException(buildInvokeMethodErrorMessage(method, obj, args), ex.getCause());
      }
      catch (InvocationTargetException ex)
      {
         throw new RuntimeException(buildInvokeMethodErrorMessage(method, obj, args), ex);
      }
      catch (NullPointerException ex)
      {
         NullPointerException ex2 = new NullPointerException(buildInvokeMethodErrorMessage(method, obj, args));
         ex2.initCause(ex.getCause());
         throw ex2;
      }
      catch (ExceptionInInitializerError e)
      {
         throw new RuntimeException(buildInvokeMethodErrorMessage(method, obj, args), e);
      }
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
      throw new IllegalArgumentException("no such setter method: " + clazz.getName() + '.' + name);
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
}