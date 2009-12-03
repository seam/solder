package org.jboss.weld.extensions.util.annotated;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;

/**
 * 
 * @author Stuart Douglas
 * 
 */
abstract class AbstractNewAnnotatedCallable<X, Y extends Member> extends AbstractNewAnnotatedMember<X, Y> implements AnnotatedCallable<X>
{

   private final List<AnnotatedParameter<X>> parameters;

   protected AbstractNewAnnotatedCallable(AnnotatedType<X> declaringType, Y member, Class<?> memberType, Class<?>[] parameterTypes, AnnotationStore annotations, Map<Integer, AnnotationStore> parameterAnnotations)
   {
      super(declaringType, member, memberType, annotations);
      this.parameters = getAnnotatedParameters(this, parameterTypes, parameterAnnotations);
   }

   public List<AnnotatedParameter<X>> getParameters()
   {
      return Collections.unmodifiableList(parameters);
   }

   public AnnotatedParameter<X> getParameter(int index)
   {
      return parameters.get(index);

   }  
   
   private static <X, Y extends Member> List<AnnotatedParameter<X>> getAnnotatedParameters(AbstractNewAnnotatedCallable<X, Y> callable, Class<?>[] parameterTypes, Map<Integer, AnnotationStore> parameterAnnotations)
   {
      List<AnnotatedParameter<X>> parameters = new ArrayList<AnnotatedParameter<X>>();
      int len = parameterTypes.length;
      for (int i = 0; i < len; ++i)
      {
         AnnotationBuilder builder = new AnnotationBuilder();
         if (parameterAnnotations != null && parameterAnnotations.containsKey(i))
         {
            builder.addAll(parameterAnnotations.get(i));
         }
         NewAnnotatedParameter<X> p = new NewAnnotatedParameter<X>(callable, parameterTypes[i], i, builder.create());
         parameters.add(p);
      }
      return parameters;
   }

}
