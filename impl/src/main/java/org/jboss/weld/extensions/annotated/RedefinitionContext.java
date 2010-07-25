package org.jboss.weld.extensions.annotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import org.jboss.weld.extensions.util.Reflections;

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

   public AnnotatedElement getAnnotatedElement()
   {
      return annotatedElement;
   }
   
   public Type getBaseType()
   {
      return baseType;
   }
   
   public Class<?> getRawType()
   {
      return Reflections.getRawType(baseType);
   }
   
   public AnnotationBuilder getAnnotationBuilder()
   {
      return annotationBuilder;
   }
   
   public String getElementName()
   {
      return elementName;
   }
   
}
