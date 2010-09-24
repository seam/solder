package org.jboss.weld.extensions.bean.generic;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.extensions.bean.Beans;
import org.jboss.weld.extensions.reflection.Synthetic;

/**
 * A helper class for implementing producer methods and fields on generic beans
 * 
 * @author Pete Muir
 *
 */
abstract class AbstractGenericProducerBean<T> extends AbstactGenericBean<T>
{

   private final Type declaringBeanType;
   private final Annotation declaringBeanQualifier;

   protected AbstractGenericProducerBean(Bean<T> delegate, Annotation genericConfiguration, Set<Annotation> qualifiers, Synthetic.Provider syntheticProvider, BeanManager beanManager)
   {
      super(delegate, qualifiers, beanManager);
      this.declaringBeanType = delegate.getBeanClass();
      this.declaringBeanQualifier = syntheticProvider.get(genericConfiguration);
   }
   
   protected Annotation getDeclaringBeanQualifier()
   {
      return declaringBeanQualifier;
   }
   
   protected Type getDeclaringBeanType()
   {
      return declaringBeanType;
   }
   
   protected abstract T getValue(Object receiver, CreationalContext<T> creationalContext);

   @Override
   public T create(CreationalContext<T> creationalContext)
   {
      try
      {
         Object receiver = getReceiver(creationalContext);
         T instance = getValue(receiver, creationalContext);
         Beans.checkReturnValue(instance, this, null, getBeanManager());
         return instance;
      }
      finally
      {
         // Generic managed beans must be dependent
         creationalContext.release();
      }
   }
   
   protected Object getReceiver(CreationalContext<T> creationalContext)
   {
      Bean<?> declaringBean = getBeanManager().resolve(getBeanManager().getBeans(getDeclaringBeanType(), getDeclaringBeanQualifier()));
      return getBeanManager().getReference(declaringBean, declaringBean.getBeanClass(), creationalContext);
   }

}