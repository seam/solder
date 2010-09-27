package org.jboss.weld.extensions.bean.generic;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.weld.extensions.bean.InjectableMethod;
import org.jboss.weld.extensions.bean.InjectionPointImpl;

// TODO Make this passivation capable
public class GenericProducerMethod<T, X> extends AbstractGenericProducerBean<T>
{

   private final InjectableMethod<X> producerMethod;
   private final InjectableMethod<X> disposerMethod;

   GenericProducerMethod(Bean<T> originalBean, Annotation genericConfiguration, AnnotatedMethod<X> method, AnnotatedMethod<X> disposerMethod, final Set<Annotation> qualifiers, final Set<Annotation> genericBeanQualifiers, Class<? extends Annotation> scopeOverride, BeanManager beanManager)
   {
      super(originalBean, genericConfiguration, qualifiers, genericBeanQualifiers, scopeOverride, beanManager);
      this.producerMethod = new InjectableMethod<X>(method, this, beanManager)
      {
         protected InjectionPoint wrapParameterInjectionPoint(InjectionPoint injectionPoint)
         {
            return wrapInjectionPoint(injectionPoint, genericBeanQualifiers);
         };
      };
      if (disposerMethod != null)
      {
         this.disposerMethod = new InjectableMethod<X>(disposerMethod, this, beanManager)
         {
            protected InjectionPoint wrapParameterInjectionPoint(InjectionPoint injectionPoint)
            {
               return wrapInjectionPoint(injectionPoint, genericBeanQualifiers);
            };
         };
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
      // creationalContext.release();
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
         return new InjectionPointImpl((AnnotatedParameter<?>) anotated, newQualifiers, injectionPoint.getBean(), injectionPoint.isTransient(), injectionPoint.isDelegate());
      }
      return injectionPoint;
   }

}
