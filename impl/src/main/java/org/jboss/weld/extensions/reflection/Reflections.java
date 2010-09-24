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
package org.jboss.weld.extensions.reflection;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;

/**
 * Utility class for working with Java Reflection
 * 
 * @author Stuart Douglas
 * @author Pete Muir
 * 
 */
public class Reflections
{

   public static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];

   public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

   private Reflections()
   {
   }
   
   @SuppressWarnings("unchecked")
   public static <T> T cast(Object obj)
   {
      return (T) obj;
   }

   /**
    * Get all the declared fields on the class hierarchy. This <b>will</b>
    * return overridden fields.
    * 
    * @param clazz The class to search
    * @return the set of all declared fields or an empty set if there are none
    */
   public static Set<Field> getAllDeclaredFields(Class<?> clazz)
   {
      HashSet<Field> fields = new HashSet<Field>();
      for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass())
      {
         for (Field a : c.getDeclaredFields())
         {
            fields.add(a);
         }
      }
      return fields;
   }

   /**
    * Search the class hierarchy for a field with the given name. Will return
    * the nearest match, starting with the class specified and searching up the
    * hierarchy.
    * 
    * @param clazz The class to search
    * @param name The name of the field to search for
    * @return The field found, or null if no field is found
    */
   public static Field findDeclaredField(Class<?> clazz, String name)
   {
      for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass())
      {
         try
         {
            return c.getDeclaredField(name);
         }
         catch (NoSuchFieldException e)
         {
            // No-op, we continue looking up the class hierarchy
         }
      }
      return null;
   }

   /**
    * Search the annotatedType for the field, returning the
    * {@link AnnotatedField}
    * 
    * @param annotatedType The annotatedType to search
    * @param field the field to search for
    * @return The {@link AnnotatedField} found, or null if no field is found
    */
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

   /**
    * Search for annotations with the specified meta annotation type
    * 
    * @param annotations The annotation set to search
    * @param metaAnnotationType The type of the meta annotation to search for
    * @return The set of annotations with the specified meta annotation, or an
    *         empty set if none are found
    */
   public static Set<Annotation> getAnnotationsWithMetaAnnotation(Set<Annotation> annotations, Class<? extends Annotation> metaAnnotationType)
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

   /**
    * Extract any qualifiers from the set of annotations
    * 
    * @param annotations The set of annotations to search
    * @param beanManager The beanManager to use to establish if an annotation is
    *           a qualifier
    * @return The qualifiers present in the set, or an empty set if there are
    *         none
    */
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

   /**
    * Determine if a method exists in a specified class hierarchy
    * 
    * @param clazz The class to search
    * @param name The name of the method
    * @return true if a method is found, otherwise false
    */
   public static boolean methodExists(Class<?> clazz, String name)
   {
      for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass())
      {
         for (Method m : c.getDeclaredMethods())
         {
            if (m.getName().equals(name))
            {
               return true;
            }
         }
      }
      return false;
   }

   /**
    * Get all the declared methods on the class hierarchy. This <b>will</b>
    * return overridden methods.
    * 
    * @param clazz The class to search
    * @return the set of all declared methods or an empty set if there are none
    */
   public static Set<Method> getAllDeclaredMethods(Class<?> clazz)
   {
      HashSet<Method> methods = new HashSet<Method>();
      for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass())
      {
         for (Method a : c.getDeclaredMethods())
         {
            methods.add(a);
         }
      }
      return methods;
   }

   /**
    * Search the class hierarchy for a method with the given name and arguments.
    * Will return the nearest match, starting with the class specified and
    * searching up the hierarchy.
    * 
    * @param clazz The class to search
    * @param name The name of the method to search for
    * @param args The arguments of the method to search for
    * @return The method found, or null if no method is found
    */
   public static Method findDeclaredMethod(Class<?> clazz, String name, Class<?>... args)
   {
      for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass())
      {
         try
         {
            return c.getDeclaredMethod(name, args);
         }
         catch (NoSuchMethodException e)
         {
            // No-op, continue the search
         }
      }
      return null;
   }

   /**
    * Search the class hierarchy for a constructor with the given arguments.
    * Will return the nearest match, starting with the class specified and
    * searching up the hierarchy.
    * 
    * @param clazz The class to search
    * @param args The arguments of the constructor to search for
    * @return The constructor found, or null if no constructor is found
    */
   public static Constructor<?> findDeclaredConstructor(Class<?> clazz, Class<?>... args)
   {
      for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass())
      {
         try
         {
            return c.getDeclaredConstructor(args);
         }
         catch (NoSuchMethodException e)
         {
            // No-op, continue the search
         }
      }
      return null;
   }

   /**
    * Get all the declared constructors on the class hierarchy. This <b>will</b>
    * return overridden constructors.
    * 
    * @param clazz The class to search
    * @return the set of all declared constructors or an empty set if there are
    *         none
    */
   public static Set<Constructor<?>> getAllDeclaredConstructors(Class<?> clazz)
   {
      HashSet<Constructor<?>> constructors = new HashSet<Constructor<?>>();
      for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass())
      {
         for (Constructor<?> constructor : c.getDeclaredConstructors())
         {
            constructors.add(constructor);
         }
      }
      return constructors;
   }

   /**
    * Get the type of the member
    * 
    * @param member The member
    * @return The type of the member
    * @throws UnsupportedOperationException if the member is not a field,
    *            method, or constructor
    */
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

   /**
    * Loads and initializes a class for the given name.
    * 
    * If the Thread Context Class Loader is available, it will be used,
    * otherwise the classloader used to load {@link Reflections} will be used
    * 
    * It is also possible to specify additional classloaders to attempt to load
    * the class with. If the first attempt fails, then these additional loaders
    * are tried in order.
    * 
    * @param name The name of the class to load
    * @param loaders Additional classloaders to use to attempt to load the class
    * @return The class object
    * @throws ClassNotFoundException if the class cannot be found
    */
   public static Class<?> classForName(String name, ClassLoader... loaders) throws ClassNotFoundException
   {
      try
      {
         if (Thread.currentThread().getContextClassLoader() != null)
         {
            return Class.forName(name, true, Thread.currentThread().getContextClassLoader());
         }
         else
         {
            return Class.forName(name);
         }
      }
      catch (ClassNotFoundException e)
      {
         for (ClassLoader l : loaders)
         {
            try
            {
               return Class.forName(name, true, l);
            }
            catch (ClassNotFoundException ex)
            {

            }
         }
      }
      if (Thread.currentThread().getContextClassLoader() != null)
      {
         throw new ClassNotFoundException("Could not load class " + name + " with the context class loader " + Thread.currentThread().getContextClassLoader().toString() + " or any of the additional ClassLoaders: " + Arrays.toString(loaders));
      }
      else
      {
         throw new ClassNotFoundException("Could not load class " + name + " using Class.forName or using any of the additional ClassLoaders: " + Arrays.toString(loaders));
      }
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

   public static Object invokeMethod(Method method, Object instance, Object... args)
   {
      return invokeMethod(method, Object.class, instance, args);
   }
   
   public static <T> T invokeMethod(Method method, Class<T> expectedReturnType, Object instance, Object... args)
   {
      try
      {
         return expectedReturnType.cast(method.invoke(instance, args));
      }
      catch (IllegalAccessException ex)
      {
         throw new RuntimeException(buildInvokeMethodErrorMessage(method, instance, args), ex);
      }
      catch (IllegalArgumentException ex)
      {
         throw new IllegalArgumentException(buildInvokeMethodErrorMessage(method, instance, args), ex.getCause());
      }
      catch (InvocationTargetException ex)
      {
         throw new RuntimeException(buildInvokeMethodErrorMessage(method, instance, args), ex);
      }
      catch (NullPointerException ex)
      {
         NullPointerException ex2 = new NullPointerException(buildInvokeMethodErrorMessage(method, instance, args));
         ex2.initCause(ex.getCause());
         throw ex2;
      }
      catch (ExceptionInInitializerError e)
      {
         throw new RuntimeException(buildInvokeMethodErrorMessage(method, instance, args), e);
      }
   }

   public static void setFieldValue(Field field, Object instance, Object value)
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

   private static String buildSetFieldValueErrorMessage(Field field, Object obj, Object value)
   {
      return String.format("Exception setting [%s] field on object [%s] to value [%s]", field.getName(), obj, value);
   }

   private static String buildGetFieldValueErrorMessage(Field field, Object obj)
   {
      return String.format("Exception reading [%s] field from object [%s].", field.getName(), obj);
   }

   public static Object getFieldValue(Field field, Object instance)
   {
      return getFieldValue(field, instance, Object.class);
   }
   
   public static <T> T getFieldValue(Field field, Object instance, Class<T> expectedType)
   {
      field.setAccessible(true);
      try
      {
         return expectedType.cast(field.get(instance));
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

   @SuppressWarnings("unchecked")
   public static <T> Class<T> getRawType(Type type)
   {
      if (type instanceof Class<?>)
      {
         return (Class<T>) type;
      }
      else if (type instanceof ParameterizedType)
      {
         if (((ParameterizedType) type).getRawType() instanceof Class<?>)
         {
            return (Class<T>) ((ParameterizedType) type).getRawType();
         }
      }
      return null;
   }

   /**
    * Check if a class is serializable.
    * 
    * @param clazz The class to check
    * @return true if the class implements serializable or is a primitive
    */
   public static boolean isSerializable(Class<?> clazz)
   {
      return clazz.isPrimitive() || Serializable.class.isAssignableFrom(clazz);
   }

}
