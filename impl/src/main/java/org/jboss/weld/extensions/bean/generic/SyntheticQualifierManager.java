package org.jboss.weld.extensions.bean.generic;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jboss.weld.extensions.util.AnnotationInstanceProvider;

class SyntheticQualifierManager
{


   private final AnnotationInstanceProvider annotationProvider;
   
   //Map of generic Annotation instance to a SyntheticQualifier
   private final Map<Annotation, SyntheticQualifier> qualifierMap;
   
   private long count;
   
   SyntheticQualifierManager()
   {
      this.qualifierMap = new HashMap<Annotation, SyntheticQualifier>();
      this.annotationProvider = new AnnotationInstanceProvider();
      this.count = 0;
   }

   SyntheticQualifier getQualifierForGeneric(Annotation annotation)
   {
      if (!qualifierMap.containsKey(annotation))
      {
         SyntheticQualifier qualifier = annotationProvider.get(SyntheticQualifier.class, Collections.singletonMap("value", count++));
         qualifierMap.put(annotation, qualifier);
      }
      return qualifierMap.get(annotation);
   }

}
