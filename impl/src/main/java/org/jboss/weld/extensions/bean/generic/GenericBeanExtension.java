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

import static org.jboss.weld.extensions.util.Reflections.EMPTY_ANNOTATION_ARRAY;
import static org.jboss.weld.extensions.util.Reflections.getQualifiers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.ArrayList;
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

import org.jboss.weld.extensions.annotated.AnnotatedTypeBuilder;
import org.jboss.weld.extensions.bean.BeanBuilder;
import org.jboss.weld.extensions.util.Synthetic;
import org.jboss.weld.extensions.util.properties.Properties;
import org.jboss.weld.extensions.util.properties.Property;

/**
 * Extension that wires in Generic Beans
 * 
 * @author Pete Muir
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 * 
 */
class GenericBeanExtension implements Extension
{

   private final Map<Class<?>, Set<AnnotatedType<?>>> genericBeans;

   private final Map<Class<?>, Map<Member, Annotation>> producers;

   // A map of a generic annotation type to all instances of that type found on beans
   private final Map<Class<?>, Set<Annotation>> concreteGenerics;
   
   private final Synthetic.Provider syntheticProvider;

   GenericBeanExtension()
   {
      this.genericBeans = new HashMap<Class<?>, Set<AnnotatedType<?>>>();
      this.producers = new HashMap<Class<?>, Map<Member, Annotation>>();
      this.concreteGenerics = new HashMap<Class<?>, Set<Annotation>>();
      this.syntheticProvider = new Synthetic.Provider("org.jboss.weld.extensions.bean.generic");
   }

   void beforeBeanDiscovery(@Observes BeforeBeanDiscovery event)
   {
      event.addQualifier(Synthetic.class);
   }

   void processAnnotatedType(@Observes ProcessAnnotatedType<?> event)
   {
      AnnotatedType<?> type = event.getAnnotatedType();
      if (type.isAnnotationPresent(Generic.class))
      {
         Generic generic = type.getAnnotation(Generic.class);
         if (!genericBeans.containsKey(generic.value()))
         {
            genericBeans.put(generic.value(), new HashSet<AnnotatedType<?>>());
         }
         genericBeans.get(generic.value()).add(type);
         // we will install (multiple copies of) this bean later
         event.veto();

      }
      // make note of any producer fields that produce generic beans
      for (AnnotatedField<?> field : type.getFields())
      {
         if (field.isAnnotationPresent(Produces.class))
         {
            for (Annotation annotation : field.getAnnotations())
            {
               if (annotation.annotationType().isAnnotationPresent(GenericConfiguration.class))
               {
                  if (!producers.containsKey(type.getJavaClass()))
                  {
                     producers.put(type.getJavaClass(), new HashMap<Member, Annotation>());
                  }
                  if (!concreteGenerics.containsKey(annotation.annotationType()))
                  {
                     concreteGenerics.put(annotation.annotationType(), new HashSet<Annotation>());
                  }
                  producers.get(type.getJavaClass()).put(field.getJavaMember(), annotation);
                  concreteGenerics.get(annotation.annotationType()).add(annotation);
               }
            }
         }
      }
      
      // make note of any producer method that produce generic beans
      for (AnnotatedMethod<?> method : type.getMethods())
      {
         if (method.isAnnotationPresent(Produces.class))
         {
            for (Annotation annotation : method.getAnnotations())
            {
               if (annotation.annotationType().isAnnotationPresent(GenericConfiguration.class))
               {
                  if (!producers.containsKey(type.getJavaClass()))
                  {
                     producers.put(type.getJavaClass(), new HashMap<Member, Annotation>());
                  }
                  if (!concreteGenerics.containsKey(annotation.annotationType()))
                  {
                     concreteGenerics.put(annotation.annotationType(), new HashSet<Annotation>());
                  }
                  producers.get(type.getJavaClass()).put(method.getJavaMember(), annotation);
                  concreteGenerics.get(annotation.annotationType()).add(annotation);
               }
            }
         }
      }
   }

   /**
    * wraps InjectionTarget to initialise producer fields that produce generic
    * beans
    */
   <T> void processInjectionTarget(@Observes ProcessInjectionTarget<T> event, BeanManager beanManager)
   {
      Class<?> clazz = event.getAnnotatedType().getJavaClass();
      if (producers.containsKey(clazz))
      {
         Map<Member, Annotation> producersOnClass = producers.get(clazz);
         List<Property<Object>> setters = new ArrayList<Property<Object>>();
         for (Member member : producersOnClass.keySet())
         {
            Property<Object> property = Properties.createProperty(member);
            setters.add(property);
         }
         ProducerInjectionTarget<T> it = new ProducerInjectionTarget<T>(event.getInjectionTarget(), beanManager, setters, producersOnClass, syntheticProvider);
         event.setInjectionTarget(it);
      }
      
      
   }

   /**
    * Installs the generic beans.
    */
   void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager beanManager)
   {
      for (Entry<Class<?>, Set<AnnotatedType<?>>> entry : genericBeans.entrySet())
      {
         Set<Annotation> concretes = concreteGenerics.get(entry.getKey());
         if (concretes != null)
         {
            for (AnnotatedType<?> type : entry.getValue())
            {
               for (Annotation concrete : concretes)
               {
                  event.addBean(redefineType(type, concrete, beanManager));
               }
            }
         }
      }
   }

   private <X> Bean<X> redefineType(AnnotatedType<X> annotatedType, Annotation concrete, BeanManager beanManager)
   {
      Synthetic syntheticQualifier = syntheticProvider.get(concrete);

      AnnotatedTypeBuilder<X> builder = AnnotatedTypeBuilder.newInstance(annotatedType).readAnnotationsFromUnderlyingType();
      builder.addToClass(syntheticQualifier);
      for (AnnotatedField<? super X> field : annotatedType.getFields())
      {
         if (field.isAnnotationPresent(Inject.class))
         {
            // if this is a configuration injection point
            if (concrete.annotationType().isAssignableFrom(field.getJavaMember().getType()))
            {
               builder.removeFromField(field.getJavaMember(), Inject.class);
               builder.addToField(field.getJavaMember(), InjectConfiguration.INSTANCE);
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
               // TODO IMprove this
               Annotation[] qualifiers = getQualifiers(field.getAnnotations(), beanManager).toArray(EMPTY_ANNOTATION_ARRAY);
               Set<Bean<?>> beans = beanManager.getBeans(field.getJavaMember().getType(), qualifiers);
               if (beans.isEmpty())
               {
                  builder.addToField(field.getJavaMember(), syntheticQualifier);
               }
            }
         }
         else if (field.isAnnotationPresent(Produces.class))
         {
            // TODO: register a producer with the appropriate qualifier
         }
      }
      for (AnnotatedMethod<?> method : annotatedType.getMethods())
      {
         // TODO: need to properly handle Observer methods and Disposal
         // methods
         if (method.isAnnotationPresent(Produces.class))
         {
            // TODO: we need to register the producer bean, so this is not
            // very useful at the moment
            for (AnnotatedParameter<?> pm : method.getParameters())
            {
               Class<?> paramType = method.getJavaMember().getParameterTypes()[pm.getPosition()];

               // check to see if we should be injecting a generic bean
               // we do this by checking if there are any beans that can be
               // injected into this point
               // if there is not then we assume it is a generic injection
               // point
               // this has the downside that if it is actually a deployment
               // error then it will confuse the user
               Annotation[] qualifiers = getQualifiers(pm.getAnnotations(), beanManager).toArray(EMPTY_ANNOTATION_ARRAY);
               Set<Bean<?>> beans = beanManager.getBeans(paramType, qualifiers);
               if (beans.isEmpty())
               {
                  builder.addToMethod(method.getJavaMember(), syntheticQualifier);
               }
            }
         }
      }

      for (AnnotatedConstructor<X> constructor : annotatedType.getConstructors())
      {
         if (constructor.isAnnotationPresent(Inject.class))
         {
            for (AnnotatedParameter<X> parameter : constructor.getParameters())
            {
               Class<?> paramType = constructor.getJavaMember().getParameterTypes()[parameter.getPosition()];
               Annotation[] qualifiers = getQualifiers(parameter.getAnnotations(), beanManager).toArray(EMPTY_ANNOTATION_ARRAY);
               Set<Bean<?>> beans = beanManager.getBeans(paramType, qualifiers);
               if (beans.isEmpty())
               {
                  builder.addToConstructorParameter(constructor.getJavaMember(), parameter.getPosition(), syntheticQualifier);
               }
            }
         }
      }
      AnnotatedType<X> newAnnotatedType = builder.create();
      InjectionTarget<X> it = beanManager.createInjectionTarget(newAnnotatedType);

      it = new GenericBeanInjectionTargetWrapper<X>(newAnnotatedType, it, concrete);
      BeanBuilder<X> beanBuilder = new BeanBuilder<X>(newAnnotatedType, beanManager).defineBeanFromAnnotatedType().setInjectionTarget(it);
      return beanBuilder.create();
   }

}
