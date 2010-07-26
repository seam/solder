package org.jboss.weld.extensions.bean.generic;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;

import org.jboss.weld.extensions.bean.InjectionPointImpl;
import org.jboss.weld.extensions.util.Reflections;
import org.jboss.weld.extensions.util.Synthetic;

// TODO Make this passivation capable
class GenericManagedBean<T> extends AbstactGenericBean<T>
{

   private final InjectionTarget<T> injectionTarget;
   private final Map<AnnotatedField<? super T>, InjectionPoint> injectedFields;

   GenericManagedBean(Bean<T> originalBean, Annotation genericConfiguration, InjectionTarget<T> injectionTarget, AnnotatedType<T> type, Synthetic.Provider syntheticProvider, Synthetic.Provider productSyntheticProvider, BeanManager beanManager)
   {
      super(originalBean, Collections.<Annotation>singleton(syntheticProvider.get(genericConfiguration)), beanManager); 
      this.injectionTarget = injectionTarget;
      this.injectedFields = new HashMap<AnnotatedField<? super T>, InjectionPoint>();
      Synthetic genericProductQualifier = productSyntheticProvider.get(genericConfiguration);
      for (AnnotatedField<? super T> field : type.getFields())
      {
         if (field.isAnnotationPresent(InjectGeneric.class) && field.isAnnotationPresent(GenericProduct.class))
         {
            injectedFields.put(field, new InjectionPointImpl(field, Collections.<Annotation>singleton(genericProductQualifier), this, false, false));
         }
         else if (field.isAnnotationPresent(InjectGeneric.class))
         {
            injectedFields.put(field, new InjectionPointImpl(field, getQualifiers(), this, false, false));
         }
      }
   }

   @Override
   public T create(CreationalContext<T> creationalContext)
   {
      T instance = injectionTarget.produce(creationalContext);
      injectionTarget.inject(instance, creationalContext);
      for (Entry<AnnotatedField<? super T>, InjectionPoint> field : injectedFields.entrySet())
      {
         Object value = getBeanManager().getInjectableReference(field.getValue(), creationalContext);
         Reflections.setFieldValue(field.getKey().getJavaMember(), instance, value);
      }
      injectionTarget.postConstruct(instance);
      return instance;
   }

}
