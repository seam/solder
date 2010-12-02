/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.solder.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.BeanManager;

/**
 * Inspect an {@link AnnotatedElement} or {@link Annotated} to obtain it's meta
 * annotations and annotations, taking into account stereotypes.
 * 
 * @author Pete Muir
 * 
 */
public class AnnotationInspector
{

   private AnnotationInspector()
   {
   }

   /**
    * Discover if a AnnotatedElement <b>element</b> has been annotated with
    * <b>annotationType</b>. This also discovers annotations defined through a @
    * {@link Stereotype} and the CDI SPI.
    * 
    * @param element The element to inspect.
    * @param annotationType
    * @param metaAnnotation Whether the annotation may be used as a
    *           meta-annotation or not
    * 
    * @return true if annotation is present either on the method itself. Returns
    *         false if the annotation is not present
    * @throws IllegalArgumentException if element or annotationType is null
    */
   public static boolean isAnnotationPresent(AnnotatedElement element, Class<? extends Annotation> annotationType, boolean metaAnnotation, BeanManager beanManager)
   {
      return getAnnotation(element, annotationType, metaAnnotation, beanManager) != null;
   }

   /**
    * Inspect AnnoatedElement <b>element</b> for a specific <b>type</b> of
    * annotation. This also discovers annotations defined through a @
    * {@link Stereotype} and the CDI SPI.
    * 
    * @param element The element to inspect
    * @param annotationType The annotation type to check for
    * @param metaAnnotation Whether the annotation may be used as a
    *           meta-annotation or not
    * 
    * @return The annotation instance found on this method or null if no
    *         matching annotation was found.
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

   /**
    * Inspects an annotated element for the given meta annotation. This should
    * only be used for user defined meta annotations, where the annotation must
    * be physically present.
    * 
    * @param element The element to inspect
    * @param annotationType The meta annotation to search for
    * @return The annotation instance found on this method or null if no
    *         matching annotation was found.
    */
   public static <A extends Annotation> A getMetaAnnotation(Annotated element, final Class<A> annotationType)
   {
      for (Annotation annotation : element.getAnnotations())
      {
         if (annotation.annotationType().isAnnotationPresent(annotationType))
         {
            return annotation.annotationType().getAnnotation(annotationType);
         }
      }
      return null;
   }

   /**
    * Inspects an annotated element for any annotations with the given meta
    * annotation. This should only be used for user defined meta annotations,
    * where the annotation must be physically present.
    * 
    * @param element The element to inspect
    * @param annotationType The meta annotation to search for
    * @return The annotation instances found on this method or an empty set if
    *         no matching meta-annotation was found.
    */
   public static Set<Annotation> getAnnotations(Annotated element, final Class<? extends Annotation> metaAnnotationType)
   {
      Set<Annotation> annotations = new HashSet<Annotation>();
      for (Annotation annotation : element.getAnnotations())
      {
         if (annotation.annotationType().isAnnotationPresent(metaAnnotationType))
         {
            annotations.add(annotation);
         }
      }
      return annotations;
   }

}
