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
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessProducer;
import javax.inject.Inject;

import org.jboss.weld.extensions.annotated.AnnotatedTypeBuilder;
import org.jboss.weld.extensions.bean.BeanBuilder;
import org.jboss.weld.extensions.bean.BeanLifecycle;
import org.jboss.weld.extensions.util.Arrays2;
import org.jboss.weld.extensions.util.Synthetic;

/**
 * Extension that wires in Generic Beans
 * 
 * @author Pete Muir
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 * 
 */
class GenericBeanExtension implements Extension
{

   // A map of generic configuration types to generic bean types
   private final Map<Class<?>, Set<AnnotatedType<?>>> genericBeanTypes;

   // A map of a generic configuration types to all instances of that type found
   private final Map<Class<?>, Set<Annotation>> genericConfigurationTypes;
   
   // A map of generic configuration annotations to generic configuration beans
   private final Map<Annotation, Bean<?>> genericConfigurationBeans;

   private final Synthetic.Provider syntheticProvider;

   GenericBeanExtension()
   {
      this.genericBeanTypes = new HashMap<Class<?>, Set<AnnotatedType<?>>>();
      this.genericConfigurationBeans = new HashMap<Annotation, Bean<?>>();
      this.genericConfigurationTypes = new HashMap<Class<?>, Set<Annotation>>();
      this.syntheticProvider = new Synthetic.Provider("org.jboss.weld.extensions.bean.generic");
   }

   void beforeBeanDiscovery(@Observes BeforeBeanDiscovery event)
   {
      event.addQualifier(Synthetic.class);
      event.addQualifier(GenericBean.class);
   }

   <X> void processAnnotatedType(@Observes ProcessAnnotatedType<X> event)
   {
      AnnotatedType<?> type = event.getAnnotatedType();
      if (type.isAnnotationPresent(Generic.class))
      {
         addGenericBeanType(type);
         // we will install (multiple copies of) this bean later
         event.veto();

      }
   }
   
   private <X> void addGenericBeanType(AnnotatedType<X> type)
   {
      Generic generic = type.getAnnotation(Generic.class);
      if (!genericBeanTypes.containsKey(generic.value()))
      {
         Set<AnnotatedType<?>> annotatedTypes = new HashSet<AnnotatedType<?>>();
         annotatedTypes.add(type);
         genericBeanTypes.put(generic.value(), annotatedTypes);
      }
      else
      {
         genericBeanTypes.get(generic.value()).add(type);
      }
   }
   
   <T, X> void processProducers(@Observes ProcessProducer<T, X> event, BeanManager beanManager)
   {
      // Only process the producer as a generic producer, if it has an annotation meta-annotated with GenericConfiguration
      List<Annotation> genericConfigurationAnnotiations = new ArrayList<Annotation>();
      for (Annotation annotation : event.getAnnotatedMember().getAnnotations())
      {
         if (annotation.annotationType().isAnnotationPresent(GenericConfiguration.class))
         {
            genericConfigurationAnnotiations.add(annotation);
         }
      }
      
      if (genericConfigurationAnnotiations.size() > 1)
      {
         throw new IllegalStateException("Can only have one generic configuration annotation on producer " + event.getAnnotatedMember());
      }
      else if (genericConfigurationAnnotiations.size() == 1)
      {
         Annotation genericConfiguration = genericConfigurationAnnotiations.get(0);
         replaceProducer(event, genericConfiguration, beanManager);
         addGenericConfigurationType(genericConfiguration);
      }
   }
   
   private <X> void addGenericConfigurationType(Annotation genericConfiguration)
   {
      Class<? extends Annotation> genericConfigurationType = genericConfiguration.annotationType();
      if (!genericConfigurationTypes.containsKey(genericConfigurationType))
      {
         Set<Annotation> annotations = new HashSet<Annotation>();
         annotations.add(genericConfiguration);
         genericConfigurationTypes.put(genericConfigurationType, annotations);
      }
      else
      {
         genericConfigurationTypes.get(genericConfigurationType).add(genericConfiguration);
      }
   }
   
   private <T, X> void replaceProducer(ProcessProducer<T, X> event, Annotation genericConfigurationAnnotation, BeanManager beanManager)
   {
      // First, check that this producer's type is assignable from a generic bean's type
      AnnotatedMember<T> annotatedMember = event.getAnnotatedMember();
      Class<?> memberRawType = getRawType(annotatedMember.getBaseType());
      for (Entry<Class<?>, Set<AnnotatedType<?>>> entry : genericBeanTypes.entrySet())
      {
         for (AnnotatedType<?> annotatedType : entry.getValue())
         {
            // TODO should take account of parameterized types
            if (memberRawType.isAssignableFrom(getRawType(annotatedType.getBaseType())))
            {
               // This is the generic bean that this producer is making
               event.setProducer(new GenericBeanProducer<X>(event.getProducer(), memberRawType, genericConfigurationAnnotation, syntheticProvider, beanManager));
               // Short-circuit, they're can of course be only one matching generic bean to be produced!
               return;
            }
         }
      }
      throw new IllegalStateException("Unable to find a generic bean type for " + event.getAnnotatedMember() + " amongst " + genericBeanTypes);
   }

   /**
    * Install the generic beans and generic configuration beans
    */
   void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager beanManager)
   {
      for (Entry<Class<?>, Set<AnnotatedType<?>>> entry : genericBeanTypes.entrySet())
      {
         Set<Annotation> genericConfigurations = genericConfigurationTypes.get(entry.getKey());
         if (genericConfigurations != null)
         {
            for (AnnotatedType<?> type : entry.getValue())
            {
               for (Annotation genericConfiguration : genericConfigurations)
               {
                  event.addBean(createGenericBean(type, genericConfiguration, beanManager));
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
   
   void cleanup(@Observes AfterDeploymentValidation event)
   {
      // Defensively clear maps to help with GC
      this.genericConfigurationTypes.clear();
      this.genericConfigurationBeans.clear();
      this.genericBeanTypes.clear();
      // TODO this.producers.clear();
      // TODO this.syntheticProvider.clear();
   }

}
