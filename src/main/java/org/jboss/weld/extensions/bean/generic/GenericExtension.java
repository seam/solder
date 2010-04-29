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
package org.jboss.weld.extensions.bean.generic;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.inject.Inject;

import org.jboss.weld.extensions.bean.CustomBeanBuilder;
import org.jboss.weld.extensions.util.AnnotationInstanceProvider;
import org.jboss.weld.extensions.util.annotated.NewAnnotatedTypeBuilder;

public class GenericExtension implements Extension
{

   private AnnotationInstanceProvider annotationProvider = new AnnotationInstanceProvider();

   private Map<Class<?>, Set<AnnotatedType<?>>> genericBeans = new HashMap<Class<?>, Set<AnnotatedType<?>>>();

   private Map<Class<?>, Map<AnnotatedField<?>, Annotation>> producerFields = new HashMap<Class<?>, Map<AnnotatedField<?>, Annotation>>();

   /**
    * map of a generic annotation type to all instances of that type found on
    * beans
    */
   private Map<Class<?>, Set<Annotation>> concreteGenerics = new HashMap<Class<?>, Set<Annotation>>();

   /**
    * Map of generic Annotation instance to a SyntheticQualifier
    */
   private Map<Annotation, SyntheticQualifier> qualifierMap = new HashMap<Annotation, SyntheticQualifier>();

   long count = 0;

   public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery event)
   {
      event.addQualifier(SyntheticQualifier.class);
   }

   public void processAnnotatedType(@Observes ProcessAnnotatedType<?> event)
   {
      AnnotatedType<?> type = event.getAnnotatedType();
      if (type.isAnnotationPresent(Generic.class))
      {
         Generic an = type.getAnnotation(Generic.class);
         if (!genericBeans.containsKey(an.value()))
         {
            genericBeans.put(an.value(), new HashSet<AnnotatedType<?>>());
         }
         genericBeans.get(an.value()).add(type);
         // we will install (multiple copies of) this bean later
         event.veto();

      }
      // make note of any producer fields that produce generic beans
      for (Object f : type.getFields())
      {
         AnnotatedField<?> field = (AnnotatedField<?>) f;
         if (field.isAnnotationPresent(Produces.class))
         {
            for (Annotation a : field.getAnnotations())
            {
               if (a.annotationType().isAnnotationPresent(GenericAnnotation.class))
               {
                  if (!producerFields.containsKey(type.getJavaClass()))
                  {
                     producerFields.put(type.getJavaClass(), new HashMap<AnnotatedField<?>, Annotation>());
                  }
                  if (!concreteGenerics.containsKey(a.annotationType()))
                  {
                     concreteGenerics.put(a.annotationType(), new HashSet<Annotation>());
                  }
                  producerFields.get(type.getJavaClass()).put(field, a);
                  concreteGenerics.get(a.annotationType()).add(a);
               }
            }
         }
      }
   }

   /**
    * wraps InjectionTarget to initialise producer fields that produce generic
    * beans
    */
   public <T> void processInjectionTarget(@Observes ProcessInjectionTarget<T> event, BeanManager beanManager)
   {
      Class<?> javaClass = event.getAnnotatedType().getJavaClass();
      if (producerFields.containsKey(javaClass))
      {
         Map<AnnotatedField<?>, Annotation> producers = producerFields.get(javaClass);
         List<FieldSetter> setters = new ArrayList<FieldSetter>();
         for (AnnotatedField<?> a : producers.keySet())
         {
            SyntheticQualifier qual = this.getQualifierForGeneric(producers.get(a));
            FieldSetter f = new FieldSetter(beanManager, a.getJavaMember(), qual);
            setters.add(f);
         }
         ProducerFieldInjectionTarget<T> it = new ProducerFieldInjectionTarget<T>(event.getInjectionTarget(), setters);
         event.setInjectionTarget(it);
      }
   }

   /**
    * Installs the generic beans.
    */
   public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager beanManager)
   {
      for (Entry<Class<?>, Set<AnnotatedType<?>>> entry : genericBeans.entrySet())
      {
         Set<Annotation> concretes = concreteGenerics.get(entry.getKey());
         if (concretes != null)
         {
            for (AnnotatedType<?> type : entry.getValue())
            {
               for (Annotation conc : concretes)
               {
                  abd.addBean(redefineType(type, conc, beanManager));
               }
            }
         }
      }
   }

   private <X> Bean<X> redefineType(AnnotatedType<X> at, Annotation conc, BeanManager beanManager)
   {
      SyntheticQualifier newQualifier = getQualifierForGeneric(conc);

      NewAnnotatedTypeBuilder<X> builder = NewAnnotatedTypeBuilder.newInstance(at).readAnnotationsFromUnderlying();
      builder.addToClass(newQualifier);
      for (AnnotatedField<? super X> f : at.getFields())
      {
         if (f.isAnnotationPresent(Inject.class))
         {
            // if this is a configuration injection point
            if (conc.annotationType().isAssignableFrom(f.getJavaMember().getType()))
            {
               builder.removeFromField(f.getJavaMember(), Inject.class);
               builder.addToField(f.getJavaMember(), InjectConfiguration.INSTANCE);
            }
            else
            {
               // check to see if we should be injecting a generic bean
               // we do this by checking if there are any beans that can be
               // injected into this point
               // if there is not then we assume it is a generic injection
               // point
               // this has the downside that if it is actually a deployment
               // error then it will confuse the user
               Annotation[] qls = getQualifiers(f.getAnnotations(), beanManager);
               Set<Bean<?>> beans = beanManager.getBeans(f.getJavaMember().getType(), qls);
               if (beans.isEmpty())
               {
                  builder.addToField(f.getJavaMember(), newQualifier);
               }
            }
         }
         else if (f.isAnnotationPresent(Produces.class))
         {
            // TODO: register a producer with the appropriate qualifier
         }
      }
      for (AnnotatedMethod<?> m : at.getMethods())
      {
         // TODO: need to properly handle Observer methods and Disposal
         // methods
         if (m.isAnnotationPresent(Produces.class))
         {
            // TODO: we need to register the producer bean, so this is not
            // very useful at the moment
            for (AnnotatedParameter<?> pm : m.getParameters())
            {
               Class<?> paramType = m.getJavaMember().getParameterTypes()[pm.getPosition()];

               // check to see if we should be injecting a generic bean
               // we do this by checking if there are any beans that can be
               // injected into this point
               // if there is not then we assume it is a generic injection
               // point
               // this has the downside that if it is actually a deployment
               // error then it will confuse the user
               Annotation[] qls = getQualifiers(pm.getAnnotations(), beanManager);
               Set<Bean<?>> beans = beanManager.getBeans(paramType, qls);
               if (beans.isEmpty())
               {
                  builder.addToMethod(m.getJavaMember(), newQualifier);
               }
            }
         }
      }

      for (AnnotatedConstructor<X> m : at.getConstructors())
      {
         if (m.isAnnotationPresent(Inject.class))
         {
            for (AnnotatedParameter<X> pm : m.getParameters())
            {
               Class<?> paramType = m.getJavaMember().getParameterTypes()[pm.getPosition()];
               Annotation[] qls = getQualifiers(pm.getAnnotations(), beanManager);
               Set<Bean<?>> beans = beanManager.getBeans(paramType, qls);
               if (beans.isEmpty())
               {
                  builder.addToConstructorParameter(m.getJavaMember(), pm.getPosition(), newQualifier);
               }
            }
         }
      }
      AnnotatedType<X> newAnnotatedType = builder.create();
      InjectionTarget<X> it = beanManager.createInjectionTarget(newAnnotatedType);

      it = new GenericBeanInjectionTargetWrapper<X>(newAnnotatedType, it, conc);
      CustomBeanBuilder<X> beanBuilder = new CustomBeanBuilder<X>(newAnnotatedType, beanManager, it);
      return beanBuilder.build();
   }

   public SyntheticQualifier getQualifierForGeneric(Annotation a)
   {
      if (!qualifierMap.containsKey(a))
      {
         SyntheticQualifier qualifier = annotationProvider.get(SyntheticQualifier.class, (Map) Collections.singletonMap("value", count++));
         qualifierMap.put(a, qualifier);
      }
      return qualifierMap.get(a);
   }

   static Annotation[] getQualifiers(Set<Annotation> annotations, BeanManager manager)
   {
      List<Annotation> qualifiers = new ArrayList<Annotation>();
      for (Annotation a : annotations)
      {
         if (manager.isQualifier(a.annotationType()))
         {
            qualifiers.add(a);
         }
      }
      Annotation[] qls = new Annotation[qualifiers.size()];
      for (int j = 0; j < qls.length; ++j)
      {
         qls[j] = qualifiers.get(j);
      }
      return qls;
   }

}
