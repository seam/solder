package org.jboss.weld.extensions.bean.generic;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.extensions.bean.Beans;

// TODO make this passivation capable?
public class GenericProducerBean<T> extends AbstactGenericBean<T>
{

   private final Set<Type> types;
   private final Type genericBeanType;
   private final Annotation genericBeanQualifier;
   private final Annotation genericConfiguration;

   public GenericProducerBean(Set<Annotation> qualifiers, Annotation genericBeanQualifier, Annotation genericConfiguration, Type genericBeanType, BeanManager beanManager, Bean<T> originalBean)
   {
      super(originalBean, qualifiers, beanManager);
      this.genericBeanType = genericBeanType;
      this.types = new HashSet<Type>();
      types.add(genericBeanType);
      types.add(Object.class);
      this.genericBeanQualifier = genericBeanQualifier;
      this.genericConfiguration = genericConfiguration;
   }

   @Override
   public Set<Type> getTypes()
   {
      return types;
   }

   @Override
   public void destroy(T instance, CreationalContext<T> creationalContext)
   {
      // TODO Implement support for disposer methods for generic producers
   }

   @Override
   public T create(CreationalContext<T> creationalContext)
   {
      Bean<?> underlyingBean = getBeanManager().resolve(getBeanManager().getBeans(genericBeanType, genericBeanQualifier));
      if (underlyingBean == null)
      {
         throw new UnsatisfiedResolutionException("Could not resolve generic bean " + genericBeanType + " with generic configuration " + genericConfiguration);
      }

      Object object = getBeanManager().getReference(underlyingBean, underlyingBean.getBeanClass(), creationalContext);

      @SuppressWarnings("unchecked")
      T value = (T) object;
      
      // Check the return value, as this is actually a producer method or field
      Beans.checkReturnValue(value, this, null, getBeanManager());

      return value;
   }

}
