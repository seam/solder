package org.jboss.weld.extensions.bean.generic;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Producer;

import org.jboss.weld.extensions.bean.AbstractImmutableProducer;
import org.jboss.weld.extensions.reflection.Synthetic;

/**
 * Helper class which implements the {@link Producer} contract for creating generic producers
 * 
 * @author Pete Muir
 *
 */
public class GenericProducer<T> extends AbstractImmutableProducer<T>
{
   
   private final BeanManager beanManager;
   private final Synthetic qualifier;
   private final Annotation genericConfiguration;
   private final Type type;
   private final AnnotatedMember<?> originalMember;
   
   GenericProducer(Producer<T> originalProducer, Type genericBeanType, Annotation genericConfiguration, AnnotatedMember<?> originalMember, Synthetic.Provider syntheticProvider, BeanManager beanManager)
   {
      super(originalProducer.getInjectionPoints());
      this.beanManager = beanManager;
      this.genericConfiguration = genericConfiguration;
      this.qualifier = syntheticProvider.get(genericConfiguration);
      this.type = genericBeanType;
      this.originalMember = originalMember;
   }

   public void dispose(T instance)
   {
      // TODO Support disposer methods for generic producers
   }

   public T produce(CreationalContext<T> ctx)
   {
      Bean<?> underlyingBean = beanManager.resolve(beanManager.getBeans(type, qualifier));
      if (underlyingBean == null)
      {
         throw new UnsatisfiedResolutionException("Could not resolve generic bean " + type + " with generic configuration " + genericConfiguration);
      }
      
      Object object = beanManager.getReference(underlyingBean, underlyingBean.getBeanClass(), ctx);
      
      @SuppressWarnings("unchecked")
      T value = (T) object;

      // No need to check the producer return, the CDI impl will do this for us
      
      return value;
   }
   
   @Override
   public String toString()
   {
      return "Generic producer for " + genericConfiguration + " " + type.toString() + " originally declared as " + originalMember;
   }

}
