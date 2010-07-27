package org.jboss.weld.extensions.bean.generic;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.extensions.bean.InjectableMethod;
import org.jboss.weld.extensions.util.Synthetic;

// TODO Make this passivation capable
class GenericProducerMethod<T, X> extends AbstactGenericBean<T>
{

   private final InjectableMethod<X> producerMethod;
   private final InjectableMethod<X> disposerMethod;
   private final Type declaringBeanType;
   private final Annotation declaringBeanQualifier;

   GenericProducerMethod(Bean<T> originalBean, Annotation genericConfiguration, AnnotatedMethod<X> method, AnnotatedMethod<X> disposerMethod, Set<Annotation> qualifiers, Synthetic.Provider syntheticProvider, BeanManager beanManager)
   {
      super(originalBean, qualifiers, beanManager);
      this.producerMethod = new InjectableMethod<X>(method, this, beanManager);
      if (disposerMethod != null)
      {
         this.disposerMethod = new InjectableMethod<X>(disposerMethod, this, beanManager);
      }
      else
      {
         this.disposerMethod = null;
      }
      this.declaringBeanType = originalBean.getBeanClass();
      this.declaringBeanQualifier = syntheticProvider.get(genericConfiguration);
   }

   @Override
   public T create(CreationalContext<T> creationalContext)
   {
      try
      {
         return producerMethod.invoke(getReceiver(creationalContext), creationalContext);
      }
      finally
      {
         // Generic managed beans must be dependent
         creationalContext.release();
      }
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

   private Object getReceiver(CreationalContext<T> creationalContext)
   {
      Bean<?> declaringBean = getBeanManager().resolve(getBeanManager().getBeans(declaringBeanType, declaringBeanQualifier));
      return getBeanManager().getReference(declaringBean, declaringBean.getBeanClass(), creationalContext);
   }
   
}
