package org.jboss.weld.extensions.bean;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.BeanManager;

public class Beans
{
   
   private Beans() {}

   public static Set<Annotation> getQualifiers(Iterable<Annotation> annotations, BeanManager beanManager)
   {
      Set<Annotation> qualifiers = new HashSet<Annotation>();
      for (Annotation annotation : annotations)
      {
         if (beanManager.isQualifier(annotation.annotationType()))
         {
            qualifiers.add(annotation);
         }
      }
      return qualifiers;
   }
   
   public static Set<Annotation> getQualifiers(Annotation[] annotations, BeanManager beanManager)
   {
      Set<Annotation> qualifiers = new HashSet<Annotation>();
      for (Annotation annotation : annotations)
      {
         if (beanManager.isQualifier(annotation.annotationType()))
         {
            qualifiers.add(annotation);
         }
      }
      return qualifiers;
   }
   
   

}
