package org.jboss.weld.extensions.bean;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.BeanManager;

public class Beans
{

   private Beans()
   {
   }

   public static Set<Annotation> getQualifiers(BeanManager beanManager, Iterable<Annotation>... annotations)
   {
      Set<Annotation> qualifiers = new HashSet<Annotation>();
      for (Iterable<Annotation> annotationSet : annotations)
      {
         for (Annotation annotation : annotationSet)
         {
            if (beanManager.isQualifier(annotation.annotationType()))
            {
               qualifiers.add(annotation);
            }
         }
      }
      return qualifiers;
   }

   public static Set<Annotation> getQualifiers(BeanManager beanManager, Annotation[]... annotations)
   {
      Set<Annotation> qualifiers = new HashSet<Annotation>();
      for (Annotation[] annotationArray : annotations)
      {
         for (Annotation annotation : annotationArray)
         {
            if (beanManager.isQualifier(annotation.annotationType()))
            {
               qualifiers.add(annotation);
            }
         }
      }
      return qualifiers;
   }

}
