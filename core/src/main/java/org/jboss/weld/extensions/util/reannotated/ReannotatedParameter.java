package org.jboss.weld.extensions.util.reannotated;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import javax.enterprise.inject.spi.AnnotatedParameter;

/**
 * This implementation of {@link AnnotatedParameter} is not threadsafe and any synchronization must be performed by the client
 * 
 * @author Gavin King
 *
 * @param <X>
 */
public class ReannotatedParameter<X> extends Reannotated implements AnnotatedParameter<X>
{

   private final AnnotatedParameter<X> parameter;
   private final ReannotatedCallable<X> callable;
   private final int pos;

   public ReannotatedParameter(AnnotatedParameter<X> parameter, ReannotatedCallable<X> callable, int pos)
   {
      this.parameter = parameter;
      this.callable = callable;
      this.pos = pos;
   }

   @Override
   protected AnnotatedParameter<X> delegate()
   {
      return parameter;
   }

   public ReannotatedCallable<X> getDeclaringCallable()
   {
      return callable;
   }

   public int getPosition()
   {
      return pos;
   }

   @Override
   public Class<?> getJavaClass()
   {
      Member member = callable.getJavaMember();
      if (member instanceof Method)
      {
         return Method.class.cast(member).getParameterTypes()[pos];
      }
      else
      {
         return Constructor.class.cast(member).getParameterTypes()[pos];
      }
   }

}
