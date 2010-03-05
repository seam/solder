package org.jboss.weld.extensions.util.annotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;

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

   public NewAnnotatedTypeBuilder(Class<X> underlying)
   {
      this(underlying, false);
   }

   public NewAnnotatedTypeBuilder(Class<X> underlying, boolean readAnnotations)
   {
      this.underlying = underlying;
      if (readAnnotations)
      {
         for (Annotation a : underlying.getAnnotations())
         {
            typeAnnotations.add(a);
         }

         for (Field f : underlying.getFields())
         {
            AnnotationBuilder ab = new AnnotationBuilder();
            fields.put(f, ab);
            for (Annotation a : f.getAnnotations())
            {
               ab.add(a);
            }
         }

         for (Method m : underlying.getMethods())
         {
            AnnotationBuilder ab = new AnnotationBuilder();
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

         for (Constructor m : underlying.getConstructors())
         {
            AnnotationBuilder ab = new AnnotationBuilder();
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

      }
   }

   public NewAnnotatedTypeBuilder(AnnotatedType<X> type)
   {
      this.underlying = type.getJavaClass();
      for (Annotation a : type.getAnnotations())
      {
         typeAnnotations.add(a);
      }

      for (AnnotatedField<? super X> f : type.getFields())
      {
         AnnotationBuilder ab = new AnnotationBuilder();
         fields.put(f.getJavaMember(), ab);
         for (Annotation a : f.getAnnotations())
         {
            ab.add(a);
         }
      }

      for (AnnotatedMethod<? super X> m : type.getMethods())
      {
         AnnotationBuilder ab = new AnnotationBuilder();
         methods.put(m.getJavaMember(), ab);
         for (Annotation a : m.getAnnotations())
         {
            ab.add(a);
         }
         Map<Integer, AnnotationBuilder> mparams = new HashMap<Integer, AnnotationBuilder>();
         methodParameters.put(m.getJavaMember(), mparams);
         for (AnnotatedParameter<? super X> p : m.getParameters())
         {
            AnnotationBuilder mab = new AnnotationBuilder();
            mparams.put(p.getPosition(), mab);
            for (Annotation a : p.getAnnotations())
            {
               mab.add(a);
            }
         }
      }

      for (AnnotatedConstructor<X> m : type.getConstructors())
      {
         AnnotationBuilder ab = new AnnotationBuilder();
         constructors.put(m.getJavaMember(), ab);
         for (Annotation a : m.getAnnotations())
         {
            ab.add(a);
         }
         Map<Integer, AnnotationBuilder> mparams = new HashMap<Integer, AnnotationBuilder>();
         constructorParameters.put(m.getJavaMember(), mparams);
         for (AnnotatedParameter<? super X> p : m.getParameters())
         {
            AnnotationBuilder mab = new AnnotationBuilder();
            mparams.put(p.getPosition(), mab);
            for (Annotation a : p.getAnnotations())
            {
               mab.add(a);
            }
         }
      }

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

   public <T extends Annotation> NewAnnotatedTypeBuilder<X> redefine(Class<T> annotationType, AnnotationRedefiner<T> redefinition)
   {
      redefineAnnotationBuilder(annotationType, redefinition, typeAnnotations);
      for (Entry<Field, AnnotationBuilder> e : fields.entrySet())
      {
         redefineAnnotationBuilder(annotationType, redefinition, e.getValue());
      }
      for (Entry<Method, AnnotationBuilder> e : methods.entrySet())
      {
         redefineAnnotationBuilder(annotationType, redefinition, e.getValue());
         Map<Integer, AnnotationBuilder> params = methodParameters.get(e.getKey());
         if (params != null)
         {
            for (Entry<Integer, AnnotationBuilder> p : params.entrySet())
            {
               redefineAnnotationBuilder(annotationType, redefinition, p.getValue());
            }
         }
      }
      for (Entry<Constructor<X>, AnnotationBuilder> e : constructors.entrySet())
      {
         redefineAnnotationBuilder(annotationType, redefinition, e.getValue());
         Map<Integer, AnnotationBuilder> params = constructorParameters.get(e.getKey());
         if (params != null)
         {
            for (Entry<Integer, AnnotationBuilder> p : params.entrySet())
            {
               redefineAnnotationBuilder(annotationType, redefinition, p.getValue());
            }
         }
      }
      return this;
   }

   protected <T extends Annotation> void redefineAnnotationBuilder(Class<T> annotationType, AnnotationRedefiner<T> redefinition, AnnotationBuilder builder)
   {
      T an = builder.getAnnotation(annotationType);
      if(an != null)
      {
         builder.remove(annotationType);
         T newAn = redefinition.redefine(an, builder);
         if (newAn != null)
         {
            builder.add(newAn);
         }
      }
   }

   /**
    * merges the annotations from an existing AnnoatedType. If they both have the same annotation
    * on an element overwriteExisting determines which one to keep
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
            if(builder == null)
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
      Map<Constructor<X>, Map<Integer, AnnotationStore>> constructorParameterAnnnotations = new HashMap<Constructor<X>, Map<Integer,AnnotationStore>>();
      Map<Constructor<X>, AnnotationStore> constructorAnnotations = new HashMap<Constructor<X>, AnnotationStore>();
      Map<Method, Map<Integer, AnnotationStore>> methodParameterAnnnotations = new HashMap<Method, Map<Integer,AnnotationStore>>();
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

      return new NewAnnotatedType<X>(underlying, typeAnnotations.create(), fieldAnnotations, methodAnnotations, methodParameterAnnnotations, constructorAnnotations, constructorParameterAnnnotations);
   }

}
