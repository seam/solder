package org.jboss.weld.extensions.bean.generic;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;

import org.jboss.weld.extensions.bean.ForwardingBean;
import org.jboss.weld.extensions.bean.InjectionPointImpl;
import org.jboss.weld.extensions.util.Reflections;
import org.jboss.weld.extensions.util.Synthetic;

// TODO Make this passivation capable
class GenericBeanImpl<T> extends ForwardingBean<T>
{

   private final Bean<T> delegate;
   private final Set<Annotation> qualifiers;
   private final InjectionTarget<T> injectionTarget;
   private final BeanManager beanManager;

   private final Map<AnnotatedField<? super T>, InjectionPoint> injectedFields;

   GenericBeanImpl(Bean<T> originalBean, Annotation genericConfiguration, InjectionTarget<T> injectionTarget, AnnotatedType<T> type, Synthetic.Provider syntheticProvider, BeanManager beanManager)
   {
      this.delegate = originalBean;
      this.qualifiers = Collections.<Annotation>singleton(syntheticProvider.get(genericConfiguration));
      this.injectionTarget = injectionTarget;
      this.beanManager = beanManager;
      this.injectedFields = new HashMap<AnnotatedField<? super T>, InjectionPoint>();
      for (AnnotatedField<? super T> field : type.getFields())
      {
         if (field.isAnnotationPresent(InjectGeneric.class))
         {
            injectedFields.put(field, new InjectionPointImpl(field, qualifiers, this, false, false));
         }
      }
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
   public T create(CreationalContext<T> creationalContext)
   {
      T instance = injectionTarget.produce(creationalContext);
      injectionTarget.inject(instance, creationalContext);
      for (Entry<AnnotatedField<? super T>, InjectionPoint> field : injectedFields.entrySet())
      {
         Object value = beanManager.getInjectableReference(field.getValue(), creationalContext);
         Reflections.setFieldValue(field.getKey().getJavaMember(), instance, value);
      }
      injectionTarget.postConstruct(instance);
      return instance;
   }
   
   @Override
   public boolean equals(Object obj)
   {
      if (obj instanceof GenericBeanImpl<?>)
      {
         GenericBeanImpl<?> that = (GenericBeanImpl<?>) obj;
         return this.getBeanClass().equals(that.getBeanClass()) && this.getQualifiers().equals(that.getQualifiers());
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
