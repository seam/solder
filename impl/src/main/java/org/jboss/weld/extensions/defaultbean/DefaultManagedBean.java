package org.jboss.weld.extensions.defaultbean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

class DefaultManagedBean<T> extends AbstactDefaultBean<T>
{

   DefaultManagedBean(Bean<T> originalBean, Type defaultBeanType, Set<Type> types, Set<Annotation> qualifiers, BeanManager beanManager)
   {
      super(originalBean, defaultBeanType, types, qualifiers, beanManager);
   }

}
