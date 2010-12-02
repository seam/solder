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
package org.jboss.seam.solder.bean.defaultbean;

import static org.jboss.seam.solder.util.collections.Multimaps.newSetMultimap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.ProcessObserverMethod;
import javax.enterprise.inject.spi.ProcessProducerField;
import javax.enterprise.inject.spi.ProcessProducerMethod;

import org.jboss.seam.solder.bean.Beans;
import org.jboss.seam.solder.literal.DefaultLiteral;
import org.jboss.seam.solder.reflection.Synthetic;
import org.jboss.seam.solder.reflection.annotated.AnnotatedTypeBuilder;
import org.jboss.seam.solder.util.collections.SetMultimap;
import org.jboss.seam.solder.util.collections.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registers beans annotated @DefaultBean
 * 
 * During the ProcessAnnotatedTypePhase beans and producers annotated with @Default
 * have a synthetic qualifier added to them and existing qualifiers removed.
 * 
 * During the ProcessBean phases these default beans are stored for later use
 * 
 * If no alternative bean was observed for each of the default beans then the
 * stored bean is added as a forwarding bean with it's original set of
 * qualifiers
 * 
 * @author Stuart Douglas
 * 
 */
public class DefaultBeanExtension implements Extension
{

   private static final String QUALIFIER_NAMEPSACE = "org.jboss.seam.solder.defaultbean";

   private static final String PRODUCER_QUALIFIER_NAMEPSACE = "org.jboss.seam.solder.defaultbean.producer";

   private static final Logger log = LoggerFactory.getLogger(DefaultBeanExtension.class);

   private boolean beanDiscoveryOver = false;

   /**
    * list of all beans in the system
    */
   private final List<Bean<?>> processedBeans = new LinkedList<Bean<?>>();;

   /**
    * stores type and qualifier information for default beans
    */
   private final Map<Synthetic, DefaultBeanType> beanTypeInformation = new HashMap<Synthetic, DefaultBeanType>();

   /**
    * all default managed beans
    */
   private final Map<Synthetic, Bean<?>> defaultManagedBeans = new HashMap<Synthetic, Bean<?>>();

   /**
    * all default producer beans
    */
   private final Map<Synthetic, Bean<?>> defaultProducerMethods = new HashMap<Synthetic, Bean<?>>();

   /**
    * all default producer fields
    */
   private final Map<Synthetic, Bean<?>> defaultProducerFields = new HashMap<Synthetic, Bean<?>>();

   /**
    * qualifiers for observer methods on default managed beans
    */
   private final SetMultimap<Synthetic, ObserverMethodInfo<?>> defaultObserverMethodsByBean = newSetMultimap(new HashMap<Synthetic, Collection<ObserverMethodInfo<?>>>(), DefaultBeanExtension.<ObserverMethodInfo<?>>createHashSetSupplier());


   private final Map<Method, ObserverMethodInfo<?>> defaultObserverMethods = new HashMap<Method, ObserverMethodInfo<?>>();

   /**
    * map of producer method/field qualifiers to default bean synthetic
    * qualifiers
    */
   private final Map<Synthetic, DefaultBeanQualifiers> producerToDeclaringDefaultBean = new HashMap<Synthetic, DefaultBeanQualifiers>();
   
   /**
    * map of producer method info to the annotated method
    */
   private final Map<Synthetic, DefaultProducerMethodInfo<?>> producerAnnotatedMethods = new HashMap<Synthetic, DefaultProducerMethodInfo<?>>();

   /**
    * map of producer methods to the annotated field
    */
   private final Map<Synthetic, AnnotatedField<?>> producerAnnotatedFields = new HashMap<Synthetic, AnnotatedField<?>>();
   
   private final Synthetic.Provider syntheticProvider = new Synthetic.Provider(QUALIFIER_NAMEPSACE);

   private final Synthetic.Provider producerSyntheticProvider = new Synthetic.Provider(PRODUCER_QUALIFIER_NAMEPSACE);

   private final Set<Throwable> deploymentProblems = new HashSet<Throwable>();

   <X> void processAnnotatedType(@Observes ProcessAnnotatedType<X> event, BeanManager beanManager)
   {
      boolean defaultBean = false;
      AnnotatedType<X> tp = event.getAnnotatedType();
      AnnotatedTypeBuilder<X> builder = null;
      Synthetic declaringBeanSyntheticQualifier = null;
      Set<Annotation> declaringBeanQualifiers = null;
      if (event.getAnnotatedType().isAnnotationPresent(DefaultBean.class))
      {
         Set<Annotation> qualifiers = new HashSet<Annotation>();
         defaultBean = true;
         builder = new AnnotatedTypeBuilder<X>().readFromType(tp);
         for (Annotation a : tp.getAnnotations())
         {
            // remove the qualifiers
            if (beanManager.isQualifier(a.annotationType()))
            {
               qualifiers.add(a);
               builder.removeFromClass(a.annotationType());
            }
         }
         if (qualifiers.isEmpty())
         {
            qualifiers.add(DefaultLiteral.INSTANCE);
         }
         declaringBeanQualifiers = new HashSet<Annotation>(qualifiers);
         declaringBeanSyntheticQualifier = syntheticProvider.get();
         // store the qualifiers for later
         beanTypeInformation.put(declaringBeanSyntheticQualifier, new DefaultBeanType(qualifiers, tp.getAnnotation(DefaultBean.class).value()));
         builder.addToClass(declaringBeanSyntheticQualifier);
         // now loop though observer methods and replace qualifiers
         for (AnnotatedMethod<? super X> m : tp.getMethods())
         {
            AnnotatedParameter<? super X> observerParameter = null;
            for (AnnotatedParameter<? super X> p : m.getParameters())
            {
               if (p.isAnnotationPresent(Observes.class))
               {
                  observerParameter = p;
                  break;
               }
            }
            if (observerParameter != null)
            {
               // we have an observer method
               Set<Annotation> observerQualifiers = new HashSet<Annotation>();
               for (Annotation a : observerParameter.getAnnotations())
               {
                  // remove all qualifiers
                  if (beanManager.isQualifier(a.annotationType()))
                  {
                     observerQualifiers.add(a);
                     builder.removeFromMethodParameter(m.getJavaMember(), observerParameter.getPosition(), a.annotationType());
                  }
               }
               if (qualifiers.isEmpty())
               {
                  qualifiers.add(DefaultLiteral.INSTANCE);
               }
               builder.addToMethodParameter(m.getJavaMember(), observerParameter.getPosition(), declaringBeanSyntheticQualifier);
               ObserverMethodInfo<?> info = ObserverMethodInfo.of(observerQualifiers, declaringBeanQualifiers, m, declaringBeanSyntheticQualifier);
               defaultObserverMethodsByBean.put(declaringBeanSyntheticQualifier, info);
               defaultObserverMethods.put(m.getJavaMember(), info);
            }
         }
      }
      // now look for producer methods
      // if this bean is a default bean then all producers are default beans
      // otherwise the annotaion needs to be present
      for (AnnotatedMethod<? super X> m : tp.getMethods())
      {
         if (m.isAnnotationPresent(Produces.class) && (defaultBean || m.isAnnotationPresent(DefaultBean.class)))
         {
            if (declaringBeanQualifiers == null)
            {
               declaringBeanQualifiers = new HashSet<Annotation>(Beans.getQualifiers(beanManager, tp.getAnnotations()));
               if (declaringBeanQualifiers.isEmpty())
               {
                  declaringBeanQualifiers.add(DefaultLiteral.INSTANCE);
               }
            }
            if (builder == null)
            {
               builder = new AnnotatedTypeBuilder<X>().readFromType(tp);
            }
            Set<Annotation> qualifiers = new HashSet<Annotation>();
            for (Annotation a : m.getAnnotations())
            {
               // remove the qualifiers
               if (beanManager.isQualifier(a.annotationType()))
               {
                  qualifiers.add(a);
                  builder.removeFromMethod(m, a.annotationType());
               }
            }
            if (qualifiers.isEmpty())
            {
               qualifiers.add(DefaultLiteral.INSTANCE);
            }
            Synthetic syntheticQualifier = producerSyntheticProvider.get();
            // store the qualifiers for later
            Type type = null;
            // if the type is not explicity set then we infer it
            if (m.isAnnotationPresent(DefaultBean.class))
            {
               type = m.getAnnotation(DefaultBean.class).value();
            }
            else
            {
               type = m.getJavaMember().getGenericReturnType();
            }
            beanTypeInformation.put(syntheticQualifier, new DefaultBeanType(qualifiers, type));
            builder.addToMethod(m, syntheticQualifier);
            producerToDeclaringDefaultBean.put(syntheticQualifier, new DefaultBeanQualifiers(declaringBeanSyntheticQualifier, declaringBeanQualifiers));
         }
      }

      for (AnnotatedField<? super X> f : tp.getFields())
      {
         if (f.isAnnotationPresent(Produces.class) && (defaultBean || f.isAnnotationPresent(DefaultBean.class)))
         {
            if (declaringBeanQualifiers == null)
            {
               declaringBeanQualifiers = new HashSet<Annotation>(Beans.getQualifiers(beanManager, tp.getAnnotations()));
               if (declaringBeanQualifiers.isEmpty())
               {
                  declaringBeanQualifiers.add(DefaultLiteral.INSTANCE);
               }
            }
            // we do not support producer fields on normal scoped beans
            // as proxies prevent us from reading the fields
            for (Annotation i : tp.getAnnotations())
            {
               if (beanManager.isNormalScope(i.annotationType()))
               {
                  deploymentProblems.add(new RuntimeException("Default producer fields are not supported on normal scoped beans. Field: " + f + " Declaring Bean: " + tp));
               }
            }
            if (builder == null)
            {
               builder = new AnnotatedTypeBuilder<X>().readFromType(tp);
            }
            Set<Annotation> qualifiers = new HashSet<Annotation>();
            for (Annotation a : f.getAnnotations())
            {
               // remove the qualifiers
               if (beanManager.isQualifier(a.annotationType()))
               {
                  qualifiers.add(a);
                  builder.removeFromField(f, a.annotationType());
               }
            }
            if (qualifiers.isEmpty())
            {
               qualifiers.add(DefaultLiteral.INSTANCE);
            }
            Synthetic syntheticQualifier = producerSyntheticProvider.get();
            // store the qualifiers for later
            Type type = null;
            if (f.isAnnotationPresent(DefaultBean.class))
            {
               type = f.getAnnotation(DefaultBean.class).value();
            }
            else
            {
               type = f.getJavaMember().getGenericType();
            }
            beanTypeInformation.put(syntheticQualifier, new DefaultBeanType(qualifiers, type));
            builder.addToField(f, syntheticQualifier);
            producerToDeclaringDefaultBean.put(syntheticQualifier, new DefaultBeanQualifiers(declaringBeanSyntheticQualifier, declaringBeanQualifiers));
         }
      }
      if (builder != null)
      {
         event.setAnnotatedType(builder.create());
      }
   }

   <X> void processBean(@Observes ProcessBean<X> event)
   {
      // after the bean discovery is over we don't need to do any more
      // processing
      if (beanDiscoveryOver)
      {
         return;
      }
      Bean<X> b = event.getBean();
      processedBeans.add(b);
      Synthetic qualifier = null;
      for (Annotation a : b.getQualifiers())
      {
         if (a instanceof Synthetic)
         {
            Synthetic sa = (Synthetic) a;
            if (sa.namespace().equals(QUALIFIER_NAMEPSACE))
            {
               qualifier = sa;
               break;
            }
         }
      }
      if (qualifier != null)
      {
         defaultManagedBeans.put(qualifier, b);
      }
   }

   <T, X> void processProducer(@Observes ProcessProducerMethod<T, X> event)
   {
      if (beanDiscoveryOver)
      {
         return;
      }
      Bean<X> b = event.getBean();
      Synthetic qualifier = handleProducerBean(b);
      if (qualifier != null)
      {
         // store producer method information
         defaultProducerMethods.put(qualifier, event.getBean());
         AnnotatedMethod<T> method = event.getAnnotatedProducerMethod();
         AnnotatedMethod<T> disposerMethod = null;
         if (event.getAnnotatedDisposedParameter() != null)
         {
            disposerMethod = (AnnotatedMethod<T>) event.getAnnotatedDisposedParameter().getDeclaringCallable();
         }
         producerAnnotatedMethods.put(qualifier, new DefaultProducerMethodInfo<T>(method, disposerMethod));
      }
   }

   <T, X> void processProducer(@Observes ProcessProducerField<T, X> event)
   {
      if (beanDiscoveryOver)
      {
         return;
      }
      Bean<X> b = event.getBean();
      Synthetic qualifier = handleProducerBean(b);
      if (qualifier != null)
      {
         defaultProducerFields.put(qualifier, event.getBean());
         producerAnnotatedFields.put(qualifier, event.getAnnotatedProducerField());
      }
   }

   <X> Synthetic handleProducerBean(Bean<X> b)
   {
      Synthetic qualifier = null;
      for (Annotation a : b.getQualifiers())
      {
         if (a instanceof Synthetic)
         {
            Synthetic sa = (Synthetic) a;
            if (sa.namespace().equals(PRODUCER_QUALIFIER_NAMEPSACE))
            {
               qualifier = sa;
               break;
            }
         }
      }
      return qualifier;
   }

   <T, X> void processObserverMethod(@Observes ProcessObserverMethod<T, X> event)
   {
      Synthetic qualifier = null;
      for (Annotation a : event.getObserverMethod().getObservedQualifiers())
      {
         if (a instanceof Synthetic)
         {
            Synthetic sa = (Synthetic) a;
            if (sa.namespace().equals(QUALIFIER_NAMEPSACE))
            {
               qualifier = sa;
               break;
            }
         }
      }
      if (qualifier != null)
      {
         ObserverMethodInfo<?> info = defaultObserverMethods.get(event.getAnnotatedMethod().getJavaMember());
         info.setDelegate(event.getObserverMethod());
      }
   }

   void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager manager)
   {
      beanDiscoveryOver = true;
      // first check for duplicate default bean definitions
      Map<DefaultBeanType, Bean<?>> duplicateDetectionMap = new HashMap<DefaultBeanType, Bean<?>>();
      for (Entry<Synthetic, DefaultBeanType> e : beanTypeInformation.entrySet())
      {
         Bean<?> bean = defaultManagedBeans.get(e.getKey());
         if (bean == null)
         {
            bean = defaultProducerMethods.get(e.getKey());
         }
         if (bean == null)
         {
            bean = defaultProducerFields.get(e.getKey());
         }
         if (duplicateDetectionMap.containsKey(e.getValue()))
         {
            Bean<?> other = duplicateDetectionMap.get(e.getValue());
            deploymentProblems.add(new RuntimeException("Two default beans with the same type and qualifiers: Type: " + e.getValue().getType() + " Qualifiers: " + e.getValue().getQualifiers() + " Beans are " + other.toString() + " and " + bean.toString()));
         }
         duplicateDetectionMap.put(e.getValue(), bean);
      }

      // loop over all installed beans and see if they match any default beans
      if (beanTypeInformation.size() > 0)
      {
         for (Bean<?> processedBean : processedBeans)
         {
            Iterator<Entry<Synthetic, DefaultBeanType>> it = beanTypeInformation.entrySet().iterator();
            while (it.hasNext())
            {
               Entry<Synthetic, DefaultBeanType> definition = it.next();
               if (definition.getValue().matches(processedBean))
               {
                  Synthetic qual = definition.getKey();
                  Bean<?> bean = null;
                  // remove the default bean from the beans to be installed
                  bean = defaultManagedBeans.remove(qual);
                  if (bean == null)
                  {
                     bean = defaultProducerMethods.remove(qual);
                  }
                  if (bean == null)
                  {
                     bean = defaultProducerFields.remove(qual);
                  }
                  log.info("Preventing install of default bean " + bean);
                  it.remove();
               }
            }
         }
      }
      Set<Synthetic> allDefaultBeanQualifiers = new HashSet<Synthetic>(defaultManagedBeans.keySet());
      allDefaultBeanQualifiers.addAll(defaultProducerFields.keySet());
      allDefaultBeanQualifiers.addAll(defaultProducerMethods.keySet());

      for (Synthetic qual : allDefaultBeanQualifiers)
      {
         final DefaultBeanType beanInfo = beanTypeInformation.get(qual);
         final HashSet<Type> types = new HashSet<Type>();
         types.add(Object.class);
         types.add(beanInfo.getType());
         final Set<Annotation> qualifiers = new HashSet<Annotation>(beanInfo.getQualifiers());
         if (defaultManagedBeans.containsKey(qual))
         {
            Bean<?> db = DefaultManagedBean.of(defaultManagedBeans.get(qual), beanInfo.getType(), types, qualifiers, manager);
            log.info("Installing default managed bean " + db);
            event.addBean(db);
            Set<ObserverMethodInfo<?>> observers = defaultObserverMethodsByBean.get(qual);
            for (ObserverMethodInfo<?> i : observers)
            {
               DefaultObserverMethod<?, ?> obs = DefaultObserverMethod.of(i.getDelegate(), i.getAnnotatedMethod(), qualifiers, i.getObserverParameterQualifier(), db, manager);
               event.addObserverMethod(obs);
            }
         }
         else if (defaultProducerMethods.containsKey(qual))
         {
            Synthetic declaringDefaultBean = this.producerToDeclaringDefaultBean.get(qual).getSyntheticQualifier();
            Set<Annotation> declaringBeanQualifiers;
            if (declaringDefaultBean != null && !beanTypeInformation.containsKey(declaringDefaultBean))
            {
               // this is a default producer method that was declared on a
               // default bean that has been replaced
               declaringBeanQualifiers = Collections.singleton((Annotation) declaringDefaultBean);
            }
            else
            {
               declaringBeanQualifiers = this.producerToDeclaringDefaultBean.get(qual).getQualifiers();
            }
            DefaultProducerMethodInfo<?> info = producerAnnotatedMethods.get(qual);
            Bean<?> db = createDefaultProducerMethod(defaultProducerMethods.get(qual), qual, beanInfo, types, qualifiers, declaringBeanQualifiers, info, manager);
            log.info("Installing default producer bean " + db);
            event.addBean(db);
         }
         else if (defaultProducerFields.containsKey(qual))
         {
            Synthetic declaringDefaultBean = this.producerToDeclaringDefaultBean.get(qual).getSyntheticQualifier();
            Set<Annotation> declaringBeanQualifiers;
            if (declaringDefaultBean != null && !beanTypeInformation.containsKey(declaringDefaultBean))
            {
               // this is a default producer method that was declared on a
               // default bean that has been replaced
               declaringBeanQualifiers = Collections.singleton((Annotation) declaringDefaultBean);
            }
            else
            {
               declaringBeanQualifiers = this.producerToDeclaringDefaultBean.get(qual).getQualifiers();
            }
            Bean<?> db = DefaultProducerField.of(defaultProducerFields.get(qual), beanInfo.getType(), types, qualifiers, declaringBeanQualifiers, producerAnnotatedFields.get(qual), manager);
            log.info("Installing default producer bean " + db);
            event.addBean(db);
         }
      }
      for (Throwable e : deploymentProblems)
      {
         event.addDefinitionError(e);
      }
   }

   void afterDeploymentValidation(@Observes AfterDeploymentValidation event)
   {
      this.processedBeans.clear();
      this.beanTypeInformation.clear();
      this.producerToDeclaringDefaultBean.clear();
      this.producerSyntheticProvider.clear();
      this.producerAnnotatedFields.clear();
      this.producerAnnotatedMethods.clear();
   }

   private static class DefaultBeanType
   {
      private final Set<Annotation> qualifiers;
      private final Type type;

      public DefaultBeanType(Set<Annotation> qualifiers, Type type)
      {
         this.qualifiers = new HashSet<Annotation>(qualifiers);
         this.type = type;
      }

      public Set<Annotation> getQualifiers()
      {
         return qualifiers;
      }

      public Type getType()
      {
         return type;
      }

      private boolean matches(Bean<?> bean)
      {
         if (bean.getTypes().contains(type))
         {
            for (Annotation a : qualifiers)
            {
               if (!bean.getQualifiers().contains(a))
               {
                  return false;
               }
            }
            return true;
         }
         return false;
      }

      @Override
      public int hashCode()
      {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((qualifiers == null) ? 0 : qualifiers.hashCode());
         result = prime * result + ((type == null) ? 0 : type.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj)
      {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         DefaultBeanType other = (DefaultBeanType) obj;
         if (qualifiers == null)
         {
            if (other.qualifiers != null)
               return false;
         }
         else if (!qualifiers.equals(other.qualifiers))
            return false;
         if (type == null)
         {
            if (other.type != null)
               return false;
         }
         else if (!type.equals(other.type))
            return false;
         return true;
      }

   }

   private static class ObserverMethodInfo<T>
   {

      private final Set<Annotation> observerParameterQualifier;
      private final Set<Annotation> declaringBeanQualfiers;
      private final AnnotatedMethod<T> annotatedMethod;
      private final Synthetic defaultBeanSynthetic;
      private ObserverMethod<?> delegate;
      
      public static <T> ObserverMethodInfo<T> of(Set<Annotation> observerParameterQualifier, Set<Annotation> declaringBeanQualfiers, AnnotatedMethod<T> annotatedMethod, Synthetic defaultBeanSynthetic)
      {
         return new ObserverMethodInfo<T>(observerParameterQualifier, declaringBeanQualfiers, annotatedMethod, defaultBeanSynthetic);
      }

      public ObserverMethodInfo(Set<Annotation> observerParameterQualifier, Set<Annotation> declaringBeanQualfiers, AnnotatedMethod<T> annotatedMethod, Synthetic defaultBeanSynthetic)
      {
         this.observerParameterQualifier = observerParameterQualifier;
         this.declaringBeanQualfiers = declaringBeanQualfiers;
         this.annotatedMethod = annotatedMethod;
         this.defaultBeanSynthetic = defaultBeanSynthetic;
      }

      public Set<Annotation> getObserverParameterQualifier()
      {
         return observerParameterQualifier;
      }

      public AnnotatedMethod<T> getAnnotatedMethod()
      {
         return annotatedMethod;
      }

      public ObserverMethod<?> getDelegate()
      {
         return delegate;
      }

      public void setDelegate(ObserverMethod<?> delegate)
      {
         this.delegate = delegate;
      }

      @Override
      public int hashCode()
      {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((declaringBeanQualfiers == null) ? 0 : declaringBeanQualfiers.hashCode());
         result = prime * result + ((observerParameterQualifier == null) ? 0 : observerParameterQualifier.hashCode());
         result = prime * result + ((defaultBeanSynthetic == null) ? 0 : defaultBeanSynthetic.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj)
      {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         ObserverMethodInfo<?> other = (ObserverMethodInfo<?>) obj;
         if (annotatedMethod == null)
         {
            if (other.annotatedMethod != null)
               return false;
         }
         else if (!annotatedMethod.equals(other.annotatedMethod))
            return false;
         if (declaringBeanQualfiers == null)
         {
            if (other.declaringBeanQualfiers != null)
               return false;
         }
         else if (!declaringBeanQualfiers.equals(other.declaringBeanQualfiers))
            return false;
         if (observerParameterQualifier == null)
         {
            if (other.observerParameterQualifier != null)
               return false;
         }
         else if (!observerParameterQualifier.equals(other.observerParameterQualifier))
            return false;
         if (defaultBeanSynthetic == null)
         {
            if (other.defaultBeanSynthetic != null)
               return false;
         }
         else if (!defaultBeanSynthetic.equals(other.defaultBeanSynthetic))
            return false;
         return true;
      }

   }
   
   private final class DefaultProducerMethodInfo<X>
   {
      private final AnnotatedMethod<X> producerMethod;
      private final AnnotatedMethod<X> disposerMethod;
      
      public DefaultProducerMethodInfo(AnnotatedMethod<X> producerMethod, AnnotatedMethod<X> disposerMethod)
      {
         this.producerMethod = producerMethod;
         this.disposerMethod = disposerMethod;
      }

      public AnnotatedMethod<X> getProducerMethod()
      {
         return producerMethod;
      }

      public AnnotatedMethod<X> getDisposerMethod()
      {
         return disposerMethod;
      }
   }

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
   
   private <T, X> DefaultProducerMethod<T, X> createDefaultProducerMethod(Bean<T> originalBean, Annotation qualifier, DefaultBeanType beanInfo, Set<Type> types, Set<Annotation> qualifiers, Set<Annotation> declaringBeanQualifiers, DefaultProducerMethodInfo<X> info, BeanManager beanManager)
   {
      return DefaultProducerMethod.of(originalBean, beanInfo.getType(), types, qualifiers, declaringBeanQualifiers, info.getProducerMethod(), info.getDisposerMethod(), beanManager);
   }

   private static class DefaultBeanQualifiers
   {
      private Synthetic syntheticQualifier;
      private Set<Annotation> qualifiers;

      public DefaultBeanQualifiers(Synthetic syntheticQualifier, Set<Annotation> qualifiers)
      {
         this.syntheticQualifier = syntheticQualifier;
         this.qualifiers = qualifiers;
      }

      public Synthetic getSyntheticQualifier()
      {
         return syntheticQualifier;
      }

      public Set<Annotation> getQualifiers()
      {
         return qualifiers;
      }

   }
}
