/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.extensions.bean;

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
 * A simple implementation of InjectionPoint
 * 
 * @author stuart
 * 
 */
public class InjectionPointImpl implements InjectionPoint
{
   

   private final Annotated annotated;
   private final Member member;
   private final Bean<?> bean;
   private final Set<Annotation> qualifiers;
   private final Type type;
   private final boolean _transient;
   private final boolean delegate;

   public InjectionPointImpl(AnnotatedField<?> field, Set<Annotation> qualifiers, Bean<?> bean, boolean trans, boolean delegate)
   {
      this.annotated = field;
      this.member = field.getJavaMember();
      this.qualifiers = new HashSet<Annotation>(qualifiers);
      this.type = field.getJavaMember().getGenericType();
      this._transient = trans;
      this.delegate = delegate;
      this.bean = bean;
   }

   public InjectionPointImpl(AnnotatedField<?> field, BeanManager beanManager, Bean<?> bean, boolean trans, boolean delegate)
   {
      this.annotated = field;
      this.member = field.getJavaMember();
      this.qualifiers = Beans.getQualifiers(field.getAnnotations(), beanManager);
      this.type = field.getJavaMember().getGenericType();
      this._transient = trans;
      this.delegate = delegate;
      this.bean = bean;
   }

   public InjectionPointImpl(AnnotatedParameter<?> param, Set<Annotation> qualifiers, Bean<?> bean, boolean trans, boolean delegate)
   {
      this.annotated = param;
      this.member = param.getDeclaringCallable().getJavaMember();
      this.qualifiers = new HashSet<Annotation>(qualifiers);
      this._transient = trans;
      this.delegate = delegate;
      this.bean = bean;
      this.type = param.getBaseType();
   }

   public InjectionPointImpl(AnnotatedParameter<?> param, BeanManager beanManager, Bean<?> bean, boolean trans, boolean delegate)
   {
      this.annotated = param;
      this.member = param.getDeclaringCallable().getJavaMember();
      this.qualifiers = Beans.getQualifiers(param.getAnnotations(), beanManager);
      this._transient = trans;
      this.delegate = delegate;
      this.bean = bean;
      this.type = param.getBaseType();
   }

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
      return _transient;
   }

}
