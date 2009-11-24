package org.jboss.weld.extensions.util;

import java.lang.reflect.Constructor;

import javax.enterprise.inject.spi.AnnotatedConstructor;

/**
 * This implementation of {@link AnnotatedConstructor} is not threadsafe and any synchronization must be performed by the client
 * 
 * @author Gavin King
 *
 * @param <X>
 */
public class ReannotatedConstructor<X> extends ReannotatedCallable<X> implements AnnotatedConstructor<X>
{

   private final AnnotatedConstructor<X> constructor;

   ReannotatedConstructor(ReannotatedType<X> declaringType, AnnotatedConstructor<X> constructor)
   {
      super(declaringType, constructor.getParameters());
      this.constructor = constructor;
   }

   @Override
   protected AnnotatedConstructor<X> delegate()
   {
      return constructor;
   }

   @Override
   public Constructor<X> getJavaMember()
   {
      return constructor.getJavaMember();
   }

   @Override
   public boolean isStatic()
   {
      return constructor.isStatic();
   }

}
