package org.jboss.weld.extensions.util.properties;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * A convenience class for working with an annotated property (either a field or
 * method) of a JavaBean class.
 * 
 * @author Shane Bryzak
 * @author Pete Muir
 */
public class AnnotatedBeanProperty<V> extends AbstractBeanProperty<V>
{

   /**
    * An {@link AnnotatedElement} based matcher
    * 
    * @author pmuir
    * 
    */
   public interface AnnotationMatcher
   {
      boolean matches(AnnotatedElement element);
   }

   private static class AnnotationMatcherAdapter implements FieldMatcher, MethodMatcher
   {
      private final AnnotationMatcher matcher;

      public AnnotationMatcherAdapter(AnnotationMatcher matcher)
      {
         if (matcher == null)
         {
            throw new IllegalArgumentException("matcher must not be null");
         }
         this.matcher = matcher;
      }

      public boolean matches(Field f)
      {
         return matcher.matches(f);
      }

      public boolean matches(Method m)
      {
         return matcher.matches(m);
      }
   }
   
   public AnnotatedBeanProperty(Class<?> targetClass, AnnotationMatcher annotationMatcher)
   {
      this(targetClass, new AnnotationMatcherAdapter(annotationMatcher));
   }
   
   private AnnotatedBeanProperty(Class<?> targetClass, AnnotationMatcherAdapter matcher)
   {
      super(targetClass, matcher, matcher);
   }

}