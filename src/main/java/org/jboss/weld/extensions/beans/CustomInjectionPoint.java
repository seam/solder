package org.jboss.weld.extensions.beans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * implementation of InjectionPoint that can be used by other extensions
 * 
 * @author stuart
 * 
 */
public class CustomInjectionPoint implements InjectionPoint
{

   public CustomInjectionPoint(AnnotatedField<?> field, Set<Annotation> qualifiers, Bean<?> bean, boolean trans, boolean delegate)
   {
      this.annotated = field;
      this.member = field.getJavaMember();
      this.qualifiers = new HashSet<Annotation>(qualifiers);
      this.type = field.getJavaMember().getGenericType();
      this.trans = trans;
      this.delegate = delegate;
      this.bean = bean;
   }

   public CustomInjectionPoint(AnnotatedField<?> field, BeanManager beanManager, Bean<?> bean, boolean trans, boolean delegate)
   {
      this.annotated = field;
      this.member = field.getJavaMember();
      this.qualifiers = new HashSet<Annotation>();
      this.type = field.getJavaMember().getGenericType();
      this.trans = trans;
      this.delegate = delegate;
      this.bean = bean;
      for (Annotation a : field.getAnnotations())
      {
         if (beanManager.isQualifier(a.annotationType()))
         {
            qualifiers.add(a);
         }
      }
   }

   public CustomInjectionPoint(AnnotatedParameter<?> param, Set<Annotation> qualifiers, Bean<?> bean, boolean trans, boolean delegate)
   {
      this.annotated = param;
      this.member = param.getDeclaringCallable().getJavaMember();
      this.qualifiers = new HashSet<Annotation>(qualifiers);
      this.trans = trans;
      this.delegate = delegate;
      this.bean = bean;
      this.type = param.getBaseType();
   }

   public CustomInjectionPoint(AnnotatedParameter<?> param, BeanManager beanManager, Bean<?> bean, boolean trans, boolean delegate)
   {
      this.annotated = param;
      this.member = param.getDeclaringCallable().getJavaMember();
      this.qualifiers = new HashSet<Annotation>();
      this.trans = trans;
      this.delegate = delegate;
      this.bean = bean;
      this.type = param.getBaseType();
      for (Annotation a : annotated.getAnnotations())
      {
         if (beanManager.isQualifier(a.annotationType()))
         {
            qualifiers.add(a);
         }
      }
   }

   private final Annotated annotated;

   private final Member member;

   private final Bean<?> bean;

   private final Set<Annotation> qualifiers;

   private final Type type;

   private final boolean trans;

   private final boolean delegate;

   public Annotated getAnnotated()
   {
      return annotated;
   }

   public Bean<?> getBean()
   {
      return bean;
   }

   public Member getMember()
   {
      return member;
   }

   public Set<Annotation> getQualifiers()
   {
      return qualifiers;
   }

   public Type getType()
   {
      return type;
   }

   public boolean isDelegate()
   {
      return delegate;
   }

   public boolean isTransient()
   {
      return trans;
   }

}
