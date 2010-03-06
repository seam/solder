package org.jboss.weld.extensions.util.annotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

import javax.enterprise.inject.spi.Annotated;

/**
 * The base class for all New Annotated types.
 * 
 * @author Stuart Douglas
 * 
 */
abstract class AbstractNewAnnotatedElement implements Annotated
{

   private final Type type;
   private final Set<Type> typeClosure;
   private final AnnotationStore annotations;

   protected AbstractNewAnnotatedElement(Class<?> type, AnnotationStore annotations, Type genericType)
   {
      this.typeClosure = new TypeClosureBuilder().add(type).getTypes();
      if (genericType != null)
      {
         typeClosure.add(genericType);
         this.type = genericType;
      }
      else
      {
         this.type = type;
      }
      if (annotations == null)
      {
         this.annotations = new AnnotationStore();
      }
      else
      {
         this.annotations = annotations;
      }
   }

   public <T extends Annotation> T getAnnotation(Class<T> annotationType)
   {
      return annotations.getAnnotation(annotationType);
   }

   public Set<Annotation> getAnnotations()
   {
      return annotations.getAnnotations();
   }

   public boolean isAnnotationPresent(Class<? extends Annotation> annotationType)
   {
      return annotations.isAnnotationPresent(annotationType);
   }

   public Set<Type> getTypeClosure()
   {
      return Collections.unmodifiableSet(typeClosure);
   }

   public Type getBaseType()
   {
      return type;
   }
   
}
