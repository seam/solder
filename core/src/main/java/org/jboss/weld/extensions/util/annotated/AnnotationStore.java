package org.jboss.weld.extensions.util.annotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Stuart Douglas
 *
 */
class AnnotationStore
{
   HashMap<Class<? extends Annotation>, Annotation> annotationMap = new HashMap<Class<? extends Annotation>, Annotation>();
   Set<Annotation> annotations = new HashSet<Annotation>();

   public void clear()
   {
      annotationMap.clear();
      annotations.clear();
   }

   public void addAnnotation(Annotation a)
   {
      annotations.add(a);
      annotationMap.put(a.getClass(), a);
   }

   public void removeAnnotation(Class a)
   {
      Annotation an = annotationMap.get(a);
      if (an != null)
      {
         annotations.remove(an);
         annotationMap.remove(a);
      }
   }

   public <T extends Annotation> T getAnnotation(Class<T> type)
   {
      return (T) annotationMap.get(type);
   }

   public Set<Annotation> getAnnotations()
   {
      return Collections.unmodifiableSet(annotations);
   }

   public boolean isAnnotationPresent(Class<? extends Annotation> type)
   {
      return annotationMap.containsKey(type);
   }

   public void addAll(AnnotatedElement element)
   {
      for (Annotation a : element.getAnnotations())
      {
         addAnnotation(a);
      }
   }

}
