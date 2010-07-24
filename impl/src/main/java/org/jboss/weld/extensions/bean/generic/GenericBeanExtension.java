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

import static org.jboss.weld.extensions.util.Reflections.getRawType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
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
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.inject.Inject;

import org.jboss.weld.extensions.annotated.AnnotatedTypeBuilder;
import org.jboss.weld.extensions.bean.BeanBuilder;
import org.jboss.weld.extensions.bean.BeanLifecycle;
import org.jboss.weld.extensions.util.Arrays2;
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

   private final Map<Class<?>, Set<AnnotatedType<?>>> genericTypes;

   private final Map<Class<?>, Map<Member, Annotation>> producers;

   // A map of a generic annotation type to all instances of that type found on beans
   private final Map<Class<?>, Set<Annotation>> concreteGenerics;
   
   private final Map<Annotation, Bean<?>> genericConfigurationBeans;

   private final Synthetic.Provider syntheticProvider;

   GenericBeanExtension()
   {
      this.genericTypes = new HashMap<Class<?>, Set<AnnotatedType<?>>>();
      this.genericConfigurationBeans = new HashMap<Annotation, Bean<?>>();
      this.producers = new HashMap<Class<?>, Map<Member, Annotation>>();
      this.concreteGenerics = new HashMap<Class<?>, Set<Annotation>>();
      this.syntheticProvider = new Synthetic.Provider("org.jboss.weld.extensions.bean.generic");
   }

   void beforeBeanDiscovery(@Observes BeforeBeanDiscovery event)
   {
      event.addQualifier(Synthetic.class);
      event.addQualifier(GenericBean.class);
   }

   void processAnnotatedType(@Observes ProcessAnnotatedType<?> event)
   {
      AnnotatedType<?> type = event.getAnnotatedType();
      if (type.isAnnotationPresent(Generic.class))
      {
         Generic generic = type.getAnnotation(Generic.class);
         if (!genericTypes.containsKey(generic.value()))
         {
            genericTypes.put(generic.value(), new HashSet<AnnotatedType<?>>());
         }
         genericTypes.get(generic.value()).add(type);
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
            // TODO Need a producer method property really
            Property<Object> property = Properties.createProperty(member);
            setters.add(property);
         }
         ProducerInjectionTarget<T> it = new ProducerInjectionTarget<T>(event.getInjectionTarget(), beanManager, setters, producersOnClass, syntheticProvider);
         event.setInjectionTarget(it);
      }

   }

   /**
    * Install the generic beans and generic configuration beans
    */
   void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager beanManager)
   {
      for (Entry<Class<?>, Set<AnnotatedType<?>>> entry : genericTypes.entrySet())
      {
         Set<Annotation> concretes = concreteGenerics.get(entry.getKey());
         if (concretes != null)
         {
            for (AnnotatedType<?> type : entry.getValue())
            {
               for (Annotation concrete : concretes)
               {
                  event.addBean(createGenericBean(type, concrete, beanManager));
               }
            }
         }
      }
      // Add all the generic configuration beans, which were created above
      for (Bean<?> bean : genericConfigurationBeans.values())
      {
         event.addBean(bean);
      }
   }

   private <X> Bean<X> createGenericBean(AnnotatedType<X> annotatedType, Annotation concrete, BeanManager beanManager)
   {
      Synthetic genericBeanQualifier = syntheticProvider.get(concrete);

      AnnotatedTypeBuilder<X> builder = new AnnotatedTypeBuilder<X>().readFromType(annotatedType);

      // Add in the synthetic qualifier to the bean
      builder.addToClass(genericBeanQualifier);

      for (AnnotatedField<? super X> field : annotatedType.getFields())
      {
         if (field.isAnnotationPresent(Inject.class))
         {
            // if this is a configuration injection point
            if (concrete.annotationType().isAssignableFrom(getRawType(field.getBaseType())))
            {
               builder.addToField(field, getGenericConfigurationQualifier(beanManager, concrete));
            }
            // if this is a generic bean injection point
            else if (field.isAnnotationPresent(Inject.class) && field.isAnnotationPresent(GenericBean.class))
            {
               builder.removeFromField(field, GenericBean.class);
               builder.addToField(field, genericBeanQualifier);
            }
         }
      }
      for (AnnotatedMethod<? super X> method : annotatedType.getMethods())
      {
         if (method.isAnnotationPresent(Inject.class))
         {
            for (AnnotatedParameter<? super X> parameter : method.getParameters())
            {
               // if this is a configuration injection point
               if (concrete.annotationType().isAssignableFrom(getRawType(parameter.getBaseType())))
               {
                  builder.addToParameter(parameter, getGenericConfigurationQualifier(beanManager, concrete));
               }
               // if this is a generic bean injection point
               if (parameter.isAnnotationPresent(GenericBean.class))
               {
                  builder.removeFromParameter(parameter, GenericBean.class);
                  builder.addToParameter(parameter, genericBeanQualifier);
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
               // if this is a configuration injection point
               if (concrete.annotationType().isAssignableFrom(getRawType(parameter.getBaseType())))
               {
                  builder.addToParameter(parameter, getGenericConfigurationQualifier(beanManager, concrete));
               }
               // if this is a generic bean injection point
               if (parameter.isAnnotationPresent(GenericBean.class))
               {
                  builder.removeFromParameter(parameter, GenericBean.class);
                  builder.addToParameter(parameter, genericBeanQualifier);
               }
            }
         }
      }
      BeanBuilder<X> beanBuilder = new BeanBuilder<X>(beanManager).defineBeanFromAnnotatedType(builder.create());
      return beanBuilder.create();
   }

   private Synthetic getGenericConfigurationQualifier(BeanManager beanManager, final Annotation genericConfiguration)
   {
      if (!genericConfigurationBeans.containsKey(genericConfiguration))
      {
         // We don't have a bean created for this generic configuration annotation. Create it, store it to be added later
         Synthetic syntheticQualifier = syntheticProvider.get(genericConfiguration);
         // TODO make this passivation capable?
         BeanBuilder<Annotation> builder = new BeanBuilder<Annotation>(beanManager).setJavaClass(genericConfiguration.annotationType()).setTypes(Arrays2.<Type> asSet(genericConfiguration.annotationType(), Object.class)).setScope(Dependent.class).setQualifiers(Arrays2.<Annotation>asSet(syntheticQualifier)).setBeanLifecycle(new BeanLifecycle<Annotation>()
         {
   
            public void destroy(Bean<Annotation> bean, Annotation arg0, CreationalContext<Annotation> arg1)
            {
               // No-op
            }
   
            public Annotation create(Bean<Annotation> bean, CreationalContext<Annotation> arg0)
            {
               return genericConfiguration;
            }
         });
         genericConfigurationBeans.put(genericConfiguration, builder.create());
         return syntheticQualifier;
      }
      else
      {
         // The bean already exists, just return the qualifier
         return syntheticProvider.get(genericConfiguration);
      }
   }

}
