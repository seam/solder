package org.jboss.weld.extensions.bean.generic;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.extensions.bean.InjectableMethod;
import org.jboss.weld.extensions.util.Synthetic;

// TODO Make this passivation capable
public class GenericProducerMethod<T, X> extends AbstractGenericProducerBean<T>
{

   private final InjectableMethod<X> producerMethod;
   private final InjectableMethod<X> disposerMethod;
   
   GenericProducerMethod(Bean<T> originalBean, Annotation genericConfiguration, AnnotatedMethod<X> method, AnnotatedMethod<X> disposerMethod, Set<Annotation> qualifiers, Synthetic.Provider syntheticProvider, BeanManager beanManager)
   {
      super(originalBean, genericConfiguration, qualifiers, syntheticProvider, beanManager);
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
            // Generic managed beans must be dependent
            creationalContext.release();
         }
      }
   }
   
}
