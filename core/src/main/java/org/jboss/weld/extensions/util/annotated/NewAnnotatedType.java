package org.jboss.weld.extensions.util.annotated;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;

/**
 * AnnotatedType implementation for adding beans in the BeforeBeanDiscovery
 * event
 * 
 * @author Stuart Douglas
 * 
 */
class NewAnnotatedType<X> extends AbstractNewAnnotatedElement implements AnnotatedType<X>
{

   private final Set<AnnotatedConstructor<X>> constructors;
   private final Set<AnnotatedField<? super X>> fields;
   private final Set<AnnotatedMethod<? super X>> methods;

   private final Class<X> javaClass;

   NewAnnotatedType(Class<X> clazz, AnnotationStore typeAnnotations, Map<Field, AnnotationStore> fieldAnnotations, Map<Method, AnnotationStore> methodAnnotations, Map<Method, Map<Integer, AnnotationStore>> methodParameterAnnotations, Map<Constructor<X>, AnnotationStore> constructorAnnotations, Map<Constructor<X>, Map<Integer, AnnotationStore>> constructorParameterAnnotations)
   {
      super(clazz, typeAnnotations);
      this.javaClass = clazz;
      this.constructors = new HashSet<AnnotatedConstructor<X>>();
      for (Constructor<?> c : clazz.getConstructors())
      {
         NewAnnotatedConstructor<X> nc = new NewAnnotatedConstructor<X>(this, c, constructorAnnotations.get(c), constructorParameterAnnotations.get(c));
         constructors.add(nc);
      }
      this.methods = new HashSet<AnnotatedMethod<? super X>>();
      for (Method m : clazz.getMethods())
      {
         NewAnnotatedMethod<X> met = new NewAnnotatedMethod<X>(this, m, methodAnnotations.get(m), methodParameterAnnotations.get(m));
         methods.add(met);
      }
      this.fields = new HashSet<AnnotatedField<? super X>>();
      for (Field f : clazz.getFields())
      {
         NewAnnotatedField<X> b = new NewAnnotatedField<X>(this, f, fieldAnnotations.get(f));
         fields.add(b);
      }
   }

   public Set<AnnotatedConstructor<X>> getConstructors()
   {
      return Collections.unmodifiableSet(constructors);
   }

   public Set<AnnotatedField<? super X>> getFields()
   {
      return Collections.unmodifiableSet(fields);
   }

   public Class<X> getJavaClass()
   {
      return javaClass;
   }

   public Set<AnnotatedMethod<? super X>> getMethods()
   {
      return Collections.unmodifiableSet(methods);
   }

}
