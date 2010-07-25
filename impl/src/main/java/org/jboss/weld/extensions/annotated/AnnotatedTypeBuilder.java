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
package org.jboss.weld.extensions.annotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
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

import org.jboss.weld.extensions.util.Reflections;

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
public class AnnotatedTypeBuilder<X>
{

   private Class<X> javaClass;
   private final AnnotationBuilder typeAnnotations;

   private final Map<Constructor<?>, AnnotationBuilder> constructors;
   private final Map<Constructor<?>, Map<Integer, AnnotationBuilder>> constructorParameters;
   private Map<Constructor<?>, Map<Integer, Type>> constructorParameterTypes;

   private final Map<Field, AnnotationBuilder> fields;
   private final Map<Field, Type> fieldTypes;

   private final Map<Method, AnnotationBuilder> methods;
   private final Map<Method, Map<Integer, AnnotationBuilder>> methodParameters;
   private final Map<Method, Map<Integer, Type>> methodParameterTypes;

   public AnnotatedTypeBuilder()
   {
      this.typeAnnotations = new AnnotationBuilder();
      this.constructors = new HashMap<Constructor<?>, AnnotationBuilder>();
      this.constructorParameters = new HashMap<Constructor<?>, Map<Integer, AnnotationBuilder>>();
      this.constructorParameterTypes = new HashMap<Constructor<?>, Map<Integer, Type>>();
      this.fields = new HashMap<Field, AnnotationBuilder>();
      this.fieldTypes = new HashMap<Field, Type>();
      this.methods = new HashMap<Method, AnnotationBuilder>();
      this.methodParameters = new HashMap<Method, Map<Integer, AnnotationBuilder>>();
      this.methodParameterTypes = new HashMap<Method, Map<Integer, Type>>();
   }

   public AnnotatedTypeBuilder<X> addToClass(Annotation annotation)
   {
      typeAnnotations.add(annotation);
      return this;
   }

   public AnnotatedTypeBuilder<X> removeFromClass(Class<? extends Annotation> annotationType)
   {
      typeAnnotations.remove(annotationType);
      return this;
   }

   public AnnotatedTypeBuilder<X> addToField(Field field, Annotation annotation)
   {
      if (fields.get(field) == null)
      {
         fields.put(field, new AnnotationBuilder());
      }
      fields.get(field).add(annotation);
      return this;
   }

   public AnnotatedTypeBuilder<X> addToField(AnnotatedField<? super X> field, Annotation annotation)
   {
      return addToField(field.getJavaMember(), annotation);
   }

   public AnnotatedTypeBuilder<X> removeFromField(Field field, Class<? extends Annotation> annotationType)
   {
      if (fields.get(field) != null)
      {
         fields.get(field).remove(annotationType);
      }
      return this;
   }

   public AnnotatedTypeBuilder<X> removeFromField(AnnotatedField<? super X> field, Class<? extends Annotation> annotationType)
   {
      return removeFromField(field.getJavaMember(), annotationType);
   }

   public AnnotatedTypeBuilder<X> addToMethod(Method method, Annotation annotation)
   {
      if (methods.get(method) == null)
      {
         methods.put(method, new AnnotationBuilder());
      }
      methods.get(method).add(annotation);
      return this;
   }

   public AnnotatedTypeBuilder<X> addToMethod(AnnotatedMethod<? super X> method, Annotation annotation)
   {
      return addToMethod(method.getJavaMember(), annotation);
   }

   public AnnotatedTypeBuilder<X> removeFromMethod(Method method, Class<? extends Annotation> annotationType)
   {
      if (methods.get(method) != null)
      {
         methods.get(method).remove(annotationType);
      }
      return this;
   }

   public AnnotatedTypeBuilder<X> removeFromMethod(AnnotatedMethod<? super X> method, Class<? extends Annotation> annotationType)
   {
      return removeFromMethod(method.getJavaMember(), annotationType);
   }

   public AnnotatedTypeBuilder<X> addToMethodParameter(Method method, int position, Annotation annotation)
   {
      if (!methods.containsKey(method))
      {
         methods.put(method, new AnnotationBuilder());
      }
      if (methodParameters.get(method) == null)
      {
         methodParameters.put(method, new HashMap<Integer, AnnotationBuilder>());
      }
      if (methodParameters.get(method).get(position) == null)
      {
         methodParameters.get(method).put(position, new AnnotationBuilder());
      }
      methodParameters.get(method).get(position).add(annotation);
      return this;
   }

   public AnnotatedTypeBuilder<X> removeFromMethodParameter(Method method, int position, Class<? extends Annotation> annotationType)
   {
      if (methodParameters.get(method) != null)
      {
         if (methodParameters.get(method).get(position) != null)
         {
            methodParameters.get(method).get(position).remove(annotationType);
         }
      }
      return this;
   }

   public AnnotatedTypeBuilder<X> addToConstructor(Constructor<X> constructor, Annotation annotation)
   {
      if (constructors.get(constructor) == null)
      {
         constructors.put(constructor, new AnnotationBuilder());
      }
      constructors.get(constructor).add(annotation);
      return this;
   }

   public AnnotatedTypeBuilder<X> addToConstructor(AnnotatedConstructor<X> constructor, Annotation annotation)
   {
      return addToConstructor(constructor.getJavaMember(), annotation);
   }

   public AnnotatedTypeBuilder<X> removeFromConstructor(Constructor<X> constructor, Class<? extends Annotation> annotationType)
   {
      if (constructors.get(constructor) != null)
      {
         constructors.get(constructor).remove(annotationType);
      }
      return this;
   }

   public AnnotatedTypeBuilder<X> removeFromConstructor(AnnotatedConstructor<X> constructor, Class<? extends Annotation> annotationType)
   {
      return removeFromConstructor(constructor.getJavaMember(), annotationType);
   }

   public AnnotatedTypeBuilder<X> addToConstructorParameter(Constructor<X> constructor, int position, Annotation annotation)
   {
      if (!constructors.containsKey(constructor))
      {
         constructors.put(constructor, new AnnotationBuilder());
      }
      if (constructorParameters.get(constructor) == null)
      {
         constructorParameters.put(constructor, new HashMap<Integer, AnnotationBuilder>());
      }
      if (constructorParameters.get(constructor).get(position) == null)
      {
         constructorParameters.get(constructor).put(position, new AnnotationBuilder());
      }
      constructorParameters.get(constructor).get(position).add(annotation);
      return this;
   }

   public AnnotatedTypeBuilder<X> removeFromConstructorParameter(Constructor<X> constructor, int position, Class<? extends Annotation> annotationType)
   {
      if (constructorParameters.get(constructor) != null)
      {
         if (constructorParameters.get(constructor).get(position) != null)
         {
            constructorParameters.get(constructor).get(position).remove(annotationType);
         }
      }
      return this;
   }

   public AnnotatedTypeBuilder<X> removeFromParameter(AnnotatedParameter<? super X> parameter, Class<? extends Annotation> annotationType)
   {
      if (parameter.getDeclaringCallable().getJavaMember() instanceof Method)
      {
         Method method = (Method) parameter.getDeclaringCallable().getJavaMember();
         return removeFromMethodParameter(method, parameter.getPosition(), annotationType);
      }
      if (parameter.getDeclaringCallable().getJavaMember() instanceof Constructor<?>)
      {
         @SuppressWarnings("unchecked")
         Constructor<X> constructor = (Constructor<X>) parameter.getDeclaringCallable().getJavaMember();
         return removeFromConstructorParameter(constructor, parameter.getPosition(), annotationType);
      }
      else
      {
         throw new IllegalArgumentException("Cannot remove from parameter " + parameter + " - cannot operate on member " + parameter.getDeclaringCallable().getJavaMember());
      }
   }

   public AnnotatedTypeBuilder<X> addToParameter(AnnotatedParameter<? super X> parameter, Annotation annotation)
   {
      if (parameter.getDeclaringCallable().getJavaMember() instanceof Method)
      {
         Method method = (Method) parameter.getDeclaringCallable().getJavaMember();
         return addToMethodParameter(method, parameter.getPosition(), annotation);
      }
      if (parameter.getDeclaringCallable().getJavaMember() instanceof Constructor<?>)
      {
         @SuppressWarnings("unchecked")
         Constructor<X> constructor = (Constructor<X>) parameter.getDeclaringCallable().getJavaMember();
         return addToConstructorParameter(constructor, parameter.getPosition(), annotation);
      }
      else
      {
         throw new IllegalArgumentException("Cannot remove from parameter " + parameter + " - cannot operate on member " + parameter.getDeclaringCallable().getJavaMember());
      }
   }

   public AnnotatedTypeBuilder<X> removeFromAll(Class<? extends Annotation> annotationType)
   {
      removeFromClass(annotationType);
      for (Entry<Field, AnnotationBuilder> field : fields.entrySet())
      {
         field.getValue().remove(annotationType);
      }
      for (Entry<Method, AnnotationBuilder> method : methods.entrySet())
      {
         method.getValue().remove(annotationType);
         if (methodParameters.get(method.getKey()) != null)
         {
            for (Entry<Integer, AnnotationBuilder> parameter : methodParameters.get(method.getKey()).entrySet())
            {
               parameter.getValue().remove(annotationType);
            }
         }
      }
      for (Entry<Constructor<?>, AnnotationBuilder> constructor : constructors.entrySet())
      {
         constructor.getValue().remove(annotationType);
         if (constructorParameters.get(constructor.getKey()) != null)
         {
            for (Entry<Integer, AnnotationBuilder> parameter : constructorParameters.get(constructor.getKey()).entrySet())
            {
               parameter.getValue().remove(annotationType);
            }
         }
      }
      return this;
   }
   
   public <A extends Annotation> AnnotatedTypeBuilder<X> redefine(Class<A> annotationType, AnnotationRedefiner<A> redefinition)
   {
      for (Entry<Field, AnnotationBuilder> field : fields.entrySet())
      {
         redefineAnnotationBuilder(annotationType, redefinition, field.getKey(), field.getKey().getGenericType(), field.getValue(), field.getKey().getName());
      }
      for (Entry<Method, AnnotationBuilder> method : methods.entrySet())
      {
         redefineAnnotationBuilder(annotationType, redefinition, method.getKey(), method.getKey().getGenericReturnType(), method.getValue(), method.getKey().getName());
      }
      for (Entry<Constructor<?>, AnnotationBuilder> constructor : constructors.entrySet())
      {
         redefineAnnotationBuilder(annotationType, redefinition, constructor.getKey(), constructor.getKey().getDeclaringClass(), constructor.getValue(), null);
      }
      for (Entry<Method, AnnotationBuilder> method : methods.entrySet())
      {
         if (methodParameters.get(method.getKey()) != null)
         {
            for (Entry<Integer, AnnotationBuilder> parameter : methodParameters.get(method.getKey()).entrySet())
            {
               Parameter<?> p = Parameter.create(method.getKey(), parameter.getKey());
               redefineAnnotationBuilder(annotationType, redefinition, p, p.getBaseType(), parameter.getValue(), null);
            }
         }
      }
      for (Entry<Constructor<?>, AnnotationBuilder> constructor : constructors.entrySet())
      {
         if (constructorParameters.get(constructor.getKey()) != null)
         {
            for (Entry<Integer, AnnotationBuilder> parameter : constructorParameters.get(constructor.getKey()).entrySet())
            {
               Parameter<?> p = Parameter.create(constructor.getKey(), parameter.getKey());
               redefineAnnotationBuilder(annotationType, redefinition, p, p.getBaseType(), parameter.getValue(), null);
            }
         }
      }
      return this;
   }

   protected <A extends Annotation> void redefineAnnotationBuilder(Class<A> annotationType, AnnotationRedefiner<A> redefinition, AnnotatedElement annotated, Type baseType, AnnotationBuilder builder, String elementName)
   {
      if (builder.isAnnotationPresent(annotationType))
      {
         redefinition.redefine(new RedefinitionContext<A>(annotated, baseType, builder, elementName));
      }
   }

   /**
    * Reads in from an existing AnnotatedType. Any elements not present are
    * added. The javaClass will be read in. If the annotation already exists on
    * that element in the builder the read annotation will be used.
    * 
    * @param type The type to read from
    */
   public AnnotatedTypeBuilder<X> readFromType(AnnotatedType<X> type)
   {
      return readFromType(type, true);
   }

   /**
    * Reads in from an existing AnnotatedType. Any elements not present are
    * added. The javaClass will be read in if overwrite is true. If the
    * annotation already exists on that element in the builder, overwrite
    * determines whether the original or read annotation will be used.
    * 
    * @param type The type to read from
    * @param overwrite If true, the read annotation will replace any existing
    *           annotation
    */
   public AnnotatedTypeBuilder<X> readFromType(AnnotatedType<X> type, boolean overwrite)
   {
      if (javaClass == null || overwrite)
      {
         this.javaClass = type.getJavaClass();
      }
      mergeAnnotationsOnElement(type, overwrite, typeAnnotations);
      for (AnnotatedField<? super X> field : type.getFields())
      {
         if (fields.get(field.getJavaMember()) == null)
         {
            fields.put(field.getJavaMember(), new AnnotationBuilder());
         }
         mergeAnnotationsOnElement(field, overwrite, fields.get(field.getJavaMember()));
      }
      for (AnnotatedMethod<? super X> method : type.getMethods())
      {
         if (methods.get(method.getJavaMember()) == null)
         {
            methods.put(method.getJavaMember(), new AnnotationBuilder());
         }
         mergeAnnotationsOnElement(method, overwrite, methods.get(method.getJavaMember()));
         for (AnnotatedParameter<? super X> p : method.getParameters())
         {
            if (methodParameters.get(method.getJavaMember()) == null)
            {
               methodParameters.put(method.getJavaMember(), new HashMap<Integer, AnnotationBuilder>());
            }
            if (methodParameters.get(method.getJavaMember()).get(p.getPosition()) == null)
            {
               methodParameters.get(method.getJavaMember()).put(p.getPosition(), new AnnotationBuilder());
            }
            mergeAnnotationsOnElement(p, overwrite, methodParameters.get(method.getJavaMember()).get(p.getPosition()));
         }
      }
      for (AnnotatedConstructor<? super X> constructor : type.getConstructors())
      {
         if (constructors.get(constructor.getJavaMember()) == null)
         {
            constructors.put(constructor.getJavaMember(), new AnnotationBuilder());
         }
         mergeAnnotationsOnElement(constructor, overwrite, constructors.get(constructor.getJavaMember()));
         for (AnnotatedParameter<? super X> p : constructor.getParameters())
         {
            if (constructorParameters.get(constructor.getJavaMember()) == null)
            {
               constructorParameters.put(constructor.getJavaMember(), new HashMap<Integer, AnnotationBuilder>());
            }
            if (constructorParameters.get(constructor.getJavaMember()).get(p.getPosition()) == null)
            {
               constructorParameters.get(constructor.getJavaMember()).put(p.getPosition(), new AnnotationBuilder());
            }
            mergeAnnotationsOnElement(p, overwrite, constructorParameters.get(constructor.getJavaMember()).get(p.getPosition()));
         }
      }
      return this;
   }

   /**
    * reads the annotations from an existing java type. Annotations already
    * present will be overriten
    * 
    */
   public AnnotatedTypeBuilder<X> readFromType(Class<X> underlyingType)
   {
      return readFromType(underlyingType, true);
   }

   /**
    * reads the annotations from an existing java type. If overwrite is true
    * then existing annotations will be overwritten
    * 
    */
   public AnnotatedTypeBuilder<X> readFromType(Class<X> underlyingType, boolean overwrite)
   {
      if (javaClass == null || overwrite)
      {
         this.javaClass = underlyingType;
      }
      for (Annotation annotation : underlyingType.getAnnotations())
      {
         if (overwrite || !typeAnnotations.isAnnotationPresent(annotation.annotationType()))
         {
            typeAnnotations.add(annotation);
         }
      }

      for (Field field : Reflections.getAllFields(underlyingType))
      {
         AnnotationBuilder annotationBuilder = fields.get(field);
         if (annotationBuilder == null)
         {
            annotationBuilder = new AnnotationBuilder();
            fields.put(field, annotationBuilder);
         }
         field.setAccessible(true);
         for (Annotation annotation : field.getAnnotations())
         {
            if (overwrite || !annotationBuilder.isAnnotationPresent(annotation.annotationType()))
            {
               annotationBuilder.add(annotation);
            }
         }
      }

      for (Method method : Reflections.getAllMethods(underlyingType))
      {
         AnnotationBuilder annotationBuilder = methods.get(method);
         if (annotationBuilder == null)
         {
            annotationBuilder = new AnnotationBuilder();
            methods.put(method, annotationBuilder);
         }
         method.setAccessible(true);
         for (Annotation annotation : method.getAnnotations())
         {
            if (overwrite || !annotationBuilder.isAnnotationPresent(annotation.annotationType()))
            {
               annotationBuilder.add(annotation);
            }
         }

         Map<Integer, AnnotationBuilder> parameters = methodParameters.get(method);
         if (parameters == null)
         {
            parameters = new HashMap<Integer, AnnotationBuilder>();
            methodParameters.put(method, parameters);
         }
         for (int i = 0; i < method.getParameterTypes().length; ++i)
         {
            AnnotationBuilder parameterAnnotationBuilder = parameters.get(i);
            if (parameterAnnotationBuilder == null)
            {
               parameterAnnotationBuilder = new AnnotationBuilder();
               parameters.put(i, parameterAnnotationBuilder);
            }
            for (Annotation annotation : method.getParameterAnnotations()[i])
            {
               if (overwrite || !parameterAnnotationBuilder.isAnnotationPresent(annotation.annotationType()))
               {
                  parameterAnnotationBuilder.add(annotation);
               }
            }
         }
      }

      for (Constructor<?> constructor : underlyingType.getDeclaredConstructors())
      {
         AnnotationBuilder annotationBuilder = constructors.get(constructor);
         if (annotationBuilder == null)
         {
            annotationBuilder = new AnnotationBuilder();
            constructors.put(constructor, annotationBuilder);
         }
         constructor.setAccessible(true);
         for (Annotation annotation : constructor.getAnnotations())
         {
            if (overwrite || !annotationBuilder.isAnnotationPresent(annotation.annotationType()))
            {
               annotationBuilder.add(annotation);
            }
         }
         Map<Integer, AnnotationBuilder> mparams = constructorParameters.get(constructor);
         if (mparams == null)
         {
            mparams = new HashMap<Integer, AnnotationBuilder>();
            constructorParameters.put(constructor, mparams);
         }
         for (int i = 0; i < constructor.getParameterTypes().length; ++i)
         {
            AnnotationBuilder parameterAnnotationBuilder = mparams.get(i);
            if (parameterAnnotationBuilder == null)
            {
               parameterAnnotationBuilder = new AnnotationBuilder();
               mparams.put(i, parameterAnnotationBuilder);
            }
            for (Annotation annotation : constructor.getParameterAnnotations()[i])
            {
               if (overwrite || !parameterAnnotationBuilder.isAnnotationPresent(annotation.annotationType()))
               {
                  annotationBuilder.add(annotation);
               }
            }
         }
      }
      return this;
   }

   protected void mergeAnnotationsOnElement(Annotated annotated, boolean overwriteExisting, AnnotationBuilder typeAnnotations)
   {
      for (Annotation annotation : annotated.getAnnotations())
      {
         if (typeAnnotations.getAnnotation(annotation.annotationType()) != null)
         {
            if (overwriteExisting)
            {
               typeAnnotations.remove(annotation.annotationType());
               typeAnnotations.add(annotation);
            }
         }
         else
         {
            typeAnnotations.add(annotation);
         }
      }
   }

   public AnnotatedType<X> create()
   {
      Map<Constructor<?>, Map<Integer, AnnotationStore>> constructorParameterAnnnotations = new HashMap<Constructor<?>, Map<Integer, AnnotationStore>>();
      Map<Constructor<?>, AnnotationStore> constructorAnnotations = new HashMap<Constructor<?>, AnnotationStore>();
      Map<Method, Map<Integer, AnnotationStore>> methodParameterAnnnotations = new HashMap<Method, Map<Integer, AnnotationStore>>();
      Map<Method, AnnotationStore> methodAnnotations = new HashMap<Method, AnnotationStore>();
      Map<Field, AnnotationStore> fieldAnnotations = new HashMap<Field, AnnotationStore>();

      for (Entry<Field, AnnotationBuilder> field : fields.entrySet())
      {
         fieldAnnotations.put(field.getKey(), field.getValue().create());
      }

      for (Entry<Method, AnnotationBuilder> method : methods.entrySet())
      {
         methodAnnotations.put(method.getKey(), method.getValue().create());
      }
      for (Entry<Method, Map<Integer, AnnotationBuilder>> parameters : methodParameters.entrySet())
      {
         Map<Integer, AnnotationStore> parameterAnnotations = new HashMap<Integer, AnnotationStore>();
         methodParameterAnnnotations.put(parameters.getKey(), parameterAnnotations);
         for (Entry<Integer, AnnotationBuilder> parameter : parameters.getValue().entrySet())
         {
            parameterAnnotations.put(parameter.getKey(), parameter.getValue().create());
         }
      }

      for (Entry<Constructor<?>, AnnotationBuilder> constructor : constructors.entrySet())
      {
         constructorAnnotations.put(constructor.getKey(), constructor.getValue().create());
      }
      for (Entry<Constructor<?>, Map<Integer, AnnotationBuilder>> parameters : constructorParameters.entrySet())
      {
         Map<Integer, AnnotationStore> parameterAnnotations = new HashMap<Integer, AnnotationStore>();
         constructorParameterAnnnotations.put(parameters.getKey(), parameterAnnotations);
         for (Entry<Integer, AnnotationBuilder> parameter : parameters.getValue().entrySet())
         {
            parameterAnnotations.put(parameter.getKey(), parameter.getValue().create());
         }
      }

      return new AnnotatedTypeImpl<X>(javaClass, typeAnnotations.create(), fieldAnnotations, methodAnnotations, methodParameterAnnnotations, constructorAnnotations, constructorParameterAnnnotations, fieldTypes, methodParameterTypes, constructorParameterTypes);
   }

   public void overrideFieldType(Field field, Type type)
   {
      fieldTypes.put(field, type);
   }

   public void overrideFieldType(AnnotatedField<? super X> field, Type type)
   {
      fieldTypes.put(field.getJavaMember(), type);
   }

   public AnnotatedTypeBuilder<X> overrideMethodParameterType(Method method, int position, Type type)
   {
      if (methodParameterTypes.get(method) == null)
      {
         methodParameterTypes.put(method, new HashMap<Integer, Type>());
      }
      methodParameterTypes.get(method).put(position, type);
      return this;
   }

   public AnnotatedTypeBuilder<X> overrideConstructorParameterType(Constructor<X> constructor, int position, Type type)
   {
      if (constructorParameterTypes.get(constructor) == null)
      {
         constructorParameterTypes.put(constructor, new HashMap<Integer, Type>());
      }
      constructorParameterTypes.get(constructor).put(position, type);
      return this;
   }

   public AnnotatedTypeBuilder<X> overrideParameterType(AnnotatedParameter<? super X> parameter, Type type)
   {
      if (parameter.getDeclaringCallable().getJavaMember() instanceof Method)
      {
         Method method = (Method) parameter.getDeclaringCallable().getJavaMember();
         return overrideMethodParameterType(method, parameter.getPosition(), type);
      }
      if (parameter.getDeclaringCallable().getJavaMember() instanceof Constructor<?>)
      {
         @SuppressWarnings("unchecked")
         Constructor<X> constructor = (Constructor<X>) parameter.getDeclaringCallable().getJavaMember();
         return overrideConstructorParameterType(constructor, parameter.getPosition(), type);
      }
      else
      {
         throw new IllegalArgumentException("Cannot remove from parameter " + parameter + " - cannot operate on member " + parameter.getDeclaringCallable().getJavaMember());
      }
   }

   public Class<X> getJavaClass()
   {
      return javaClass;
   }

   public AnnotatedTypeBuilder<X> setJavaClass(Class<X> javaClass)
   {
      this.javaClass = javaClass;
      return this;
   }

}
