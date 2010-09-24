package org.jboss.weld.extensions.bean.generic;

import static org.jboss.weld.extensions.reflection.Reflections.getFieldValue;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.extensions.reflection.Reflections;
import org.jboss.weld.extensions.reflection.Synthetic;

// TODO Make this passivation capable
class GenericProducerField<T, X> extends AbstractGenericProducerBean<T>
{

   private final AnnotatedField<X> field;

   GenericProducerField(Bean<T> originalBean, Annotation genericConfiguration, AnnotatedField<X> field, Set<Annotation> qualifiers, Synthetic.Provider syntheticProvider, BeanManager beanManager)
   {
      super(originalBean, genericConfiguration, qualifiers, syntheticProvider, beanManager);
      this.field = field;
   }

   @Override
   protected T getValue(Object receiver, CreationalContext<T> creationalContext)
   {
      return getFieldValue(field.getJavaMember(), receiver, Reflections.<T>getRawType(field.getBaseType()));
   }

   @Override
   public void destroy(T instance, CreationalContext<T> creationalContext)
   {
      // Generic managed beans must be dependent
      creationalContext.release();
   }

}
