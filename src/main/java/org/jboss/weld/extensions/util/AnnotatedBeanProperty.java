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
   
   private Class<T> annotationClass;
   
   /**
    * Default constructor
    * 
    * @param cls The class to scan for the property
    * @param annotationClass The annotation class to scan for. Specified attribute
    * values may be scanned for by providing an implementation of the isMatch() method. 
    */
   public AnnotatedBeanProperty(Class<?> cls, Class<T> annotationClass)
   {            
      super(cls);
      this.annotationClass = annotationClass;
   }   
   
   protected boolean fieldMatches(Field f)
   {      
      if (f.isAnnotationPresent(annotationClass) && 
            annotationMatches(f.getAnnotation(annotationClass)))
      {      
         this.annotation = f.getAnnotation(annotationClass);
         return true;
      }
      else
      {
         return false;
      }
   }
   
   protected boolean methodMatches(Method m)
   {
      if (m.isAnnotationPresent(annotationClass) &&
            annotationMatches(m.getAnnotation(annotationClass)))
      {
         this.annotation = m.getAnnotation(annotationClass);
         return true;
      }
      else
      {
         return false;
      }
   }
   
   /**
    * This method may be overridden by a subclass. It can be used to scan 
    * for an annotation with particular attribute values, or to allow a match
    * based on more complex logic.  
    * 
    * @param annotation The potential match
    * @return true if the specified annotation is a match
    */
   protected boolean annotationMatches(T annotation)
   {
      return true;
   }
   
   /**
    * Returns the annotation type
    * 
    * @return The annotation type
    */
   public T getAnnotation()
   {
      scan();
      return annotation;
   }

    
   
 
}