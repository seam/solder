package org.jboss.weld.extensions.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * class that provides a way of retrieving all methods and fields from a class
 * 
 * @author stuart
 * 
 */
public class ReflectionUtils
{

   public static Set<Field> getFields(Class<?> clazz)
   {
      HashSet<Field> ret = new HashSet<Field>();
      Class<?> p = clazz;
      while (p != null && p != Object.class)
      {
         for (Field a : p.getDeclaredFields())
         {
            ret.add(a);
         }
         p = p.getSuperclass();
      }
      return ret;
   }

   public static Field getField(Class<?> parent, String name)
   {
      Class<?> p = parent;
      while (p != null && p != Object.class)
      {
         try
         {
            return p.getDeclaredField(name);
         }
         catch (Exception e1)
         {

         }
         p = p.getSuperclass();
      }
      return null;
   }

   public static boolean methodExists(Class<?> parent, String name)
   {
      Class<?> p = parent;
      while (p != null && p != Object.class)
      {
         for (Method m : p.getDeclaredMethods())
         {
            if (m.getName().equals(name))
            {
               return true;
            }
         }
         p = p.getSuperclass();
      }
      return false;
   }

   public static Set<Method> getMethods(Class<?> clazz)
   {
      HashSet<Method> ret = new HashSet<Method>();
      Class<?> p = clazz;
      while (p != null && p != Object.class)
      {
         for (Method a : p.getDeclaredMethods())
         {
            ret.add(a);
         }
         p = p.getSuperclass();
      }
      return ret;
   }

   public static Method getMethod(Class<?> parent, String name, Class<?>... args)
   {
      Class<?> p = parent;
      while (p != null && p != Object.class)
      {
         try
         {
            return p.getDeclaredMethod(name, args);
         }
         catch (Exception e1)
         {

         }
         p = p.getSuperclass();
      }
      return null;
   }

   public static Constructor<?> getConstructor(Class<?> parent, Class<?>... args)
   {
      Class<?> p = parent;
      while (p != null && p != Object.class)
      {
         try
         {
            return p.getDeclaredConstructor(args);
         }
         catch (Exception e1)
         {

         }
         p = p.getSuperclass();
      }
      return null;
   }

   public static Set<Constructor<?>> getConstructors(Class<?> clazz)
   {
      HashSet<Constructor<?>> ret = new HashSet();
      Class<?> p = clazz;
      while (p != null && p != Object.class)
      {
         for (Constructor<?> c : p.getDeclaredConstructors())
         {
            ret.add(c);
         }
         p = p.getSuperclass();
      }
      return ret;
   }
}
