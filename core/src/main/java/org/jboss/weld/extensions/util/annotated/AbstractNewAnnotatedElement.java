package org.jboss.weld.extensions.util.annotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.inject.spi.Annotated;

/**
 * The base class for all New Annotated types.
 * 
 * @author Stuart Douglas
 * 
 */
public abstract class AbstractNewAnnotatedElement implements Annotated
{

   private final Class<?> type;

   private final TypeStore typeStore = new TypeStore();
   private final AnnotationStore annotations = new AnnotationStore();

   /**
    * Clears all annotation data from the the class. Useful if we are not
    * interested in the annotations that are actually on the class but instead
    * want to apply our own
    */
   public void clearAllAnnotations()
   {
      annotations.clear();
   }

   protected AbstractNewAnnotatedElement(Class<?> type, boolean readAnnotations)
   {
      typeStore.add(type);
      this.type = type;
      if (readAnnotations)
      {
         annotations.addAll(type);
      }
   }

   public void addAnnotation(Annotation a)
   {
      annotations.addAnnotation(a);
   }

   public void removeAnnotation(Class<? extends Annotation> c)
   {
      annotations.removeAnnotation(c);
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
      return typeStore.getTypes();
   }

   public Type getBaseType()
   {
      return type;
   }
}
