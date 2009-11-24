package org.jboss.weld.extensions.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;

import javax.enterprise.inject.spi.AnnotatedMember;

/**
 * This implementation of {@link AnnotatedMember} is not threadsafe and any synchronization must be performed by the client
 * 
 * @author Gavin King
 *
 * @param <X>
 */
public abstract class ReannotatedMember<X> extends Reannotated implements AnnotatedMember<X>
{

   private final ReannotatedType<X> declaringType;

   ReannotatedMember(ReannotatedType<X> declaringType)
   {
      this.declaringType = declaringType;
   }

   public ReannotatedType<X> getDeclaringType()
   {
      return declaringType;
   }

   @Override
   protected abstract AnnotatedMember<X> delegate();

   public Member getJavaMember()
   {
      return delegate().getJavaMember();
   }

   public boolean isStatic()
   {
      return delegate().isStatic();
   }

   @Override
   public Class<?> getJavaClass()
   {
      return getJavaMember().getDeclaringClass();
   }

   public abstract <Y extends Annotation> void redefineAll(Class<Y> annotationType, AnnotationRedefinition<Y> visitor);

}
