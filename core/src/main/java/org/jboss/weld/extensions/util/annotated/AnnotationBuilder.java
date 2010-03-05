package org.jboss.weld.extensions.util.annotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * @author Stuart Douglas
 *
 */
class AnnotationBuilder
{
   private HashMap<Class<? extends Annotation>, Annotation> annotationMap = new HashMap<Class<? extends Annotation>, Annotation>();
   private Set<Annotation> annotationSet = new HashSet<Annotation>();

   public AnnotationBuilder add(Annotation a)
   {
      annotationSet.add(a);
      annotationMap.put(a.annotationType(), a);
      return this;
   }

   public void remove(Class<? extends Annotation> a)
   {
      Iterator<Annotation> it = annotationSet.iterator();
      while (it.hasNext())
      {
         Annotation an = it.next();
         if (a.isAssignableFrom(an.annotationType()))
         {
            it.remove();
         }
      }
      annotationMap.remove(a);
   }

   public AnnotationStore create()
   {
      return new AnnotationStore(annotationMap, annotationSet);
   }
   
   public AnnotationBuilder addAll(Set<Annotation> annotations)
   {
      for (Annotation annotation : annotations)
      {
         add(annotation);
      }
      return this;
   }
   
   public AnnotationBuilder addAll(AnnotationStore annotations)
   {
      for (Annotation annotation : annotations.getAnnotations())
      {
         add(annotation);
      }
      return this;
   }

   public AnnotationBuilder addAll(AnnotatedElement element)
   {
      for (Annotation a : element.getAnnotations())
      {
         add(a);
      }
      return this;
   }

   public <T extends Annotation> T getAnnotation(Class<T> anType)
   {
      return (T) annotationMap.get(anType);
   }

}
