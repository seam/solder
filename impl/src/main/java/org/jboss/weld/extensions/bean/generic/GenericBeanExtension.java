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

import static org.jboss.weld.extensions.reflection.AnnotationInspector.getAnnotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.enterprise.inject.spi.ProcessManagedBean;
import javax.enterprise.inject.spi.ProcessObserverMethod;
import javax.enterprise.inject.spi.ProcessProducer;
import javax.enterprise.inject.spi.ProcessProducerField;
import javax.enterprise.inject.spi.ProcessProducerMethod;
import javax.enterprise.inject.spi.Producer;
import javax.inject.Inject;

import org.jboss.weld.extensions.bean.BeanBuilder;
import org.jboss.weld.extensions.bean.BeanLifecycle;
import org.jboss.weld.extensions.bean.Beans;
import org.jboss.weld.extensions.literal.DefaultLiteral;
import org.jboss.weld.extensions.reflection.Synthetic;
import org.jboss.weld.extensions.reflection.annotated.AnnotatedTypeBuilder;
import org.jboss.weld.extensions.reflection.annotated.AnnotationRedefiner;
import org.jboss.weld.extensions.reflection.annotated.RedefinitionContext;
import org.jboss.weld.extensions.unwraps.Unwraps;
import org.jboss.weld.extensions.unwraps.UnwrapsProducerBean;
import org.jboss.weld.extensions.util.Arrays2;
import org.jboss.weld.extensions.util.collections.Multimaps;
import org.jboss.weld.extensions.util.collections.SetMultimap;
import org.jboss.weld.extensions.util.collections.Supplier;

/**
 * Extension that wires in Generic Beans
 * 
 * @author Pete Muir
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 * 
 */
public class GenericBeanExtension implements Extension
{

   private static <T> Supplier<Set<T>> createHashSetSupplier()
   {
      return new Supplier<Set<T>>()
      {

         public Set<T> get()
         {
            return new HashSet<T>();
         }

      };
   }

   private static class BeanHolder<T>
   {

      private final AnnotatedType<T> type;
      private final Bean<T> bean;

      private BeanHolder(AnnotatedType<T> type, Bean<T> bean)
      {
         this.type = type;
         this.bean = bean;
      }

      public AnnotatedType<T> getType()
      {
         return type;
      }

      public Bean<T> getBean()
      {
         return bean;
      }

   }

   private static class ProducerMethodHolder<X, T>
   {

      private final AnnotatedMethod<X> producerMethod;
      private final AnnotatedMethod<X> disposerMethod;
      private final Bean<T> bean;

      private ProducerMethodHolder(AnnotatedMethod<X> producerMethod, AnnotatedMethod<X> disposerMethod, Bean<T> bean)
      {
         this.producerMethod = producerMethod;
         this.disposerMethod = disposerMethod;
         this.bean = bean;
      }

      public AnnotatedMethod<X> getProducerMethod()
      {
         return producerMethod;
      }

      public AnnotatedMethod<X> getDisposerMethod()
      {
         return disposerMethod;
      }

      public Bean<T> getBean()
      {
         return bean;
      }

   }

   private static class ProducerHolder<X, T>
   {

      private final AnnotatedMember<X> member;
      private final Producer<T> producer;

      private ProducerHolder(AnnotatedMember<X> method, Producer<T> producer)
      {
         this.member = method;
         this.producer = producer;
      }

      public AnnotatedMember<X> getMember()
      {
         return member;
      }

      public Producer<T> getProducer()
      {
         return producer;
      }

   }

   private static class FieldHolder<X, T>
   {

      private final AnnotatedField<X> field;
      private final Bean<T> bean;

      private FieldHolder(AnnotatedField<X> field, Bean<T> bean)
      {
         this.field = field;
         this.bean = bean;
      }

      public AnnotatedField<X> getField()
      {
         return field;
      }

      public Bean<T> getBean()
      {
         return bean;
      }

   }

   private static class ObserverMethodHolder<X, T>
   {
      private final AnnotatedMethod<X> method;
      private final ObserverMethod<T> observerMethod;

      private ObserverMethodHolder(AnnotatedMethod<X> method, ObserverMethod<T> observerMethod)
      {
         this.method = method;
         this.observerMethod = observerMethod;
      }

      public AnnotatedMethod<X> getMethod()
      {
         return method;
      }

      public ObserverMethod<T> getObserverMethod()
      {
         return observerMethod;
      }

   }

   // A map of generic configuration types to generic beans
   // Used to track the generic bean found
   private final SetMultimap<Class<? extends Annotation>, BeanHolder<?>> genericBeans;

   // A map of generic configuration types to observer methods on generic beans
   // Used to track observer methods found on generic bean
   private final SetMultimap<Class<? extends Annotation>, ObserverMethodHolder<?, ?>> genericBeanObserverMethods;

   // A map of generic configuration types to producer methods on generic beans
   // Used to track producers found on generic bean
   private final SetMultimap<Class<? extends Annotation>, ProducerMethodHolder<?, ?>> genericBeanProducerMethods;

   // A map of generic configuration types to producer fields on generic beans
   // Used to track the generic bean found
   private final SetMultimap<Class<? extends Annotation>, FieldHolder<?, ?>> genericBeanProducerFields;

   // A map of generic configuration types to generic bean injection targets
   // Used to track the generic bean found
   private final Map<AnnotatedType<?>, InjectionTarget<?>> genericInjectionTargets;

   // A map of a generic configuration types to generic configurations
   // Used to track the generic configuration producers found
   private final Map<Annotation, ProducerHolder<?, ?>> genericProducers;

   // A map of a generic configuration types to generic configurations
   // Used to track the generic configuration producers found
   private final Map<AnnotatedMember<?>, Bean<?>> genericProducerBeans;

   // tracks @Unwraps methods, as these need to be handled manually
   private final SetMultimap<Class<? extends Annotation>, AnnotatedMethod<?>> unwrapsMethods;

   // provides synthetic qualifiers that allow the annotated members to be
   // injected
   private final Synthetic.Provider annotatedMemberInjectionProvider;

   // qualifier that is added to all generic beans so they are not eligable for
   // injection
   private final Synthetic genericBeanQualifier;

   private final Set<String> errors;

   public GenericBeanExtension()
   {
      this.genericBeans = Multimaps.newSetMultimap(new HashMap<Class<? extends Annotation>, Collection<BeanHolder<?>>>(), GenericBeanExtension.<BeanHolder<?>> createHashSetSupplier());
      this.genericBeanProducerMethods = Multimaps.newSetMultimap(new HashMap<Class<? extends Annotation>, Collection<ProducerMethodHolder<?, ?>>>(), GenericBeanExtension.<ProducerMethodHolder<?, ?>> createHashSetSupplier());
      this.genericBeanObserverMethods = Multimaps.newSetMultimap(new HashMap<Class<? extends Annotation>, Collection<ObserverMethodHolder<?, ?>>>(), GenericBeanExtension.<ObserverMethodHolder<?, ?>> createHashSetSupplier());
      this.genericBeanProducerFields = Multimaps.newSetMultimap(new HashMap<Class<? extends Annotation>, Collection<FieldHolder<?, ?>>>(), GenericBeanExtension.<FieldHolder<?, ?>> createHashSetSupplier());
      this.genericInjectionTargets = new HashMap<AnnotatedType<?>, InjectionTarget<?>>();
      this.genericProducers = new HashMap<Annotation, ProducerHolder<?, ?>>();
      this.genericProducerBeans = new HashMap<AnnotatedMember<?>, Bean<?>>();
      this.unwrapsMethods = Multimaps.newSetMultimap(new HashMap<Class<? extends Annotation>, Collection<AnnotatedMethod<?>>>(), GenericBeanExtension.<AnnotatedMethod<?>> createHashSetSupplier());
      this.genericBeanQualifier = new Synthetic.SyntheticLiteral("org.jboss.weld.extensions.bean.generic.genericQualifier", Long.valueOf(0));
      this.errors = new HashSet<String>();
      this.annotatedMemberInjectionProvider = new Synthetic.Provider("org.jboss.weld.extensions.bean.generic.annotatedMember");
   }

   <X> void replaceInjectOnGenericBeans(@Observes ProcessAnnotatedType<X> event)
   {
      AnnotatedType<X> type = event.getAnnotatedType();
      if (type.isAnnotationPresent(Generic.class))
      {
         final Class<? extends Annotation> genericConfigurationType = type.getAnnotation(Generic.class).value();
         // validate that the configuration type is annotated correctly
         if (!genericConfigurationType.isAnnotationPresent(GenericConfiguration.class))
         {
            errors.add("Bean " + type.getJavaClass().getName() + " specifies generic annotation " + type.getAnnotation(Generic.class) + " however " + genericConfigurationType + " is not annotated @GenericConfiguration.");
         }
         else
         {
            Class<?> configType = genericConfigurationType.getAnnotation(GenericConfiguration.class).value();
            if (configType.isAnnotationPresent(Generic.class))
            {
               errors.add("Generic configuration type " + genericConfigurationType + " specifies a value() of " + configType + " however " + configType + " is a generic bean. Generic configuration types may not be generic beans");
            }
         }

         final AnnotatedTypeBuilder<X> builder = new AnnotatedTypeBuilder<X>().readFromType(type);
         builder.addToClass(genericBeanQualifier);
         builder.redefine(Inject.class, new AnnotationRedefiner<Inject>()
         {

            public void redefine(RedefinitionContext<Inject> ctx)
            {
               if (ctx.getAnnotatedElement() instanceof Field)
               {
                  if (ctx.getRawType().equals(genericConfigurationType))
                  {
                     // This is a Generic configuration injection point
                     ctx.getAnnotationBuilder().remove(Inject.class).add(InjectGenericLiteral.INSTANCE);
                  }
                  else if (ctx.getAnnotatedElement().isAnnotationPresent(GenericBean.class))
                  {
                     // This is a Generic bean injection point
                     ctx.getAnnotationBuilder().remove(Inject.class).add(InjectGenericLiteral.INSTANCE);
                  }
                  else if (ctx.getAnnotatedElement().isAnnotationPresent(GenericProduct.class))
                  {
                     /*
                      * This is an injection point where @GenericProduct has
                      * been used, so we have to take control of injection
                      */
                     ctx.getAnnotationBuilder().remove(Inject.class).add(InjectGenericLiteral.INSTANCE);
                  }
               }
            }

         });
         builder.redefine(Produces.class, new AnnotationRedefiner<Produces>() {
            
            public void redefine(RedefinitionContext<Produces> ctx)
            {
               // Add the marker qualifier
               ctx.getAnnotationBuilder().add(GenericMarkerLiteral.INSTANCE).add(genericBeanQualifier);
            }
            
         });

         builder.redefine(GenericProduct.class, new AnnotationRedefiner<GenericProduct>()
         {
            public void redefine(RedefinitionContext<GenericProduct> ctx)
            {
               // if it is a parameter annotation
               if (!(ctx.getAnnotatedElement() instanceof AccessibleObject))
               {
                  // stick an InjectGeneric as a marker.
                  ctx.getAnnotationBuilder().remove(GenericProduct.class).add(InjectGenericLiteral.INSTANCE);
                  if (ctx.getRawType().isAnnotationPresent(Generic.class))
                  {
                     ctx.getAnnotationBuilder().add(genericBeanQualifier);
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
         Class<? extends Annotation> genericType = type.getAnnotation(Generic.class).value();
         genericBeans.put(genericType, new BeanHolder<X>(event.getAnnotatedBeanClass(), event.getBean()));
         for (AnnotatedMethod<? super X> m : event.getAnnotatedBeanClass().getMethods())
         {
            if (m.isAnnotationPresent(Unwraps.class))
            {
               unwrapsMethods.put(genericType, m);
            }
         }
      }
   }

   <X, T> void registerGenericBeanProducerMethod(@Observes ProcessProducerMethod<X, T> event)
   {
      AnnotatedType<X> declaringType = event.getAnnotatedProducerMethod().getDeclaringType();
      Annotation genericConfiguration = getGenericConfiguration(event.getAnnotated());
      if (declaringType.isAnnotationPresent(Generic.class))
      {
         genericBeanProducerMethods.put(declaringType.getAnnotation(Generic.class).value(), getProducerMethodHolder(event));
      }
      else if (genericConfiguration != null)
      {
         if (validateGenericProducer(genericConfiguration, event.getBean(), event.getAnnotatedProducerMethod()))
         {
            genericProducerBeans.put(event.getAnnotatedProducerMethod(), event.getBean());
         }
      }
   }

   private <X> boolean validateGenericProducer(Annotation genericConfiguration, Bean<?> bean, AnnotatedMember<X> member)
   {
      Class<?> configType = genericConfiguration.annotationType().getAnnotation(GenericConfiguration.class).value();
      boolean valid = false;
      for (Type t : bean.getTypes())
      {
         if (t instanceof Class<?>)
         {
            Class<?> c = (Class<?>) t;
            if (configType.isAssignableFrom(c))
            {
               valid = true;
               break;
            }
         }
      }
      if (!valid)
      {
         AnnotatedType<X> declaringType = member.getDeclaringType();
         errors.add("Generic producer method is not of correct type. Producer: " + declaringType.getJavaClass().getName() + "." + member.getJavaMember().getName() + ". Expected producer to be of type " + configType + " but was actually " + member.getBaseType());
      }
      return valid;
   }

   private static <X, T> ProducerMethodHolder<X, T> getProducerMethodHolder(ProcessProducerMethod<X, T> event)
   {
      // Only register a disposer method if it exists
      // Blocked by WELD-572
      if (event.getAnnotatedDisposedParameter() instanceof AnnotatedParameter<?>)
      {
         return new ProducerMethodHolder<X, T>(event.getAnnotatedProducerMethod(), (AnnotatedMethod<X>) event.getAnnotatedDisposedParameter().getDeclaringCallable(), event.getBean());
      }
      else
      {
         return new ProducerMethodHolder<X, T>(event.getAnnotatedProducerMethod(), null, event.getBean());
      }
   }

   <T, X> void registerGenericBeanObserverMethod(@Observes ProcessObserverMethod<T, X> event)
   {
      AnnotatedType<X> declaringType = event.getAnnotatedMethod().getDeclaringType();
      if (declaringType.isAnnotationPresent(Generic.class))
      {
         AnnotatedMethod<X> method = event.getAnnotatedMethod();
         Class<? extends Annotation> genericConfigurationType = declaringType.getAnnotation(Generic.class).value();
         genericBeanObserverMethods.put(genericConfigurationType, new ObserverMethodHolder<X, T>(method, event.getObserverMethod()));
      }
   }

   <X, T> void registerGenericBeanProducerField(@Observes ProcessProducerField<X, T> event)
   {

      AnnotatedType<X> declaringType = event.getAnnotatedProducerField().getDeclaringType();
      Annotation genericConfiguration = getGenericConfiguration(event.getAnnotated());
      if (declaringType.isAnnotationPresent(Generic.class))
      {
         AnnotatedField<X> field = event.getAnnotatedProducerField();
         Class<? extends Annotation> genericConfigurationType = declaringType.getAnnotation(Generic.class).value();
         genericBeanProducerFields.put(genericConfigurationType, new FieldHolder<X, T>(field, event.getBean()));
      }
      else if (genericConfiguration != null)
      {
         if (validateGenericProducer(genericConfiguration, event.getBean(), event.getAnnotatedProducerField()))
         {
            genericProducerBeans.put(event.getAnnotatedProducerField(), event.getBean());
         }
      }
   }

   <X> void registerGenericBeanInjectionTarget(@Observes ProcessInjectionTarget<X> event)
   {
      AnnotatedType<X> type = event.getAnnotatedType();
      if (type.isAnnotationPresent(Generic.class))
      {
         genericInjectionTargets.put(type, event.getInjectionTarget());
      }
   }

   <X, T> void processGenericProducer(@Observes ProcessProducer<X, T> event, BeanManager beanManager)
   {
      Annotation genericConfiguration = getGenericConfiguration(event.getAnnotatedMember());
      if (genericConfiguration != null)
      {
         // Ensure that this generic configuration hasn't been registered yet!
         if (genericProducers.containsKey(genericConfiguration))
         {
            throw new IllegalStateException("Generic configuration " + genericConfiguration + " is defined twice [" + event.getAnnotatedMember() + ", " + genericProducers.get(genericConfiguration) + "]");
         }
         Producer<T> originalProducer = event.getProducer();
         Class<?> requiredProducerType = genericConfiguration.annotationType().getAnnotation(GenericConfiguration.class).value();

         // Register the producer for use later
         genericProducers.put(genericConfiguration, new ProducerHolder<X, T>(event.getAnnotatedMember(), originalProducer));
      }
   }

   void createGenericBeans(@Observes AfterBeanDiscovery event, BeanManager beanManager)
   {
      // For each generic configuration type, we iterate the generic configurations
      for (Entry<Annotation, ProducerHolder<?, ?>> genericConfigurationEntry : genericProducers.entrySet())
      {
         Class<? extends Annotation> producerScope = Dependent.class;
         for (Annotation i : genericConfigurationEntry.getValue().getMember().getAnnotations())
         {
            if (beanManager.isScope(i.annotationType()))
            {
               producerScope = i.annotationType();
            }
         }
         Annotation genericConfiguration = genericConfigurationEntry.getKey();
         Set<Annotation> qualifiers = new HashSet<Annotation>();
         qualifiers.addAll(Beans.getQualifiers(beanManager, genericConfigurationEntry.getValue().getMember().getAnnotations()));
         if (qualifiers.isEmpty())
         {
            qualifiers.add(DefaultLiteral.INSTANCE);
         }
         Class<? extends Annotation> genericConfigurationType = genericConfiguration.annotationType();
         if (!genericBeans.containsKey(genericConfigurationType))
         {
            throw new IllegalStateException("No generic bean definition exists for " + genericConfigurationType + ", but a generic producer does: " + genericConfigurationEntry.getValue());
         }
         // Add a generic configuration bean for each generic configuration producer (allows us to inject the generic configuration annotation back into the generic bean)
         event.addBean(createGenericConfigurationBean(beanManager, genericConfiguration, qualifiers));

         // Register the GenericProduct bean

         event.addBean(createGenericProductAnnotatedMemberBean(beanManager, genericConfiguration, qualifiers));

         if (genericBeanProducerMethods.containsKey(genericConfigurationType))
         {
            for (ProducerMethodHolder<?, ?> holder : genericBeanProducerMethods.get(genericConfigurationType))
            {
               Class<? extends Annotation> scopeOverride = null;
               if (holder.getProducerMethod().isAnnotationPresent(ApplyScope.class))
               {
                  scopeOverride = producerScope;
               }
               event.addBean(createGenericProducerMethod(holder, genericConfiguration, beanManager, scopeOverride));
            }
         }
         if (genericBeanProducerFields.containsKey(genericConfigurationType))
         {
            for (FieldHolder<?, ?> holder : genericBeanProducerFields.get(genericConfigurationType))
            {
               Class<? extends Annotation> scopeOverride = null;
               if (holder.getField().isAnnotationPresent(ApplyScope.class))
               {
                  scopeOverride = producerScope;
               }
               event.addBean(createGenericProducerField(holder.getBean(), genericConfiguration, holder.getField(), beanManager, scopeOverride));
            }
         }
         if (genericBeanObserverMethods.containsKey(genericConfigurationType))
         {
            for (ObserverMethodHolder<?, ?> holder : genericBeanObserverMethods.get(genericConfigurationType))
            {
               event.addObserverMethod(createGenericObserverMethod(holder.getObserverMethod(), genericConfiguration, holder.getMethod(), beanManager));
            }

         }
         if(unwrapsMethods.containsKey(genericConfigurationType))
         {
            for(AnnotatedMethod<?> i : unwrapsMethods.get(genericConfigurationType))
            {
               AnnotatedMember<?> member = genericConfigurationEntry.getValue().getMember();
               Set<Annotation> unwrapsQualifiers = Beans.getQualifiers(beanManager, i.getAnnotations(), member.getAnnotations());
               if (unwrapsQualifiers.isEmpty())
               {
                  unwrapsQualifiers.add(DefaultLiteral.INSTANCE);
               }
               Set<Annotation> beanQualifiers = Beans.getQualifiers(beanManager, i.getDeclaringType().getAnnotations(), member.getAnnotations());
               if (beanQualifiers.isEmpty())
               {
                  beanQualifiers.add(DefaultLiteral.INSTANCE);
               }
               beanQualifiers.remove(genericBeanQualifier);
               event.addBean(new UnwrapsProducerBean(i, unwrapsQualifiers, beanQualifiers, beanManager));
            }
         }
         // For each generic bean that uses this genericConfigurationType, register a generic bean for this generic configuration 
         for (BeanHolder<?> genericBeanHolder : genericBeans.get(genericConfigurationType))
         {
            // Register the generic bean, this is the underlying definition, with the synthetic qualifier
            Class<? extends Annotation> scopeOverride = null;
            if (genericBeanHolder.getType().isAnnotationPresent(ApplyScope.class))
            {
               scopeOverride = producerScope;
            }
            Bean<?> genericBean = createGenericBean(genericBeanHolder, genericConfiguration, beanManager, scopeOverride);
            event.addBean(genericBean);
         }
      }
   }

   private static Annotation getGenericConfiguration(Annotated annotated)
   {
      // Only process the producer as a generic producer, if it has an annotation meta-annotated with GenericConfiguration
      Set<Annotation> genericConfigurationAnnotiations = getAnnotations(annotated, GenericConfiguration.class);

      if (genericConfigurationAnnotiations.size() > 1)
      {
         throw new IllegalStateException("Can only have one generic configuration annotation on " + annotated);
      }
      else if (genericConfigurationAnnotiations.size() == 1)
      {
         return genericConfigurationAnnotiations.iterator().next();
      }
      else
      {
         return null;
      }
   }

   private Bean<?> createGenericConfigurationBean(BeanManager beanManager, final Annotation genericConfiguration, Set<Annotation> qualifiers)
   {
      // We don't have a bean created for this generic configuration annotation. Create it, store it to be added later
      // TODO make this passivation capable?
      BeanBuilder<Annotation> builder = new BeanBuilder<Annotation>(beanManager).beanClass(genericConfiguration.annotationType()).types(Arrays2.<Type> asSet(genericConfiguration.annotationType(), Object.class)).scope(Dependent.class).qualifiers(qualifiers).beanLifecycle(new BeanLifecycle<Annotation>()
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
   
   private Bean<AnnotatedMember<?>> createGenericProductAnnotatedMemberBean(BeanManager beanManager, final Annotation genericConfiguration, Set<Annotation> qualifiers)
   {
      @SuppressWarnings("unchecked")
      final ProducerHolder<?, ?> holder = genericProducers.get(genericConfiguration);

      // TODO make this passivation capable?
      BeanBuilder<AnnotatedMember<?>> builder = new BeanBuilder<AnnotatedMember<?>>(beanManager).beanClass(AnnotatedMember.class).qualifiers(Collections.<Annotation> singleton(annotatedMemberInjectionProvider.get(genericConfiguration))).beanLifecycle(new BeanLifecycle<AnnotatedMember<?>>()
      {

         public void destroy(Bean<AnnotatedMember<?>> bean, AnnotatedMember<?> instance, CreationalContext<AnnotatedMember<?>> ctx)
         {
            // No-op
         }

         public AnnotatedMember<?> create(Bean<AnnotatedMember<?>> bean, CreationalContext<AnnotatedMember<?>> ctx)
         {
            return holder.getMember();
         }
      });
      return builder.create();
   }

   private <X> Bean<X> createGenericBean(BeanHolder<X> holder, Annotation genericConfiguration, BeanManager beanManager, Class<? extends Annotation> scopeOverride)
   {
      Set<Annotation> declaringBeanQualifiers = getQualifiers(beanManager, genericConfiguration, Collections.EMPTY_SET);
      return new GenericManagedBean<X>(holder.getBean(), genericConfiguration, (InjectionTarget<X>) genericInjectionTargets.get(holder.getType()), holder.getType(), declaringBeanQualifiers, scopeOverride, annotatedMemberInjectionProvider, beanManager);
   }

   private <X, T> Bean<T> createGenericProducerMethod(ProducerMethodHolder<X, T> holder, Annotation genericConfiguration, BeanManager beanManager, Class<? extends Annotation> scopeOverride)
   {
      Set<Annotation> qualifiers = getQualifiers(beanManager, genericConfiguration, holder.getBean().getQualifiers());
      Set<Annotation> declaringBeanQualifiers = getQualifiers(beanManager, genericConfiguration, Collections.EMPTY_SET);
      return new GenericProducerMethod<T, X>(holder.getBean(), genericConfiguration, holder.getProducerMethod(), holder.getDisposerMethod(), qualifiers, declaringBeanQualifiers, scopeOverride, beanManager);
   }

   @SuppressWarnings("unchecked")
   public Set<Annotation> getQualifiers(BeanManager beanManager, Annotation genericConfiguration, Iterable<Annotation> annotations)
   {
      return Beans.getQualifiers(beanManager, genericProducers.get(genericConfiguration).getMember().getAnnotations(), annotations);
   }

   private <X, T> ObserverMethod<T> createGenericObserverMethod(ObserverMethod<T> originalObserverMethod, Annotation genericConfiguration, AnnotatedMethod<X> method, BeanManager beanManager)
   {
      Set<Annotation> qualifiers = getQualifiers(beanManager, genericConfiguration, originalObserverMethod.getObservedQualifiers());
      Set<Annotation> declaringBeanQualifiers = getQualifiers(beanManager, genericConfiguration, Collections.EMPTY_SET);
      return new GenericObserverMethod<T, X>(originalObserverMethod, method, genericConfiguration, qualifiers, declaringBeanQualifiers, beanManager);
   }

   private <X, T> Bean<T> createGenericProducerField(Bean<T> originalBean, Annotation genericConfiguration, AnnotatedField<X> field, BeanManager beanManager, Class<? extends Annotation> scopeOverride)
   {
      Set<Annotation> qualifiers = getQualifiers(beanManager, genericConfiguration, originalBean.getQualifiers());
      Set<Annotation> declaringBeanQualifiers = getQualifiers(beanManager, genericConfiguration, Collections.EMPTY_SET);
      return new GenericProducerField<T, X>(originalBean, genericConfiguration, field, qualifiers, declaringBeanQualifiers, scopeOverride, beanManager);
   }

   void cleanup(@Observes AfterDeploymentValidation event)
   {
      // Defensively clear maps to help with GC
      this.genericBeanObserverMethods.clear();
      this.genericBeanProducerFields.clear();
      this.genericBeanProducerMethods.clear();
      this.genericBeans.clear();
      this.genericProducers.clear();


      this.genericInjectionTargets.clear();
      for (String s : errors)
      {
         event.addDeploymentProblem(new Exception(s));
      }
   }

}
