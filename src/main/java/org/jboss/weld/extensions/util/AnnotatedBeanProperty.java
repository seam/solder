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
   
   public static class DefaultAnnotationMatcher implements AnnotationMatcher
   {
      public boolean matches(Annotation annotation)
      {
         return true;
      }
   }
   
   private static class AnnotatedFieldMatcher implements FieldMatcher
   {      
      private Class<? extends Annotation> annotationClass;
      private AnnotationMatcher matcher;
      private Annotation match;
      
      public AnnotatedFieldMatcher(Class<? extends Annotation> annotationClass, 
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
      
      public Annotation getMatch()
      {
         return match;
      }
   }
   
   private static class AnnotatedMethodMatcher implements MethodMatcher
   {
      private Class<? extends Annotation> annotationClass;
      private AnnotationMatcher matcher;
      private Annotation match;
      
      public AnnotatedMethodMatcher(Class<? extends Annotation> annotationClass,
            AnnotationMatcher matcher)
      {
         this.annotationClass = annotationClass;
         this.matcher = matcher;
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
      super(cls, new AnnotatedFieldMatcher(annotationClass, annotationMatcher != null ? annotationMatcher : new DefaultAnnotationMatcher()), 
            new AnnotatedMethodMatcher(annotationClass, annotationMatcher != null ? annotationMatcher : new DefaultAnnotationMatcher()));
      
      if (((AnnotatedFieldMatcher) getFieldMatcher()).getMatch() != null)
      {
         this.annotation = (T) ((AnnotatedFieldMatcher) getFieldMatcher()).getMatch();
      }
      else if (((AnnotatedMethodMatcher) getMethodMatcher()).getMatch() != null)
      {
         this.annotation = (T) ((AnnotatedMethodMatcher) getMethodMatcher()).getMatch();
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