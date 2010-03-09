package org.jboss.weld.extensions.util.annotated;

import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedParameter;

/**
 * 
 * @author Stuart Douglas
 * 
 */
class NewAnnotatedParameter<X> extends AbstractNewAnnotatedElement implements AnnotatedParameter<X>
{

   private final int position;
   private final AnnotatedCallable<X> declaringCallable;

   NewAnnotatedParameter(AnnotatedCallable<X> declaringCallable, Class<?> type, int position, AnnotationStore annotations)
   {
      super(type, annotations, null);
      this.declaringCallable = declaringCallable;
      this.position = position;
   }

   public AnnotatedCallable<X> getDeclaringCallable()
   {
      return declaringCallable;
   }

   public int getPosition()
   {
      return position;
   }

}
