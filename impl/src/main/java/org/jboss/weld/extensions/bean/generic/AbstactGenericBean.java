package org.jboss.weld.extensions.bean.generic;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.extensions.bean.ForwardingBean;

public abstract class AbstactGenericBean<T> extends ForwardingBean<T>
{

   private final Bean<T> delegate;
   private final Set<Annotation> qualifiers;
   private final BeanManager beanManager;

   public AbstactGenericBean(Bean<T> delegate, Set<Annotation> qualifiers, BeanManager beanManager)
   {
      this.delegate = delegate;
      this.beanManager = beanManager;
      this.qualifiers = qualifiers;
   }
   
   protected BeanManager getBeanManager()
   {
      return beanManager;
   }

   @Override
   protected Bean<T> delegate()
   {
      return delegate;
   }

   @Override
   public Set<Annotation> getQualifiers()
   {
      return qualifiers;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (obj instanceof GenericManagedBean<?>)
      {
         GenericManagedBean<?> that = (GenericManagedBean<?>) obj;
         return this.delegate().equals(that.delegate()) && this.getBeanClass().equals(that.getBeanClass()) && this.getQualifiers().equals(that.getQualifiers());
      }
      else
      {
         return false;
      }
   }

   @Override
   public int hashCode()
   {
      int hash = 2;
      hash = 31 * hash + this.getTypes().hashCode();
      hash = 31 * hash + this.getQualifiers().hashCode();
      return hash;
   }

}