/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.extensions.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;

/**
 * class that provides a way of retrieving all methods and fields from a class
 * 
 * @author stuart
 * 
 */
public class Reflections
{

   private Reflections()
   {
   }

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

   public static <X> AnnotatedField<? super X> getField(AnnotatedType<X> annotatedType, Field field)
   {
      for (AnnotatedField<? super X> annotatedField : annotatedType.getFields())
      {
         if (annotatedField.getDeclaringType().getJavaClass().equals(field.getDeclaringClass()) && annotatedField.getJavaMember().getName().equals(field.getName()))
         {
            return annotatedField;
         }
      }
      return null;
   }

   public static Set<Annotation> getAnnotationsWithMetatype(Set<Annotation> annotations, Class<? extends Annotation> metaAnnotationType)
   {
      Set<Annotation> set = new HashSet<Annotation>();
      for (Annotation annotation : annotations)
      {
         if (annotation.annotationType().isAnnotationPresent(metaAnnotationType))
         {
            set.add(annotation);
         }
      }
      return set;
   }

   public static Set<Annotation> getQualifiers(Set<Annotation> annotations, BeanManager beanManager)
   {
      Set<Annotation> set = new HashSet<Annotation>();
      for (Annotation annotation : annotations)
      {
         if (beanManager.isQualifier(annotation.annotationType()))
         {
            set.add(annotation);
         }
      }
      return set;
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
      HashSet<Constructor<?>> ret = new HashSet<Constructor<?>>();
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

   public static Class<?> getMemberType(Member member)
   {
      if (member instanceof Field)
      {
         return ((Field) member).getType();
      }
      else if (member instanceof Method)
      {
         return ((Method) member).getReturnType();
      }
      else if (member instanceof Constructor<?>)
      {
         return ((Constructor<?>) member).getDeclaringClass();
      }
      else
      {
         throw new UnsupportedOperationException("Cannot operate on a member of type " + member.getClass());
      }
   }

   public static Class classForName(String name) throws ClassNotFoundException
   {
      if (Thread.currentThread().getContextClassLoader() != null)
      {
         return Thread.currentThread().getContextClassLoader().loadClass(name);
      }
      else
      {
         return Class.forName(name);
      }
   }

   public static Object invokeAndWrap(Method method, Object target, Object... args)
   {
      try
      {
         return method.invoke(target, args);
      }
      catch (Exception e)
      {
         if (e instanceof RuntimeException)
         {
            throw (RuntimeException) e;
         }
         else
         {
            throw new RuntimeException("exception invoking: " + method.getName(), e);
         }
      }
   }

}
