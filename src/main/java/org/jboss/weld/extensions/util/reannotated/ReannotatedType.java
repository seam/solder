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
package org.jboss.weld.extensions.util.reannotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;

/**
 * This implementation of {@link AnnotatedType} is not threadsafe and any synchronization must be performed by the client
 * 
 * @author Gavin King
 *
 * @param <X>
 */
public class ReannotatedType<X> extends Reannotated implements AnnotatedType<X>
{

   private final AnnotatedType<X> type;
   private final HashMap<Class<?>, ReannotatedType<?>> types;

   private final Map<Member, ReannotatedField<? super X>> fields;
   private final Map<Member, ReannotatedMethod<? super X>> methods;
   private final Map<Member, ReannotatedConstructor<X>> constructors;

   public ReannotatedType(AnnotatedType<X> type)
   {
      this(type, new HashMap<Class<?>, ReannotatedType<?>>());
   }

   public ReannotatedType(AnnotatedType<X> type, HashMap<Class<?>, ReannotatedType<?>> types)
   {
      this.type = type;
      this.types = types;

      fields = new HashMap<Member, ReannotatedField<? super X>>();
      methods = new HashMap<Member, ReannotatedMethod<? super X>>();
      constructors = new HashMap<Member, ReannotatedConstructor<X>>();

      for (AnnotatedField<? super X> field : type.getFields())
      {
         addField(field);
      }
      for (AnnotatedMethod<? super X> method : type.getMethods())
      {
         addMethod(method);
      }
      for (AnnotatedConstructor<X> constructor : type.getConstructors())
      {
         addConstructor(constructor);
      }
   }
   
   private <Y> ReannotatedType<Y> getDeclaringType(AnnotatedMember<Y> member)
   {
      return getSupertype(member.getDeclaringType());
   }

   @SuppressWarnings("unchecked")
   private <Y> ReannotatedType<Y> getSupertype(AnnotatedType<Y> supertype)
   {
      Class<Y> memberJavaClass = supertype.getJavaClass();
      if (memberJavaClass == type.getJavaClass())
      {
         return (ReannotatedType<Y>) this;
      }
      else
      {
         ReannotatedType<Y> result = (ReannotatedType<Y>) types.get(memberJavaClass);
         if (result == null)
         {
            result = new ReannotatedType<Y>(supertype, types);
            types.put(memberJavaClass, result);
         }
         return result;
      }
   }

   private void addConstructor(AnnotatedConstructor<X> constructor)
   {
      constructors.put(constructor.getJavaMember(), new ReannotatedConstructor<X>(this, constructor));
   }

   @SuppressWarnings("unchecked")
   private void addMethod(AnnotatedMethod<? super X> method)
   {
      Class<? super X> methodJavaClass = method.getDeclaringType().getJavaClass();
      if (methodJavaClass.isAssignableFrom(type.getJavaClass()))
      {
         final ReannotatedMethod<? super X> reannotated;
         if (methodJavaClass == type.getJavaClass())
         {
            reannotated = new ReannotatedMethod(getDeclaringType(method), method);
         }
         else
         {
            reannotated = getInheritedMethod(method);
         }
         methods.put(method.getJavaMember(), reannotated);
      }
   }

   @SuppressWarnings("unchecked")
   private void addField(AnnotatedField<? super X> field)
   {
      Class<? super X> fieldJavaClass = field.getDeclaringType().getJavaClass();
      final ReannotatedField<? super X> reannotated;
      if (fieldJavaClass.isAssignableFrom(type.getJavaClass()))
      {
         if (fieldJavaClass == type.getJavaClass())
         {
            reannotated = new ReannotatedField(getDeclaringType(field), field);
         }
         else
         {
            reannotated = getInheritedField(field);
         }
         fields.put(field.getJavaMember(), reannotated);
      }
   }

   private <Y> ReannotatedField<? super Y> getInheritedField(AnnotatedField<Y> field)
   {
      return getDeclaringType(field).getField(field.getJavaMember());
   }

   private <Y> ReannotatedMethod<? super Y> getInheritedMethod(AnnotatedMethod<Y> method)
   {
      return getDeclaringType(method).getMethod(method.getJavaMember());
   }

   @Override
   protected AnnotatedType<X> delegate()
   {
      return type;
   }

   public Set<AnnotatedConstructor<X>> getConstructors()
   {
      return new HashSet<AnnotatedConstructor<X>>(constructors.values());
   }

   public Set<AnnotatedMethod<? super X>> getMethods()
   {
      return new HashSet<AnnotatedMethod<? super X>>(methods.values());
   }

   public Set<AnnotatedField<? super X>> getFields()
   {
      return new HashSet<AnnotatedField<? super X>>(fields.values());
   }

   public ReannotatedConstructor<X> getConstructor(Constructor<X> constructor)
   {
      return constructors.get(constructor);
   }

   public ReannotatedMethod<? super X> getMethod(Method constructor)
   {
      return methods.get(constructor);
   }

   public ReannotatedField<? super X> getField(Field field)
   {
      return fields.get(field);
   }

   public <Y extends Annotation> void redefineConstructors(Class<Y> annotationType, AnnotationRedefinition<Y> visitor)
   {
      for (ReannotatedConstructor<X> constructor : constructors.values())
      {
         constructor.redefine(annotationType, visitor);
      }
   }

   public <Y extends Annotation> void redefineMethods(Class<Y> annotationType, AnnotationRedefinition<Y> visitor)
   {
      for (ReannotatedMethod<? super X> method : methods.values())
      {
         method.redefine(annotationType, visitor);
      }
   }

   public <Y extends Annotation> void redefineFields(Class<Y> annotationType, AnnotationRedefinition<Y> visitor)
   {
      for (ReannotatedField<? super X> field : fields.values())
      {
         field.redefine(annotationType, visitor);
      }
   }

   public <Y extends Annotation> void redefineMembers(Class<Y> annotationType, AnnotationRedefinition<Y> visitor)
   {
      redefineFields(annotationType, visitor);
      redefineMethods(annotationType, visitor);
      redefineConstructors(annotationType, visitor);
   }

   public <Y extends Annotation> void redefineMembersAndParameters(Class<Y> annotationType, AnnotationRedefinition<Y> visitor)
   {
      redefineMembers(annotationType, visitor);
      redefineParameters(annotationType, visitor);
   }

   public <Y extends Annotation> void redefineParameters(Class<Y> annotationType, AnnotationRedefinition<Y> visitor)
   {
      for (ReannotatedMethod<? super X> method : methods.values())
      {
         method.redefineParameters(annotationType, visitor);
      }
      for (ReannotatedConstructor<X> constructor : constructors.values())
      {
         constructor.redefineParameters(annotationType, visitor);
      }
   }

   public <Y extends Annotation> void redefineAll(Class<Y> annotationType, AnnotationRedefinition<Y> visitor)
   {
      redefine(annotationType, visitor);
      redefineMembersAndParameters(annotationType, visitor);
   }

   @Override
   public Class<X> getJavaClass()
   {
      return type.getJavaClass();
   }

}
