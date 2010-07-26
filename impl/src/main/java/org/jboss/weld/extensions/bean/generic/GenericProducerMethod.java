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

   private final InjectableMethod<X> method;
   private final Type declaringBeanType;
   private final Annotation declaringBeanQualifier;

   GenericProducerMethod(Bean<T> originalBean, Annotation genericConfiguration, AnnotatedMethod<X> method, Set<Annotation> qualifiers, Synthetic.Provider syntheticProvider, BeanManager beanManager)
   {
      super(originalBean, qualifiers, beanManager); 
      this.method = new InjectableMethod<X>(method, this, beanManager);
      this.declaringBeanType = originalBean.getBeanClass();
      this.declaringBeanQualifier = syntheticProvider.get(genericConfiguration);
   }

   @Override
   public T create(CreationalContext<T> creationalContext)
   {
      try
      {
         Bean<?> declaringBean = getBeanManager().resolve(getBeanManager().getBeans(declaringBeanType, declaringBeanQualifier));
         Object receiver = getBeanManager().getReference(declaringBean, declaringBean.getBeanClass(), creationalContext);
         return method.invoke(receiver, creationalContext);
      }
      finally
      {
         // Generic managed beans must be dependent
         creationalContext.release();
      }
   }

}
