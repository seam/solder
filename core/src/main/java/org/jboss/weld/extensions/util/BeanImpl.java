package org.jboss.weld.extensions.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Scope;

/**
 * This implementation of Bean is immutable and threadsafe.
 * 
 * @author Gavin King
 * @author Pete Muir
 *
 * @param <T>
 */
public class BeanImpl<T> implements Bean<T>
{

   private final InjectionTarget<T> injectionTarget;
   private final Set<Type> types;
   private final String name;
   private final Set<Annotation> qualifiers;
   private final Class<? extends Annotation> scope;
   private final boolean alternative;
   private final Class<?> beanClass;

   public BeanImpl(InjectionTarget<T> it, ReannotatedType<T> rtc)
   {
      // create the Bean
      this.injectionTarget = it;
      // TODO: this stuff does not handle stereotypes
      Set<? extends Annotation> scopes = rtc.getAnnotationsWithMetatype(Scope.class);
      this.scope = scopes.isEmpty() ? Dependent.class : scopes.iterator().next().annotationType();
      if (rtc.isAnnotationPresent(Named.class))
      {
         this.name = rtc.getAnnotation(Named.class).value();
         // no name defaulting for constructors
         if (this.name.isEmpty())
         {
            throw new RuntimeException();
         }
      }
      else
      {
         this.name = null;
      }
      alternative = rtc.isAnnotationPresent(Alternative.class);
      qualifiers = rtc.getAnnotationsWithMetatype(Qualifier.class);
      types = rtc.getTypeClosure();
      beanClass = rtc.getJavaClass();
   }

   public BeanImpl(InjectionTarget<T> injectionTarget, Set<Type> types, Set<Annotation> qualifiers, Class<? extends Annotation> scope, String name, boolean alternative, Class<?> beanClass)
   {
      this.injectionTarget = injectionTarget;
      this.types = types;
      this.qualifiers = qualifiers;
      this.scope = scope;
      this.name = name;
      this.alternative = alternative;
      this.beanClass = beanClass;
      // TODO: stereotypes!!!
   }

   public Class<?> getBeanClass()
   {
      return beanClass;
   }

   public Set<InjectionPoint> getInjectionPoints()
   {
      return injectionTarget.getInjectionPoints();
   }

   public String getName()
   {
      return name;
   }

   public Set<Annotation> getQualifiers()
   {
      return Collections.unmodifiableSet(qualifiers);
   }

   public Class<? extends Annotation> getScope()
   {
      return scope;
   }

   public Set<Class<? extends Annotation>> getStereotypes()
   {
      return Collections.emptySet(); // TODO
   }

   public Set<Type> getTypes()
   {
      return Collections.unmodifiableSet(types);
   }

   public boolean isAlternative()
   {
      return alternative;
   }

   public boolean isNullable()
   {
      return false;
   }

   public T create(CreationalContext<T> ctx)
   {
      T instance = injectionTarget.produce(ctx);
      injectionTarget.inject(instance, ctx);
      injectionTarget.postConstruct(instance);
      return instance;
   }

   public void destroy(T instance, CreationalContext<T> creationalContext)
   {
      injectionTarget.preDestroy(instance);
      creationalContext.release();
   }

}
