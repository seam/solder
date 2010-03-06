package org.jboss.weld.extensions.util.annotated;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.AnnotatedType;

/**
 * 
 * @author Stuart Douglas
 * 
 */
abstract class AbstractNewAnnotatedMember<X, M extends Member> extends AbstractNewAnnotatedElement implements AnnotatedMember<X>
{
   private final AnnotatedType<X> declaringType;
   private final M javaMember;

   protected AbstractNewAnnotatedMember(AnnotatedType<X> declaringType, M member, Class<?> memberType, AnnotationStore annotations, Type genericType)
   {
      super(memberType, annotations, genericType);
      this.declaringType = declaringType;
      this.javaMember = member;
   }

   public AnnotatedType<X> getDeclaringType()
   {
      return declaringType;
   }

   public M getJavaMember()
   {
      return javaMember;
   }

   public boolean isStatic()
   {
      return Modifier.isStatic(javaMember.getModifiers());
   }

}
