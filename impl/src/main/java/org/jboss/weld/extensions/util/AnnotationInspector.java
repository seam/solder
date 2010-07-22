package org.jboss.weld.extensions.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.spi.BeanManager;

public class AnnotationInspector
{
   
   private AnnotationInspector() {}
   
   /**
    * Discover if a AnnotatedElement <b>element</b> has been annotated with <b>annotationType</b>. This
    * also discovers annotations defined through a @{@link Stereotype} and the CDI SPI.
    *
    * @param element The element to inspect.
    * @param annotationType
    * @param metaAnnotation Whether the annotation may be used as a meta-annotation or not
    *
    * @return true if annotation is present either on the method itself. Returns false if the annotation
    * is not present
    * @throws IllegalArgumentException if element or annotationType is null
    */
   public static boolean isAnnotationPresent(AnnotatedElement element, Class<? extends Annotation> annotationType, boolean metaAnnotation, BeanManager beanManager)
   {
      return getAnnotation(element, annotationType, metaAnnotation, beanManager) != null;
   }

   /**
    * Inspect AnnoatedElement <b>element</b> for a specific <b>type</b> of annotation. This
    * also discovers annotations defined through a @ {@link Stereotype} and the CDI SPI.
    *
    * @param element The element to inspect
    * @param annotationType The annotation type to check for
    * @param metaAnnotation Whether the annotation may be used as a meta-annotation or not
    *
    * @return The annotation instance found on this method or null if no matching annotation was found.
    * @throws IllegalArgumentException if element or annotationType is null
    */
   public static <A extends Annotation> A getAnnotation(AnnotatedElement element, final Class<A> annotationType, boolean metaAnnotation, BeanManager beanManager)
   {
      if (metaAnnotation)
      {
         for (Annotation annotation : element.getAnnotations())
         {
            if (beanManager.isStereotype(annotation.annotationType()))
            {
               for (Annotation stereotypedAnnotation : beanManager.getStereotypeDefinition(annotation.annotationType()))
               {
                  if (stereotypedAnnotation.annotationType().equals(annotationType))
                  {
                     return annotationType.cast(stereotypedAnnotation);
                  }
               }
            }
         }
         return null;
      }
      else
      {
         return element.getAnnotation(annotationType);
      }
   }

}
