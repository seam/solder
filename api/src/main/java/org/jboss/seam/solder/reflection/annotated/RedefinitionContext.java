package org.jboss.seam.solder.reflection.annotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.jboss.seam.solder.reflection.Reflections;

/**
 * Provides access to the context of an annotation redefinition.
 * 
 * @author Pete Muir
 * @see AnnotatedTypeBuilder
 * @see AnnotationRedefiner
 * 
 */
public class RedefinitionContext<A extends Annotation>
{

   private final AnnotatedElement annotatedElement;
   private final Type baseType;
   private final AnnotationBuilder annotationBuilder;
   private final String elementName;

   RedefinitionContext(AnnotatedElement annotatedElement, Type baseType, AnnotationBuilder annotationBuilder, String elementName)
   {
      this.annotatedElement = annotatedElement;
      this.baseType = baseType;
      this.annotationBuilder = annotationBuilder;
      this.elementName = elementName;
   }

   /**
    * Access to the {@link AnnotatedElement} on which this annotation is
    * defined. If the annotation is defined on a Field, this may be cast to
    * {@link Field}, if defined on a method, this may be cast to {@link Method},
    * if defined on a constructor, this may be cast to {@link Constructor} or if
    * defined on a parameter, this may be cast to {@link Parameter}
    */
   public AnnotatedElement getAnnotatedElement()
   {
      return annotatedElement;
   }

   /**
    * Access to the {@link Type} of the element on which this annotation is
    * defined
    */
   public Type getBaseType()
   {
      return baseType;
   }

   /**
    * Access to the raw type of the element on which the annotation is defined
    * 
    * @return
    */
   public Class<?> getRawType()
   {
      return Reflections.getRawType(baseType);
   }

   /**
    * Access to the annotations present on the element. It is safe to modify the
    * annotations present using the {@link AnnotationBuilder}
    */
   public AnnotationBuilder getAnnotationBuilder()
   {
      return annotationBuilder;
   }

   /**
    * Access to the name of the element, or null if this represents a
    * constructor or parameter.
    */
   public String getElementName()
   {
      return elementName;
   }

}
