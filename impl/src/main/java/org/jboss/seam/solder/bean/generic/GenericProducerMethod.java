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
package org.jboss.seam.solder.bean.generic;

import static org.jboss.seam.solder.bean.Beans.createInjectionPoints;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.seam.solder.bean.ImmutableInjectionPoint;
import org.jboss.seam.solder.reflection.annotated.Annotateds;
import org.jboss.seam.solder.reflection.annotated.InjectableMethod;

public class GenericProducerMethod<T, X> extends AbstractGenericProducerBean<T>
{

   private final InjectableMethod<X> producerMethod;
   private final InjectableMethod<X> disposerMethod;

   GenericProducerMethod(Bean<T> originalBean, Annotation genericConfiguration, AnnotatedMethod<X> method, AnnotatedMethod<X> disposerMethod, final Set<Annotation> qualifiers, final Set<Annotation> genericBeanQualifiers, Class<? extends Annotation> scopeOverride, boolean alternative, Class<?> declaringBeanClass, BeanManager beanManager)
   {
      super(originalBean, genericConfiguration, qualifiers, genericBeanQualifiers, scopeOverride, Annotateds.createCallableId(method), alternative, declaringBeanClass, beanManager);
      List<InjectionPoint> injectionPoints = createInjectionPoints(method, this, beanManager);
      List<InjectionPoint> wrappedInjectionPoints = new ArrayList<InjectionPoint>();
      for (InjectionPoint injectionPoint : injectionPoints)
      {
         wrappedInjectionPoints.add(wrapInjectionPoint(injectionPoint, genericBeanQualifiers));
      }
      this.producerMethod = new InjectableMethod<X>(method, wrappedInjectionPoints, beanManager);
      if (disposerMethod != null)
      {
         injectionPoints = createInjectionPoints(disposerMethod, this, beanManager);
         wrappedInjectionPoints = new ArrayList<InjectionPoint>();
         for (InjectionPoint injectionPoint : injectionPoints)
         {
            wrappedInjectionPoints.add(wrapInjectionPoint(injectionPoint, genericBeanQualifiers));
         }
         this.disposerMethod = new InjectableMethod<X>(disposerMethod, wrappedInjectionPoints, beanManager);
      }
      else
      {
         this.disposerMethod = null;
      }
   }

   @Override
   protected T getValue(Object receiver, CreationalContext<T> creationalContext)
   {
      return producerMethod.invoke(receiver, creationalContext);
   }

   @Override
   public void destroy(T instance, CreationalContext<T> creationalContext)
   {
      if (disposerMethod != null)
      {
         disposerMethod.invoke(getReceiver(creationalContext), creationalContext);
      }
   }

   private static InjectionPoint wrapInjectionPoint(InjectionPoint injectionPoint, Set<Annotation> quals)
   {
      Annotated anotated = injectionPoint.getAnnotated();
      boolean genericInjectionPoint = false;
      if (injectionPoint.getType() instanceof Class<?>)
      {
         Class<?> c = (Class<?>) injectionPoint.getType();
         genericInjectionPoint = c.isAnnotationPresent(GenericConfiguration.class);
      }
      if (anotated.isAnnotationPresent(Disposes.class) || anotated.isAnnotationPresent(InjectGeneric.class) || genericInjectionPoint)
      {
         Set<Annotation> newQualifiers = new HashSet<Annotation>();
         newQualifiers.addAll(quals);
         newQualifiers.addAll(injectionPoint.getQualifiers());
         return new ImmutableInjectionPoint((AnnotatedParameter<?>) anotated, newQualifiers, injectionPoint.getBean(), injectionPoint.isTransient(), injectionPoint.isDelegate());
      }
      return injectionPoint;
   }

}
