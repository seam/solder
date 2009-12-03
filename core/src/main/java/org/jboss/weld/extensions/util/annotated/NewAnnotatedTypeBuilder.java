package org.jboss.weld.extensions.util.annotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.inject.spi.AnnotatedType;

/**
 * Class for constructing a new AnnotatedType. A new instance of builder must be
 * used for each annotated type.
 * 
 * No annotations will be read from the underlying class definition, all
 * annotations must be added explicitly
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
      this.underlying = underlying;
   }

   public NewAnnotatedTypeBuilder<X> addToClass(Annotation a)
   {
      typeAnnotations.add(a);
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

   public NewAnnotatedTypeBuilder<X> addToMethodParameter(Method method, int parameter, Annotation a)
   {
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

   public NewAnnotatedTypeBuilder<X> addToConstructorParameter(Constructor<X> constructor, int parameter, Annotation a)
   {
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
