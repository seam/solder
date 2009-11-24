package org.jboss.weld.extensions.util;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedParameter;

/**
 * This implementation of {@link AnnotatedCallable} is not threadsafe and any synchronization should be performed by the client
 * 
 * @author Gavin King
 * @author Pete Muir
 *
 * @param <X>
 */
public abstract class ReannotatedCallable<X> extends ReannotatedMember<X> implements AnnotatedCallable<X>
{

   private final List<ReannotatedParameter<X>> parameters = new ArrayList<ReannotatedParameter<X>>();

   public ReannotatedCallable(ReannotatedType<X> declaringType, List<AnnotatedParameter<X>> params)
   {
      super(declaringType);
      for (AnnotatedParameter<X> param : params)
      {
         parameters.add(new ReannotatedParameter<X>(param, this, param.getPosition()));
      }
   }

   @Override
   protected abstract AnnotatedCallable<X> delegate();

   public List<AnnotatedParameter<X>> getParameters()
   {
      return new ArrayList<AnnotatedParameter<X>>(parameters);
   }

   public ReannotatedParameter<X> getParameter(int pos)
   {
      return parameters.get(pos);
   }

   public <Y extends Annotation> void redefineParameters(Class<Y> annotationType, AnnotationRedefinition<Y> visitor)
   {
      for (ReannotatedParameter<X> param : parameters)
      {
         param.redefine(annotationType, visitor);
      }
   }

   @Override
   public <Y extends Annotation> void redefineAll(Class<Y> annotationType, AnnotationRedefinition<Y> visitor)
   {
      redefine(annotationType, visitor);
      redefineParameters(annotationType, visitor);
   }

}
