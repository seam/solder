package org.jboss.weld.extensions.genericbeans;

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
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.jboss.weld.extensions.util.AnnotationInstanceProvider;
import org.jboss.weld.extensions.util.reannotated.ReannotatedField;
import org.jboss.weld.extensions.util.reannotated.ReannotatedParameter;
import org.jboss.weld.extensions.util.reannotated.ReannotatedType;
import org.jboss.weld.extensions.beans.CustomBeanBuilder;

public class GenericExtension implements Extension
{

   AnnotationInstanceProvider annotationProvider = new AnnotationInstanceProvider();

   Map<Class<?>, Set<AnnotatedType<?>>> genericBeans = new HashMap<Class<?>, Set<AnnotatedType<?>>>();

   Map<Class<?>, Map<AnnotatedField<?>, Annotation>> producerFields = new HashMap<Class<?>, Map<AnnotatedField<?>, Annotation>>();

   /**
    * map of a generic annotation type to all instances of that type found on
    * beans
    */
   Map<Class<?>, Set<Annotation>> concreteGenerics = new HashMap<Class<?>, Set<Annotation>>();

   /**
    * Map of generic Annotation instance to a SyntheticQualifier
    */
   Map<Annotation, SyntheticQualifier> qualifierMap = new HashMap<Annotation, SyntheticQualifier>();

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
      //make note of any producer fields that produce generic beans
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
    * wraps InjectionTarget to initialise producer fields that produce generic beans
    */
   public <T> void processInjectionTarget(@Observes ProcessInjectionTarget<T> event, BeanManager beanManager)
   {
      Class javaClass = event.getAnnotatedType().getJavaClass();
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
   public void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager beanManager)
   {
      for (Entry<Class<?>, Set<AnnotatedType<?>>> i : genericBeans.entrySet())
      {
         Set<Annotation> concretes = concreteGenerics.get(i.getKey());
         if (concretes != null)
         {
            for (AnnotatedType<?> at : i.getValue())
            {
               for (Annotation conc : concretes)
               {
                  final SyntheticQualifier newQualifier = getQualifierForGeneric(conc);
                  ReannotatedType<?> rt = new ReannotatedType(at);

                  rt.define(newQualifier);
                  for (AnnotatedField<?> f : rt.getFields())
                  {
                     ReannotatedField<?> field = (ReannotatedField<?>) f;

                     if (field.isAnnotationPresent(Inject.class))
                     {
                        //if this is a configuration injection point
                        if (conc.annotationType().isAssignableFrom(field.getJavaMember().getType()))
                        {
                           field.undefine(Inject.class);
                           field.define(new AnnotationLiteral<InjectConfiguration>()
                           {
                           });
                        }
                        else
                        {
                           //check to see if we should be injecting a generic bean
                           //we do this by checking if there are any beans that can be injected into this point
                           //if there is not then we assume it is a generic injection point
                           //this has the downside that if it is actually a deployment error then it will confuse the user
                           Annotation[] qls = getQualifiers(field.getAnnotations(), beanManager);
                           Set<Bean<?>> beans = beanManager.getBeans(field.getJavaMember().getType(), qls);
                           if (beans.isEmpty())
                           {
                              field.define(newQualifier);
                           }
                        }
                     }
                     else if (field.isAnnotationPresent(Produces.class))
                     {
                        //TODO: register a producer with the appropriate qualifier
                     }
                  }
                  for (AnnotatedMethod<?> m : rt.getMethods())
                  {
                     //TODO: need to properly handle Observer methods and Disposal methods
                     if (m.isAnnotationPresent(Produces.class))
                     {
                        //TODO: we need to register the producer bean, so this is not very useful at the moment
                        for (AnnotatedParameter<?> pm : m.getParameters())
                        {
                           ReannotatedParameter<?> param = (ReannotatedParameter<?>) pm;

                           Class paramType = m.getJavaMember().getParameterTypes()[param.getPosition()];

                           //check to see if we should be injecting a generic bean
                           //we do this by checking if there are any beans that can be injected into this point
                           //if there is not then we assume it is a generic injection point
                           //this has the downside that if it is actually a deployment error then it will confuse the user
                           Annotation[] qls = getQualifiers(param.getAnnotations(), beanManager);
                           Set<Bean<?>> beans = beanManager.getBeans(paramType, qls);
                           if (beans.isEmpty())
                           {
                              param.define(newQualifier);
                           }
                        }
                     }
                  }

                  for (AnnotatedConstructor<?> m : rt.getConstructors())
                  {
                     if (m.isAnnotationPresent(Inject.class))
                     {
                        for (AnnotatedParameter<?> pm : m.getParameters())
                        {
                           ReannotatedParameter<?> param = (ReannotatedParameter<?>) pm;

                           Class paramType = m.getJavaMember().getParameterTypes()[param.getPosition()];
                           Annotation[] qls = getQualifiers(param.getAnnotations(), beanManager);
                           Set<Bean<?>> beans = beanManager.getBeans(paramType, qls);
                           if (beans.isEmpty())
                           {
                              param.define(newQualifier);
                           }
                        }
                     }
                  }
                  InjectionTarget<?> it = beanManager.createInjectionTarget(rt);

                  it = new GenericBeanInjectionTargetWrapper(rt, it, conc);
                  CustomBeanBuilder<?> builder = new CustomBeanBuilder(rt,beanManager,it);
                  event.addBean(builder.build());

               }
            }
         }
      }

   }

   public SyntheticQualifier getQualifierForGeneric(Annotation a)
   {
      if (!qualifierMap.containsKey(a))
      {
         SyntheticQualifier qualifier = (SyntheticQualifier) annotationProvider.get(SyntheticQualifier.class, (Map) Collections.singletonMap("value", count++));
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
