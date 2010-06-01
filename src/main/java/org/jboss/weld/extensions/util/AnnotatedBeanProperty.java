package org.jboss.weld.extensions.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A convenience class for working with an annotated property (either a field or 
 * method) of a JavaBean class.  By providing an overridden annotationMatches() 
 * method in a subclass, annotations may be matched on their attribute values or 
 * other conditions.
 *  
 * @author Shane Bryzak
 */
public class AnnotatedBeanProperty<T extends Annotation> extends AbstractBeanProperty
{   
   private T annotation;   
   
   public interface AnnotationMatcher
   {
      boolean matches(Annotation annotation);
   }
   
   private static class DefaultAnnotationMatcher implements AnnotationMatcher
   {
      public boolean matches(Annotation annotation)
      {
         return true;
      }
   }
   
   private static class AnnotatedMatcher implements FieldMatcher, MethodMatcher
   {      
      private Class<? extends Annotation> annotationClass;
      private AnnotationMatcher matcher;
      private Annotation match;
      
      public AnnotatedMatcher(Class<? extends Annotation> annotationClass, 
            AnnotationMatcher matcher)
      {
         this.annotationClass = annotationClass;
         this.matcher = matcher;
      }
      
      public boolean matches(Field f)
      {
         if (f.isAnnotationPresent(annotationClass) && 
               matcher.matches(f.getAnnotation(annotationClass)))
         {
            this.match = f.getAnnotation(annotationClass);
            return true;
         }
         return false;
      }
      
      public boolean matches(Method m)
      {
         if (m.isAnnotationPresent(annotationClass) &&
               matcher.matches(m.getAnnotation(annotationClass)))
         {
            this.match = m.getAnnotation(annotationClass);
            return true;
         }
         return false;
      }      
      
      public Annotation getMatch()
      {
         return match;
      }
   }
   
   /**
    * Default constructor
    * 
    * @param cls The class to scan for the property
    * @param annotationClass The annotation class to scan for. Specified attribute
    * values may be scanned for by providing an implementation of the isMatch() method. 
    */
   @SuppressWarnings("unchecked")
   public AnnotatedBeanProperty(Class<?> cls, Class<T> annotationClass, 
         AnnotationMatcher annotationMatcher)
   {            
      super(cls, new AnnotatedMatcher(annotationClass, annotationMatcher != null ? annotationMatcher : new DefaultAnnotationMatcher()), 
            new AnnotatedMatcher(annotationClass, annotationMatcher != null ? annotationMatcher : new DefaultAnnotationMatcher()));
      
      if (((AnnotatedMatcher) getFieldMatcher()).getMatch() != null)
      {
         this.annotation = (T) ((AnnotatedMatcher) getFieldMatcher()).getMatch();
      }
      else if (((AnnotatedMatcher) getMethodMatcher()).getMatch() != null)
      {
         this.annotation = (T) ((AnnotatedMatcher) getMethodMatcher()).getMatch();
      }
   }   
   
   /**
    * Returns the annotation instance
    * 
    * @return The annotation instance
    */
   public T getAnnotation()
   {
      return annotation;
   } 
}