package org.jboss.weld.extensions.bean;

import static org.jboss.weld.extensions.reflection.Reflections.cast;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.weld.extensions.reflection.Reflections;

/**
 * <p>
 * A narrowing bean allows you to build some general purpose bean (likely a
 * producer method), and register it for a narrowed type (or qualifiers). For
 * example, you could create a producer method which uses an a String ID to
 * located an object (the object can have any class):
 * </p>
 * 
 * <pre>
 * &#064;Produces
 * // Use some synthetic scope to prevent this from interfering with other resolutions
 * &#064;MyProducer
 * Object produce(InjectionPoint ip)
 * {
 *    String id = ip.getAnnotated().getAnnotation(Id.class).value();
 *    // Lookup and return the object for the id
 * }
 * </pre>
 * 
 * <p>
 * You can then register a narrowing bean for each type you need:
 * </p>
 * 
 * <pre>
 *    event.addBean(new NarrowingBeanBuilder&lt;T&gt;(delegateBean).readFromType(type).create());
 * </pre>
 * 
 * <p>
 * {@link NarrowingBean} will use the annotations on <code>defininingType</code>
 * to discover the qualifiers, types, scope, stereotypes of the bean, as well as
 * determine it's name (if any) and whether it is an alternative.
 * </p>
 * 
 * @author Pete Muir
 * @see NarrowingBeanBuilder
 */
public class NarrowingBean<T> implements Bean<T>
{
   
   private final Bean<Object> delegate;
   private final Set<Type> types;
   private final Set<Annotation> qualifiers;
   private final String name;
   private final Class<? extends Annotation> scope;
   private final boolean alternative;
   private final Set<Class<? extends Annotation>> stereotypes;

   NarrowingBean(Bean<Object> loggerProducerBean, Set<Type> types, Set<Annotation> qualifiers, String name, Class<? extends Annotation> scope, boolean alternative, Set<Class<? extends Annotation>> stereotypes)
   {
      this.delegate = loggerProducerBean;
      this.types = types;
      this.qualifiers = qualifiers;
      this.name = name;
      this.scope = scope;
      this.alternative = alternative;
      this.stereotypes = stereotypes;
   }

   public Set<Type> getTypes()
   {
      return types;
   }

   public Set<Annotation> getQualifiers()
   {
      return qualifiers;
   }

   public String getName()
   {
      return name;
   }

   public Class<? extends Annotation> getScope()
   {
      return scope;
   }

   public T create(CreationalContext<T> creationalContext)
   {
      return Reflections.<T>cast(delegate.create(Reflections.<CreationalContext<Object>>cast(creationalContext)));
   }

   public void destroy(T instance, CreationalContext<T> creationalContext)
   {
      delegate.destroy(instance, Reflections.<CreationalContext<Object>>cast(creationalContext));
   }

   public Set<Class<? extends Annotation>> getStereotypes()
   {
      return stereotypes;
   }

   public Class<?> getBeanClass()
   {
      return delegate.getBeanClass();
   }

   public boolean isAlternative()
   {
      return alternative;
   }

   public boolean isNullable()
   {
      return false;
   }

   public Set<InjectionPoint> getInjectionPoints()
   {
      return delegate.getInjectionPoints();
   }

}
