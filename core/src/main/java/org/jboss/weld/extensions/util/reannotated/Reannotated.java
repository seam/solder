package org.jboss.weld.extensions.util.reannotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.spi.Annotated;

/**
 * This class is not thread-safe, and the client must provide any synchronization needed
 * 
 * @author Gavin King
 * @author Pete Muir
 * 
 *
 */
public abstract class Reannotated implements Annotated
{
   
   private static class AnnotationSet extends AbstractSet<Annotation>
   {

      List<Annotation> list = new ArrayList<Annotation>();

      AnnotationSet(Map<Class<? extends Annotation>, Annotation> annotations, Set<Annotation> delegateAnnotations)
      {
         list.addAll(annotations.values());
         for (Annotation ann : delegateAnnotations)
         {
            if (!annotations.containsKey(ann.annotationType()))
            {
               list.add(ann);
            }
         }
      }

      @Override
      public Iterator<Annotation> iterator()
      {
         return list.iterator();
      }

      @Override
      public int size()
      {
         return list.size();
      }

   }


   protected abstract Annotated delegate();

   private final Map<Class<? extends Annotation>, Annotation> annotations;
   
   public Reannotated()
   {
      this.annotations = new HashMap<Class<? extends Annotation>, Annotation>();
   }

   public <X extends Annotation> void redefine(Class<X> annotationType, AnnotationRedefinition<X> visitor)
   {
      if (isAnnotationPresent(annotationType))
      {
         X redefined = visitor.redefine(getAnnotation(annotationType), this);
         if (redefined == null)
         {
            annotations.remove(annotationType);
         }
         else
         {
            annotations.put(annotationType, redefined);
         }
      }
   }

   /*
    * public void undefine(Class<? extends Annotation> annotationType) {
    * annotations.put(annotationType, null); }
    */

   public void define(Annotation ann)
   {
      annotations.put(ann.annotationType(), ann);
   }

   public boolean isAnnotationWithMetatypePresent(Class<? extends Annotation> metaannotationType)
   {
      for (Annotation ann : getAnnotations())
      {
         if (ann.annotationType().isAnnotationPresent(metaannotationType))
         {
            return true;
         }
      }
      return false;
   }

   public Set<Annotation> getAnnotationsWithMetatype(Class<? extends Annotation> metaannotationType)
   {
      Set<Annotation> set = new HashSet<Annotation>();
      for (Annotation ann : getAnnotations())
      {
         if (ann.annotationType().isAnnotationPresent(metaannotationType))
         {
            set.add(ann);
         }
      }
      return set;
   }

   public abstract Class<?> getJavaClass();

   public <T extends Annotation> T getAnnotation(Class<T> annotationType)
   {
      if (annotationType == null)
      {
         throw new IllegalArgumentException("annotationType argument must not be null");
      }
      Annotation ann = annotations.get(annotationType);
      if (ann != null)
      {
         return annotationType.cast(ann);
      }
      else
      {
         return delegate().getAnnotation(annotationType);
      }
   }

   public Set<Annotation> getAnnotations()
   {
      return new AnnotationSet(annotations, delegate().getAnnotations());
   }

   public Type getBaseType()
   {
      return delegate().getBaseType();
   }

   public Set<Type> getTypeClosure()
   {
      return delegate().getTypeClosure();
   }

   public boolean isAnnotationPresent(Class<? extends Annotation> annotationType)
   {
      return annotations.containsKey(annotationType) || delegate().isAnnotationPresent(annotationType);
   }

}
