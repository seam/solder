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
import java.lang.reflect.Field;
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
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.enterprise.inject.spi.ProcessManagedBean;
import javax.enterprise.inject.spi.ProcessProducer;
import javax.enterprise.inject.spi.ProcessProducerField;
import javax.enterprise.inject.spi.ProcessProducerMethod;
import javax.enterprise.inject.spi.Producer;
import javax.inject.Inject;

import org.jboss.weld.extensions.annotated.AnnotatedTypeBuilder;
import org.jboss.weld.extensions.annotated.AnnotationRedefiner;
import org.jboss.weld.extensions.annotated.RedefinitionContext;
import org.jboss.weld.extensions.bean.BeanBuilder;
import org.jboss.weld.extensions.bean.BeanLifecycle;
import org.jboss.weld.extensions.util.Arrays2;
import org.jboss.weld.extensions.util.Reflections;
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

   // A map of generic configuration types to generic beans
   // Used to track the generic bean found
   private final Map<Class<? extends Annotation>, Map<AnnotatedType<?>, Bean<?>>> genericBeans;

   // A map of generic configuration types to producer methods on generic beans
   // Used to track the generic bean found
   private final Map<AnnotatedType<?>, Map<AnnotatedMethod<?>, Bean<?>>> genericBeanProducerMethods;

   // A map of generic configuration types to producer fields on generic beans
   // Used to track the generic bean found
   private final Map<AnnotatedType<?>, Map<AnnotatedField<?>, Bean<?>>> genericBeanProducerFields;

   // A map of generic configuration types to generic bean injection targets
   // Used to track the generic bean found
   private final Map<Class<? extends Annotation>, Map<AnnotatedType<?>, InjectionTarget<?>>> genericInjectionTargets;

   // A map of a generic configuration types to generic configurations
   // Used to track the generic configuration producers found
   private final Map<Class<? extends Annotation>, Map<Annotation, AnnotatedMember<?>>> genericProducers;
   
   private final Map<Annotation, Producer<?>> originalProducers;

   // The Synthetic qualifier provider for generic beans, and configuration injection
   private final Synthetic.Provider syntheticProvider;
   
   // The Synthetic qualifier provider for generic product beans
   private final Synthetic.Provider productSyntheticProvider;

   GenericBeanExtension()
   {
      this.genericBeans = new HashMap<Class<? extends Annotation>, Map<AnnotatedType<?>, Bean<?>>>();
      this.genericBeanProducerMethods = new HashMap<AnnotatedType<?>, Map<AnnotatedMethod<?>, Bean<?>>>();
      this.genericBeanProducerFields = new HashMap<AnnotatedType<?>, Map<AnnotatedField<?>, Bean<?>>>();
      this.genericInjectionTargets = new HashMap<Class<? extends Annotation>, Map<AnnotatedType<?>, InjectionTarget<?>>>();
      this.genericProducers = new HashMap<Class<? extends Annotation>, Map<Annotation, AnnotatedMember<?>>>();
      this.originalProducers = new HashMap<Annotation, Producer<?>>();
      this.syntheticProvider = new Synthetic.Provider("org.jboss.weld.extensions.bean.generic");
      this.productSyntheticProvider = new Synthetic.Provider("org.jboss.weld.extensions.bean.generic.product");
   }

   <X> void replaceInjectOnGenericBeans(@Observes ProcessAnnotatedType<X> event)
   {
      AnnotatedType<X> type = event.getAnnotatedType();
      if (type.isAnnotationPresent(Generic.class))
      {
         final Class<? extends Annotation> genericConfigurationType = type.getAnnotation(Generic.class).value();
         final AnnotatedTypeBuilder<X> builder = new AnnotatedTypeBuilder<X>().readFromType(type);
         builder.redefine(Inject.class, new AnnotationRedefiner<Inject>()
         {

            public void redefine(RedefinitionContext<Inject> ctx)
            {
               if (ctx.getAnnotatedElement() instanceof Field)
               {
                  if (ctx.getRawType().equals(genericConfigurationType))
                  {
                     // This is a Generic configuration injection point
                     ctx.getAnnotationBuilder().remove(Inject.class).add(InjectGeneric.INSTANCE);
                  }
                  else if (ctx.getAnnotatedElement().isAnnotationPresent(GenericBean.class))
                  {
                     // This is a Generic bean injection point
                     ctx.getAnnotationBuilder().remove(Inject.class).add(InjectGeneric.INSTANCE);
                  }
                  else if (ctx.getAnnotatedElement().isAnnotationPresent(GenericProduct.class))
                  {
                     /*
                      * This is an injection point where @GenericProduct has
                      * been used, so we have to take control of injection
                      */
                     ctx.getAnnotationBuilder().remove(Inject.class).add(InjectGeneric.INSTANCE);
                  }
               }
            }

         });
         event.setAnnotatedType(builder.create());
      }
   }

   <X> void registerGenericBean(@Observes ProcessManagedBean<X> event)
   {
      AnnotatedType<X> type = event.getAnnotatedBeanClass();
      if (type.isAnnotationPresent(Generic.class))
      {
         Class<? extends Annotation> genericConfigurationType = type.getAnnotation(Generic.class).value();
         if (genericBeans.containsKey(genericConfigurationType))
         {
            genericBeans.get(genericConfigurationType).put(type, event.getBean());
         }
         else
         {
            Map<AnnotatedType<?>, Bean<?>> beans = new HashMap<AnnotatedType<?>, Bean<?>>();
            beans.put(type, event.getBean());
            genericBeans.put(genericConfigurationType, beans);
         }
      }
   }

   <T, X> void registerGenericBeanProducerMethod(@Observes ProcessProducerMethod<T, X> event)
   {
      AnnotatedMethod<T> method = event.getAnnotatedProducerMethod();
      AnnotatedType<T> declaringType = event.getAnnotatedProducerMethod().getDeclaringType();
      if (declaringType.isAnnotationPresent(Generic.class))
      {
         if (genericBeanProducerMethods.containsKey(declaringType))
         {
            genericBeanProducerMethods.get(declaringType).put(method, event.getBean());
         }
         else
         {
            Map<AnnotatedMethod<?>, Bean<?>> beans = new HashMap<AnnotatedMethod<?>, Bean<?>>();
            beans.put(method, event.getBean());
            genericBeanProducerMethods.put(declaringType, beans);
         }
      }
   }

   <T, X> void registerGenericBeanProducerField(@Observes ProcessProducerField<T, X> event)
   {
      AnnotatedField<T> method = event.getAnnotatedProducerField();
      AnnotatedType<T> declaringType = event.getAnnotatedProducerField().getDeclaringType();
      if (declaringType.isAnnotationPresent(Generic.class))
      {
         if (genericBeanProducerFields.containsKey(declaringType))
         {
            genericBeanProducerFields.get(declaringType).put(method, event.getBean());
         }
         else
         {
            Map<AnnotatedField<?>, Bean<?>> beans = new HashMap<AnnotatedField<?>, Bean<?>>();
            beans.put(method, event.getBean());
            genericBeanProducerFields.put(declaringType, beans);
         }
      }
   }

   <X> void registerGenericBeanInjectionTarget(@Observes ProcessInjectionTarget<X> event)
   {
      AnnotatedType<X> type = event.getAnnotatedType();
      if (type.isAnnotationPresent(Generic.class))
      {
         Class<? extends Annotation> genericConfigurationType = type.getAnnotation(Generic.class).value();
         if (genericInjectionTargets.containsKey(genericConfigurationType))
         {
            genericInjectionTargets.get(genericConfigurationType).put(type, event.getInjectionTarget());
         }
         else
         {
            Map<AnnotatedType<?>, InjectionTarget<?>> injectionTargets = new HashMap<AnnotatedType<?>, InjectionTarget<?>>();
            injectionTargets.put(type, event.getInjectionTarget());
            genericInjectionTargets.put(genericConfigurationType, injectionTargets);
         }
      }
   }

   <T, X> void registerGenericProducer(@Observes ProcessProducer<T, X> event, BeanManager beanManager)
   {
      Annotation genericConfiguration = getGenericConfiguration(event.getAnnotatedMember());
      if (genericConfiguration != null)
      {
         Producer<X> originalProducer = event.getProducer();
         
         // Replace the producer, so that our generic bean is produced
         Producer<X> genericProducer = new GenericBeanProducer<X>(originalProducer, event.getAnnotatedMember().getBaseType(), genericConfiguration, syntheticProvider, beanManager);
         event.setProducer(genericProducer);

         // Register the producer for use later
         addGenericProducer(genericConfiguration, event.getAnnotatedMember());
         addOriginalProducer(genericConfiguration, originalProducer);
      }
   }

   void createGenericBeans(@Observes AfterBeanDiscovery event, BeanManager beanManager)
   {
      // For each generic configuration type, we iterate the generic configurations
      for (Entry<Class<? extends Annotation>, Map<Annotation, AnnotatedMember<?>>> genericConfigurationType : genericProducers.entrySet())
      {
         if (!genericBeans.containsKey(genericConfigurationType.getKey()))
         {
            throw new IllegalStateException("No generic bean definition exists for " + genericConfigurationType.getKey() + ", but generic producers do: " + genericConfigurationType.getValue().values());
         }
         for (Annotation genericConfiguration : genericConfigurationType.getValue().keySet())
         {
            // Add a generic configuration bean for each generic configuration producer (allows us to inject the generic configuration annotation back into the generic bean)
            event.addBean(createGenericConfigurationBean(beanManager, genericConfiguration));
            
            // Register the GenericProduct bean
            event.addBean(createGenericProductBean(beanManager, genericConfiguration));
         }
         // For each generic bean that uses this genericConfigurationType, register a generic bean for this generic configuration 
         for (Entry<AnnotatedType<?>, Bean<?>> type : genericBeans.get(genericConfigurationType.getKey()).entrySet())
         {
            for (Entry<Annotation, AnnotatedMember<?>> genericConfiguration : genericConfigurationType.getValue().entrySet())
            {
               Bean<?> originalBean = type.getValue();
               event.addBean(createGenericBean(originalBean, genericConfiguration.getKey(), (AnnotatedType) type.getKey(), beanManager));
               
               // Only register producer method and producer field beans if the generic producer field has the same type as the generic bean declaration
               if (genericConfiguration.getValue().getBaseType().equals(type.getKey().getBaseType()))
               {
                  if (genericBeanProducerMethods.containsKey(type.getKey()))
                  {
                     for (Entry<AnnotatedMethod<?>, Bean<?>> method : genericBeanProducerMethods.get(type.getKey()).entrySet())
                     {
                        event.addBean(createGenericProducerMethod(method.getValue(), genericConfiguration.getKey(), method.getKey(), beanManager));
                     }
                  }
                  if (genericBeanProducerFields.containsKey(type.getKey()))
                  {
                     for (Entry<AnnotatedField<?>, Bean<?>> field : genericBeanProducerFields.get(type.getKey()).entrySet())
                     {
                        event.addBean(createGenericProducerField(field.getValue(), genericConfiguration.getKey(), field.getKey(), beanManager));
                     }
                  }
               }
            }
         }
      }
   }

   private static Annotation getGenericConfiguration(Annotated annotated)
   {
      // Only process the producer as a generic producer, if it has an annotation meta-annotated with GenericConfiguration
      List<Annotation> genericConfigurationAnnotiations = new ArrayList<Annotation>();
      for (Annotation annotation : annotated.getAnnotations())
      {
         if (annotation.annotationType().isAnnotationPresent(GenericConfiguration.class))
         {
            genericConfigurationAnnotiations.add(annotation);
         }
      }

      if (genericConfigurationAnnotiations.size() > 1)
      {
         throw new IllegalStateException("Can only have one generic configuration annotation on " + annotated);
      }
      else if (genericConfigurationAnnotiations.size() == 1)
      {
         return genericConfigurationAnnotiations.get(0);
      }
      else
      {
         return null;
      }
   }

   private <X> void addGenericProducer(Annotation genericConfiguration, AnnotatedMember<X> annotatedMember)
   {
      Class<? extends Annotation> genericConfigurationType = genericConfiguration.annotationType();
      if (!genericProducers.containsKey(genericConfigurationType))
      {
         Map<Annotation, AnnotatedMember<?>> genericConfigurations = new HashMap<Annotation, AnnotatedMember<?>>();
         genericConfigurations.put(genericConfiguration, annotatedMember);
         genericProducers.put(genericConfigurationType, genericConfigurations);
      }
      else
      {
         genericProducers.get(genericConfigurationType).put(genericConfiguration, annotatedMember);
      }
   }
   
   private <T> void addOriginalProducer(Annotation genericConfiguration, Producer<T> producer)
   {
      originalProducers.put(genericConfiguration, producer);
   }

   private Bean<?> createGenericConfigurationBean(BeanManager beanManager, final Annotation genericConfiguration)
   {
      // We don't have a bean created for this generic configuration annotation. Create it, store it to be added later
      Synthetic syntheticQualifier = syntheticProvider.get(genericConfiguration);
      // TODO make this passivation capable?
      BeanBuilder<Annotation> builder = new BeanBuilder<Annotation>(beanManager).setJavaClass(genericConfiguration.annotationType()).setTypes(Arrays2.<Type> asSet(genericConfiguration.annotationType(), Object.class)).setScope(Dependent.class).setQualifiers(Arrays2.<Annotation> asSet(syntheticQualifier)).setBeanLifecycle(new BeanLifecycle<Annotation>()
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
      return builder.create();
   }
   
   private <T> Bean<T> createGenericProductBean(BeanManager beanManager, final Annotation genericConfiguration)
   {
      // We don't have a bean created for this generic configuration annotation. Create it, store it to be added later
      Synthetic syntheticQualifier = productSyntheticProvider.get(genericConfiguration);
      final Producer<T> producer = (Producer<T>) originalProducers.get(genericConfiguration);
      AnnotatedMember<?> originalProducer = genericProducers.get(genericConfiguration.annotationType()).get(genericConfiguration);
      
      // TODO make this passivation capable?
      BeanBuilder<T> builder = new BeanBuilder<T>(beanManager)
         .setJavaClass(Reflections.<T>getRawType(originalProducer.getBaseType()))
         .setQualifiers(Arrays2.<Annotation> asSet(syntheticQualifier))
         .setBeanLifecycle(new BeanLifecycle<T>()
            {
      
               public void destroy(Bean<T> bean, T instance, CreationalContext<T> ctx)
               {
                  producer.dispose(instance);
               }
      
               public T create(Bean<T> bean, CreationalContext<T> ctx)
               {
                  return producer.produce(ctx);
               }
            });
      return builder.create();
   }

   private <X> Bean<X> createGenericBean(Bean<X> originalBean, Annotation genericConfiguration, AnnotatedType<X> type, BeanManager beanManager)
   {
      return new GenericManagedBean<X>(originalBean, genericConfiguration, (InjectionTarget<X>) genericInjectionTargets.get(genericConfiguration.annotationType()).get(type), type, syntheticProvider, productSyntheticProvider, beanManager);
   }

   private <X, T> Bean<T> createGenericProducerMethod(Bean<T> originalBean, Annotation genericConfiguration, AnnotatedMethod<X> method, BeanManager beanManager)
   {
      Set<Annotation> qualifiers = getQualifiers(genericProducers.get(genericConfiguration.annotationType()).get(genericConfiguration).getAnnotations(), originalBean, beanManager);
      return new GenericProducerMethod<T, X>(originalBean, genericConfiguration, method, qualifiers, syntheticProvider, beanManager);
   }

   private <X, T> Bean<T> createGenericProducerField(Bean<T> originalBean, Annotation genericConfiguration, AnnotatedField<X> field, BeanManager beanManager)
   {
      Set<Annotation> qualifiers = getQualifiers(genericProducers.get(genericConfiguration.annotationType()).get(genericConfiguration).getAnnotations(), originalBean, beanManager);
      return new GenericProducerField<T, X>(originalBean, genericConfiguration, field, qualifiers, syntheticProvider, beanManager);
   }

   private static Set<Annotation> getQualifiers(Set<Annotation> annotations, Bean<?> originalBean, BeanManager beanManager)
   {
      Set<Annotation> qualifiers = new HashSet<Annotation>();
      for (Annotation annotation : annotations)
      {
         if (beanManager.isQualifier(annotation.annotationType()))
         {
            qualifiers.add(annotation);
         }
      }
      for (Annotation annotation : originalBean.getQualifiers())
      {
         if (!annotation.annotationType().equals(Default.class))
         {
            qualifiers.add(annotation);
         }
      }
      return qualifiers;
   }

   void cleanup(@Observes AfterDeploymentValidation event)
   {
      // Defensively clear maps to help with GC
      this.genericProducers.clear();
      this.genericBeans.clear();
      this.syntheticProvider.clear();
      this.genericBeanProducerFields.clear();
      this.genericBeanProducerMethods.clear();
      this.genericInjectionTargets.clear();
   }

}
