package org.jboss.weld.extensions.bean.generic;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;

import org.jboss.weld.extensions.bean.InjectionPointImpl;
import org.jboss.weld.extensions.reflection.Reflections;
import org.jboss.weld.extensions.reflection.Synthetic;

// TODO Make this passivation capable
class GenericManagedBean<T> extends AbstactGenericBean<T>
{

   private final InjectionTarget<T> injectionTarget;
   private final Map<AnnotatedField<? super T>, InjectionPoint> injectedFields;
   private final Class<? extends Annotation> scopeOverride;

   GenericManagedBean(Bean<T> originalBean, Annotation genericConfiguration, InjectionTarget<T> injectionTarget, AnnotatedType<T> type, Set<Annotation> qualifiers, Class<? extends Annotation> scopeOverride, Synthetic.Provider annotatedMemberSyntheticProvider, BeanManager beanManager)
   {
      super(originalBean, qualifiers, beanManager);
      this.injectionTarget = injectionTarget;
      this.injectedFields = new HashMap<AnnotatedField<? super T>, InjectionPoint>();
      this.scopeOverride = scopeOverride;
      for (AnnotatedField<? super T> field : type.getFields())
      {
         if (field.isAnnotationPresent(InjectGeneric.class))
         {
            if (AnnotatedMember.class.isAssignableFrom(field.getJavaMember().getType()))
            {
               injectedFields.put(field, new InjectionPointImpl(field, Collections.<Annotation> singleton(annotatedMemberSyntheticProvider.get(genericConfiguration)), this, false, false));
            }
            else
            {
               injectedFields.put(field, new InjectionPointImpl(field, getQualifiers(), this, false, false));
            }
            if (!field.getJavaMember().isAccessible())
            {
               field.getJavaMember().setAccessible(true);
            }
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
         field.getKey().getJavaMember().setAccessible(true);
         Reflections.setFieldValue(field.getKey().getJavaMember(), instance, value);
      }
      injectionTarget.postConstruct(instance);
      return instance;
   }
   
   // No need to implement destroy(), default will do

   @Override
   public Class<? extends Annotation> getScope()
   {
      if (scopeOverride != null)
      {
         return scopeOverride;
      }
      return super.getScope();
   }

}
