package org.jboss.weld.extensions.util.properties;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class TypeAnnotatedBeanProperty<T extends Annotation, V> extends AnnotatedBeanProperty<V>
{

   
   /**
    * An AnnotationMatcher which simply requires the the annotation to be of the
    * specified type
    * 
    */
   private static class TypeAnnotationMatcher<T extends Annotation> implements AnnotationMatcher
   {

      private final Class<T> annotationType;
      
      private final Matched<T> matched; 

      public TypeAnnotationMatcher(Class<T> annotationType, Matched<T> matched)
      {
         if (annotationType == null)
         {
            throw new IllegalArgumentException("annotationType must not be null");
         }
         this.annotationType = annotationType;
         this.matched = matched;
      }

      public boolean matches(AnnotatedElement element)
      {
         if (element.isAnnotationPresent(annotationType))
         {
            this.matched.setMatched(element.getAnnotation(annotationType));
            return true;
         }
         else
         {
            return false;
         }
      }
   }
   
   private final T annotation;

   /**
    * Default constructor
    * 
    * @param cls The class to scan for the property
    * @param annotationClass The annotation class to scan for. Specified
    *           attribute values may be scanned for by providing an
    *           implementation of the isMatch() method.
    */
   public TypeAnnotatedBeanProperty(Class<?> cls, Class<T> annotationType)
   {
      this(cls, annotationType, new Matched<T>());
   }
   
   private TypeAnnotatedBeanProperty(Class<?> targetClass, Class<T> annotationType, Matched<T> matched)
   {
      super(targetClass, new TypeAnnotationMatcher<T>(annotationType, matched));
      this.annotation = matched.getMatched();
   }
   
   public T getAnnotation()
   {
      return annotation;
   }

}
