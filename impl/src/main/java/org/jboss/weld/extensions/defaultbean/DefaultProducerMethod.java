package org.jboss.weld.extensions.defaultbean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.extensions.bean.InjectableMethod;

// TODO Make this passivation capable
public class DefaultProducerMethod<T, X> extends AbstractDefaultProducerBean<T>
{

   private final InjectableMethod<X> producerMethod;
   private final InjectableMethod<X> disposerMethod;
   
   DefaultProducerMethod(Bean<T> originalBean, Type declaringBeanType, Set<Type> beanTypes, Set<Annotation> qualifiers, Set<Annotation> declaringBeanQualifiers, AnnotatedMethod<X> method, AnnotatedMethod<X> disposerMethod, BeanManager beanManager)
   {
      super(originalBean, declaringBeanType, beanTypes, qualifiers, declaringBeanQualifiers, beanManager);
      this.producerMethod = new InjectableMethod<X>(method, this, beanManager);
      if (disposerMethod != null)
      {
         this.disposerMethod = new InjectableMethod<X>(disposerMethod, this, beanManager);
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
         try
         {
            disposerMethod.invoke(getReceiver(creationalContext), creationalContext);
         }
         finally
         {
            creationalContext.release();
         }
      }
   }
   
}
