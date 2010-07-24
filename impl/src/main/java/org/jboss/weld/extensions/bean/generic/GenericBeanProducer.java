package org.jboss.weld.extensions.bean.generic;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Producer;

import org.jboss.weld.extensions.bean.AbstractImmutableProducer;
import org.jboss.weld.extensions.util.Synthetic;

public class GenericBeanProducer<T> extends AbstractImmutableProducer<T>
{
   
   private final BeanManager beanManager;
   private final Synthetic qualifier;
   private final Annotation genericConfigurationAnnotation;
   private final Type type;
   
   public GenericBeanProducer(Producer<T> originalProducer, Type genericBeanType, Annotation genericConfigurationAnnotation, Synthetic.Provider syntheticProvider, BeanManager beanManager)
   {
      super(originalProducer.getInjectionPoints());
      this.beanManager = beanManager;
      this.genericConfigurationAnnotation = genericConfigurationAnnotation;
      this.qualifier = syntheticProvider.get(genericConfigurationAnnotation);
      this.type = genericBeanType;
   }

   public void dispose(T instance)
   {
      // TODO implement
   }

   public T produce(CreationalContext<T> ctx)
   {
      Bean<?> underlyingBean = beanManager.resolve(beanManager.getBeans(type, qualifier));
      if (underlyingBean == null)
      {
         throw new UnsatisfiedResolutionException("Could not resolve generic bean " + type + " with generic configuration " + genericConfigurationAnnotation);
      }
      
      Object object = beanManager.getReference(underlyingBean, underlyingBean.getBeanClass(), ctx);
      
      @SuppressWarnings("unchecked")
      T value = (T) object;
      
      return value;
   }

}
