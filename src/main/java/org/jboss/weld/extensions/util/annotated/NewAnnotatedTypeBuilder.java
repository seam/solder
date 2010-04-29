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
package org.jboss.weld.extensions.util.annotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;

import org.jboss.weld.extensions.util.ReflectionUtils;

/**
 * Class for constructing a new AnnotatedType. A new instance of builder must be
 * used for each annotated type.
 * 
 * In can either be created with no annotations, or the annotations can be read
 * from the underlying class or an AnnotatedType
 * 
 * @author Stuart Douglas
 * @author Pete Muir
 * 
 */
public class NewAnnotatedTypeBuilder<X>
{
   private Map<Field, AnnotationBuilder> fields = new HashMap<Field, AnnotationBuilder>();
   private Map<Method, AnnotationBuilder> methods = new HashMap<Method, AnnotationBuilder>();
   private Map<Method, Map<Integer, AnnotationBuilder>> methodParameters = new HashMap<Method, Map<Integer, AnnotationBuilder>>();
   private Map<Constructor<X>, AnnotationBuilder> constructors = new HashMap<Constructor<X>, AnnotationBuilder>();
   private Map<Constructor<X>, Map<Integer, AnnotationBuilder>> constructorParameters = new HashMap<Constructor<X>, Map<Integer, AnnotationBuilder>>();
   private AnnotationBuilder typeAnnotations = new AnnotationBuilder();
   private Class<X> underlying;

   private Map<Field, Type> fieldTypes = new HashMap<Field, Type>();
   private Map<Method, Map<Integer, Type>> methodParameterTypes = new HashMap<Method, Map<Integer, Type>>();
   private Map<Constructor<?>, Map<Integer, Type>> constructorParameterTypes = new HashMap<Constructor<?>, Map<Integer, Type>>();

   public static <X> NewAnnotatedTypeBuilder<X> newInstance(Class<X> underlying)
   {
      return new NewAnnotatedTypeBuilder<X>(underlying);
   }

   public static <X> NewAnnotatedTypeBuilder<X> newInstance(AnnotatedType<X> underlying)
   {
      return new NewAnnotatedTypeBuilder<X>(underlying);
   }

   protected NewAnnotatedTypeBuilder(Class<X> underlying)
   {
      this.underlying = underlying;
   }

   protected NewAnnotatedTypeBuilder(AnnotatedType<X> underlying)
   {
      this.underlying = underlying.getJavaClass();

   }

   public NewAnnotatedTypeBuilder<X> readAnnotationsFromUnderlying()
   {
      for (Annotation a : underlying.getAnnotations())
      {
         typeAnnotations.add(a);
      }

      for (Field f : ReflectionUtils.getFields(underlying))
      {
         AnnotationBuilder ab = new AnnotationBuilder();
         fields.put(f, ab);
         f.setAccessible(true);
         for (Annotation a : f.getAnnotations())
         {
            ab.add(a);
         }
      }

      for (Method m : ReflectionUtils.getMethods(underlying))
      {
         AnnotationBuilder ab = new AnnotationBuilder();
         m.setAccessible(true);
         methods.put(m, ab);
         for (Annotation a : m.getAnnotations())
         {
            ab.add(a);
         }
         Map<Integer, AnnotationBuilder> mparams = new HashMap<Integer, AnnotationBuilder>();
         methodParameters.put(m, mparams);
         for (int i = 0; i < m.getParameterTypes().length; ++i)
         {
            AnnotationBuilder mab = new AnnotationBuilder();
            mparams.put(i, mab);
            for (Annotation a : m.getParameterAnnotations()[i])
            {
               mab.add(a);
            }
         }
      }

      for (Constructor m : underlying.getDeclaredConstructors())
      {
         AnnotationBuilder ab = new AnnotationBuilder();
         m.setAccessible(true);
         constructors.put(m, ab);
         for (Annotation a : m.getAnnotations())
         {
            ab.add(a);
         }
         Map<Integer, AnnotationBuilder> mparams = new HashMap<Integer, AnnotationBuilder>();
         constructorParameters.put(m, mparams);
         for (int i = 0; i < m.getParameterTypes().length; ++i)
         {
            AnnotationBuilder mab = new AnnotationBuilder();
            mparams.put(i, mab);
            for (Annotation a : m.getParameterAnnotations()[i])
            {
               mab.add(a);
            }
         }
      }
      return this;
   }

   public NewAnnotatedTypeBuilder<X> addToClass(Annotation a)
   {
      typeAnnotations.add(a);
      return this;
   }

   public NewAnnotatedTypeBuilder<X> removeFromClass(Class<? extends Annotation> annotation)
   {
      typeAnnotations.remove(annotation);
      return this;
   }

   public NewAnnotatedTypeBuilder<X> addToField(Field field, Annotation a)
   {
      AnnotationBuilder annotations = fields.get(field);
      if (annotations == null)
      {
         annotations = new AnnotationBuilder();
         fields.put(field, annotations);
      }
      annotations.add(a);
      return this;
   }

   public NewAnnotatedTypeBuilder<X> removeFromField(Field field, Class<? extends Annotation> a)
   {
      AnnotationBuilder annotations = fields.get(field);
      if (annotations != null)
      {
         annotations.remove(a);
      }
      return this;
   }

   public NewAnnotatedTypeBuilder<X> addToMethod(Method method, Annotation a)
   {
      AnnotationBuilder annotations = methods.get(method);
      if (annotations == null)
      {
         annotations = new AnnotationBuilder();
         methods.put(method, annotations);
      }
      annotations.add(a);
      return this;
   }

   public NewAnnotatedTypeBuilder<X> removeFromMethod(Method method, Class<? extends Annotation> a)
   {
      AnnotationBuilder annotations = methods.get(method);
      if (annotations != null)
      {
         annotations.remove(a);
      }
      return this;
   }

   public NewAnnotatedTypeBuilder<X> addToMethodParameter(Method method, int parameter, Annotation a)
   {
      if (!methods.containsKey(method))
      {
         methods.put(method, new AnnotationBuilder());
      }
      Map<Integer, AnnotationBuilder> anmap = methodParameters.get(method);
      if (anmap == null)
      {
         anmap = new HashMap<Integer, AnnotationBuilder>();
         methodParameters.put(method, anmap);
      }
      AnnotationBuilder annotations = anmap.get(parameter);
      if (annotations == null)
      {
         annotations = new AnnotationBuilder();
         anmap.put(parameter, annotations);
      }
      annotations.add(a);
      return this;
   }

   public NewAnnotatedTypeBuilder<X> removeFromMethodParameter(Method method, int parameter, Class<? extends Annotation> a)
   {
      Map<Integer, AnnotationBuilder> anmap = methodParameters.get(method);
      if (anmap != null)
      {
         AnnotationBuilder annotations = anmap.get(parameter);
         if (annotations != null)
         {
            annotations.remove(a);
         }
      }
      return this;
   }

   public NewAnnotatedTypeBuilder<X> addToConstructor(Constructor<X> constructor, Annotation a)
   {
      AnnotationBuilder annotations = constructors.get(constructor);
      if (annotations == null)
      {
         annotations = new AnnotationBuilder();
         constructors.put(constructor, annotations);
      }
      annotations.add(a);
      return this;
   }

   public NewAnnotatedTypeBuilder<X> removeFromConstructor(Constructor<?> constructor, Class<? extends Annotation> a)
   {
      AnnotationBuilder annotations = constructors.get(constructor);
      if (annotations != null)
      {
         annotations.remove(a);
      }
      return this;
   }

   public NewAnnotatedTypeBuilder<X> addToConstructorParameter(Constructor<X> constructor, int parameter, Annotation a)
   {
      if (!constructors.containsKey(constructor))
      {
         constructors.put(constructor, new AnnotationBuilder());
      }
      Map<Integer, AnnotationBuilder> anmap = constructorParameters.get(constructor);
      if (anmap == null)
      {
         anmap = new HashMap<Integer, AnnotationBuilder>();
         constructorParameters.put(constructor, anmap);
      }
      AnnotationBuilder annotations = anmap.get(parameter);
      if (annotations == null)
      {
         annotations = new AnnotationBuilder();
         anmap.put(parameter, annotations);
      }
      annotations.add(a);
      return this;
   }

   public NewAnnotatedTypeBuilder<X> removeFromConstructorParameter(Constructor<X> constructor, int parameter, Class<? extends Annotation> a)
   {
      Map<Integer, AnnotationBuilder> anmap = constructorParameters.get(constructor);
      if (anmap != null)
      {
         AnnotationBuilder annotations = anmap.get(parameter);
         if (annotations != null)
         {
            annotations.remove(a);
         }
      }
      return this;
   }

   public NewAnnotatedTypeBuilder<X> removeFromAll(Class<? extends Annotation> a)
   {
      removeFromClass(a);
      for (Entry<Field, AnnotationBuilder> e : fields.entrySet())
      {
         e.getValue().remove(a);
      }
      for (Entry<Method, AnnotationBuilder> e : methods.entrySet())
      {
         e.getValue().remove(a);
         Map<Integer, AnnotationBuilder> params = methodParameters.get(e.getKey());
         if (params != null)
         {
            for (Entry<Integer, AnnotationBuilder> p : params.entrySet())
            {
               p.getValue().remove(a);
            }
         }
      }
      for (Entry<Constructor<X>, AnnotationBuilder> e : constructors.entrySet())
      {
         e.getValue().remove(a);
         Map<Integer, AnnotationBuilder> params = constructorParameters.get(e.getKey());
         if (params != null)
         {
            for (Entry<Integer, AnnotationBuilder> p : params.entrySet())
            {
               p.getValue().remove(a);
            }
         }
      }
      return this;
   }

   public <T extends Annotation> NewAnnotatedTypeBuilder<X> redefineMemberParameters(Class<T> annotationType, ParameterAnnotationRedefiner<T> redefinition)
   {
      for (Entry<Method, AnnotationBuilder> e : methods.entrySet())
      {
         Map<Integer, AnnotationBuilder> params = methodParameters.get(e.getKey());
         if (params != null)
         {
            for (Entry<Integer, AnnotationBuilder> p : params.entrySet())
            {
               redefineAnnotationBuilder(annotationType, redefinition, new Parameter(e.getKey(), p.getKey()), p.getValue());
            }
         }
      }
      for (Entry<Constructor<X>, AnnotationBuilder> e : constructors.entrySet())
      {
         Map<Integer, AnnotationBuilder> params = constructorParameters.get(e.getKey());
         if (params != null)
         {
            for (Entry<Integer, AnnotationBuilder> p : params.entrySet())
            {
               redefineAnnotationBuilder(annotationType, redefinition, new Parameter(e.getKey(), p.getKey()), p.getValue());
            }
         }
      }
      return this;
   }
   
   public <T extends Annotation> NewAnnotatedTypeBuilder<X> redefineMembers(Class<T> annotationType, MemberAnnotationRedefiner<T> redefinition)
   {
      for (Entry<Field, AnnotationBuilder> e : fields.entrySet())
      {
         redefineAnnotationBuilder(annotationType, redefinition, e.getKey(), e.getValue());
      }
      for (Entry<Method, AnnotationBuilder> e : methods.entrySet())
      {
         redefineAnnotationBuilder(annotationType, redefinition, e.getKey(), e.getValue());
      }
      for (Entry<Constructor<X>, AnnotationBuilder> e : constructors.entrySet())
      {
         redefineAnnotationBuilder(annotationType, redefinition, e.getKey(), e.getValue());
      }
      return this;
   }

   protected <T extends Annotation, A> void redefineAnnotationBuilder(Class<T> annotationType, AnnotationRedefiner<T, A> redefinition, A annotated, AnnotationBuilder builder)
   {
      T an = builder.getAnnotation(annotationType);
      if (an != null)
      {
         builder.remove(annotationType);
         T newAn = redefinition.redefine(an, annotated, builder);
         if (newAn != null)
         {
            builder.add(newAn);
         }
      }
   }

   /**
    * merges the annotations from an existing AnnotatedType. If they both have
    * the same annotation on an element overwriteExisting determines which one
    * to keep
    * 
    * @param type
    * @param overwriteExisting
    * @return
    */
   public NewAnnotatedTypeBuilder<X> mergeAnnotations(AnnotatedType<X> type, boolean overwriteExisting)
   {
      mergeAnnotationsOnElement(type, overwriteExisting, typeAnnotations);
      for (AnnotatedField<? super X> field : type.getFields())
      {
         AnnotationBuilder ans = fields.get(field.getJavaMember());
         if (ans == null)
         {
            ans = new AnnotationBuilder();
            fields.put(field.getJavaMember(), ans);
         }
         mergeAnnotationsOnElement(field, overwriteExisting, ans);
      }
      for (AnnotatedMethod<? super X> method : type.getMethods())
      {
         AnnotationBuilder ans = methods.get(method.getJavaMember());
         if (ans == null)
         {
            ans = new AnnotationBuilder();
            methods.put(method.getJavaMember(), ans);
         }
         mergeAnnotationsOnElement(method, overwriteExisting, ans);
         for (AnnotatedParameter<? super X> p : method.getParameters())
         {
            Map<Integer, AnnotationBuilder> params = methodParameters.get(method.getJavaMember());
            if (params == null)
            {
               params = new HashMap<Integer, AnnotationBuilder>();
               methodParameters.put(method.getJavaMember(), params);
            }
            AnnotationBuilder builder = params.get(p.getPosition());
            if (builder == null)
            {
               builder = new AnnotationBuilder();
               params.put(p.getPosition(), builder);
            }
            mergeAnnotationsOnElement(p, overwriteExisting, builder);
         }
      }
      for (AnnotatedConstructor<? super X> constructor : type.getConstructors())
      {
         AnnotationBuilder ans = constructors.get(constructor.getJavaMember());
         if (ans == null)
         {
            ans = new AnnotationBuilder();
            constructors.put((Constructor) constructor.getJavaMember(), ans);
         }
         mergeAnnotationsOnElement(constructor, overwriteExisting, ans);
         for (AnnotatedParameter<? super X> p : constructor.getParameters())
         {
            Map<Integer, AnnotationBuilder> params = constructorParameters.get(constructor.getJavaMember());
            if (params == null)
            {
               params = new HashMap<Integer, AnnotationBuilder>();
               constructorParameters.put((Constructor) constructor.getJavaMember(), params);
            }
            AnnotationBuilder builder = params.get(p.getPosition());
            if (builder == null)
            {
               builder = new AnnotationBuilder();
               params.put(p.getPosition(), builder);
            }
            mergeAnnotationsOnElement(p, overwriteExisting, builder);
         }
      }
      return this;
   }

   protected void mergeAnnotationsOnElement(Annotated annotated, boolean overwriteExisting, AnnotationBuilder typeAnnotations)
   {
      for (Annotation a : annotated.getAnnotations())
      {
         if (typeAnnotations.getAnnotation(a.annotationType()) != null)
         {
            if (overwriteExisting)
            {
               typeAnnotations.remove(a.annotationType());
               typeAnnotations.add(a);
            }
         }
         else
         {
            typeAnnotations.add(a);
         }
      }
   }

   public AnnotatedType<X> create()
   {
      Map<Constructor<X>, Map<Integer, AnnotationStore>> constructorParameterAnnnotations = new HashMap<Constructor<X>, Map<Integer, AnnotationStore>>();
      Map<Constructor<X>, AnnotationStore> constructorAnnotations = new HashMap<Constructor<X>, AnnotationStore>();
      Map<Method, Map<Integer, AnnotationStore>> methodParameterAnnnotations = new HashMap<Method, Map<Integer, AnnotationStore>>();
      Map<Method, AnnotationStore> methodAnnotations = new HashMap<Method, AnnotationStore>();
      Map<Field, AnnotationStore> fieldAnnotations = new HashMap<Field, AnnotationStore>();

      for (Entry<Field, AnnotationBuilder> e : fields.entrySet())
      {
         fieldAnnotations.put(e.getKey(), e.getValue().create());
      }

      for (Entry<Method, AnnotationBuilder> e : methods.entrySet())
      {
         methodAnnotations.put(e.getKey(), e.getValue().create());
      }
      for (Entry<Method, Map<Integer, AnnotationBuilder>> e : methodParameters.entrySet())
      {
         Map<Integer, AnnotationStore> parameterAnnotations = new HashMap<Integer, AnnotationStore>();
         methodParameterAnnnotations.put(e.getKey(), parameterAnnotations);
         for (Entry<Integer, AnnotationBuilder> pe : e.getValue().entrySet())
         {
            parameterAnnotations.put(pe.getKey(), pe.getValue().create());
         }
      }

      for (Entry<Constructor<X>, AnnotationBuilder> e : constructors.entrySet())
      {
         constructorAnnotations.put(e.getKey(), e.getValue().create());
      }
      for (Entry<Constructor<X>, Map<Integer, AnnotationBuilder>> e : constructorParameters.entrySet())
      {
         Map<Integer, AnnotationStore> parameterAnnotations = new HashMap<Integer, AnnotationStore>();
         constructorParameterAnnnotations.put(e.getKey(), parameterAnnotations);
         for (Entry<Integer, AnnotationBuilder> pe : e.getValue().entrySet())
         {
            parameterAnnotations.put(pe.getKey(), pe.getValue().create());
         }
      }

      return new NewAnnotatedType<X>(underlying, typeAnnotations.create(), fieldAnnotations, methodAnnotations, methodParameterAnnnotations, constructorAnnotations, constructorParameterAnnnotations, fieldTypes, methodParameterTypes, constructorParameterTypes);
   }

   public void overrideFieldType(Field field, Type type)
   {
      fieldTypes.put(field, type);
   }

   public void overrideMethodParameterType(Method method, Type type, int position)
   {
      Map<Integer, Type> t = methodParameterTypes.get(method);
      if (t == null)
      {
         t = new HashMap<Integer, Type>();
         methodParameterTypes.put(method, t);
      }
      t.put(position, type);
   }

   public void overrideConstructorParameterType(Constructor<?> constructor, Type type, int position)
   {
      Map<Integer, Type> t = constructorParameterTypes.get(constructor);
      if (t == null)
      {
         t = new HashMap<Integer, Type>();
         constructorParameterTypes.put(constructor, t);
      }
      t.put(position, type);
   }

}
