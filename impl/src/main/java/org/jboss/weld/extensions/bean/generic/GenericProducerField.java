package org.jboss.weld.extensions.bean.generic;

import static org.jboss.weld.extensions.util.Reflections.getFieldValue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.extensions.util.Synthetic;

// TODO Make this passivation capable
class GenericProducerField<T, X> extends AbstactGenericBean<T>
{

   private final AnnotatedField<X> field;
   private final Type declaringBeanType;
   private final Annotation declaringBeanQualifier;

   GenericProducerField(Bean<T> originalBean, Annotation genericConfiguration, AnnotatedField<X> field, Set<Annotation> qualifiers, Synthetic.Provider syntheticProvider, BeanManager beanManager)
   {
      super(originalBean, qualifiers, beanManager); 
      this.field = field;
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
         return (T) getFieldValue(field.getJavaMember(), receiver, Object.class);
      }
      finally
      {
         // Generic managed beans must be dependent
         creationalContext.release();
      }
   }

}
