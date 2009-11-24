package org.jboss.weld.extensions.util;

import java.lang.reflect.Method;

import javax.enterprise.inject.spi.AnnotatedMethod;

/**
 * This implementation of {@link AnnotatedMethod} is not threadsafe and any synchronization must be performed by the client
 * 
 * @author Gavin King
 *
 * @param <X>
 */
public class ReannotatedMethod<X> extends ReannotatedCallable<X> implements AnnotatedMethod<X>
{

   private final AnnotatedMethod<X> method;

   ReannotatedMethod(ReannotatedType<X> declaringType, AnnotatedMethod<X> method)
   {
      super(declaringType, method.getParameters());
      this.method = method;
   }

   @Override
   protected AnnotatedMethod<X> delegate()
   {
      return method;
   }

   @Override
   public Method getJavaMember()
   {
      return method.getJavaMember();
   }

   @Override
   public boolean isStatic()
   {
      return method.isStatic();
   }

}
