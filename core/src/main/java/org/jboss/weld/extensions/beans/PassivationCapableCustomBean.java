package org.jboss.weld.extensions.beans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.PassivationCapable;

public class PassivationCapableCustomBean<T> extends CustomBean<T> implements PassivationCapable
{
   final String id;

   PassivationCapableCustomBean(String id, Class<?> beanClass, InjectionTarget<T> injectionTarget, String name, Set<Annotation> qualifiers, Class<? extends Annotation> scope, Set<Class<? extends Annotation>> stereotypes, Set<Type> types, boolean alternative, boolean nullable, BeanLifecycle<T> beanLifecycle)
   {
      super(beanClass, injectionTarget, name, qualifiers, scope, stereotypes, types, alternative, nullable, beanLifecycle);
      this.id = id;
   }

   public String getId()
   {
      return id;
   }

}
