package org.jboss.weld.extensions.bean.generic;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.PassivationCapable;

import org.jboss.weld.extensions.bean.ForwardingBean;

/**
 * A helper class for implementing generic bean functionality
 * 
 * @author Pete Muir
 *
 */
abstract class AbstactGenericBean<T> extends ForwardingBean<T> implements PassivationCapable
{

   private final Bean<T> delegate;
   private final Set<Annotation> qualifiers;
   private final BeanManager beanManager;
   private final String id;
   private final boolean alternative;
   private final Class<?> beanClass;

   protected AbstactGenericBean(Bean<T> delegate, Set<Annotation> qualifiers, Annotation configuration, String id, boolean alternative, Class<?> beanClass, BeanManager beanManager)
   {
      this.delegate = delegate;
      this.beanManager = beanManager;
      this.qualifiers = new HashSet<Annotation>();
      for (Annotation qualifier : qualifiers)
      {
         // Don't add the GenericMarker qualifier, this is a pseudo qualifier,
         // used to remove declared qualifiers
         if (!qualifier.annotationType().equals(GenericMarker.class))
         {
            this.qualifiers.add(qualifier);
         }
      }
      this.id = getClass().getName() + "-" + configuration.toString() + "-" + id;
      this.alternative = alternative;
      this.beanClass = beanClass;
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

   public String getId()
   {
      return id;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (obj instanceof GenericManagedBean<?>)
      {
         GenericManagedBean<?> that = (GenericManagedBean<?>) obj;
         return this.getId().equals(that.getId());
      }
      else
      {
         return false;
      }
   }

   @Override
   public int hashCode()
   {
      return id.hashCode();
   }

   @Override
   public Class<?> getBeanClass()
   {
      return beanClass;
   }

   @Override
   public boolean isAlternative()
   {
      return alternative;
   }

}