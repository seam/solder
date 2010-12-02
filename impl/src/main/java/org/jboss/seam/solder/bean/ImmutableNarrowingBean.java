package org.jboss.seam.solder.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import org.jboss.seam.solder.reflection.Reflections;

/**
 * <p>
 * A narrowing bean allows you to build a general purpose bean (likely a
 * producer method), and register it for a narrowed type (or qualifiers). For
 * example, you could create a producer method which uses an a String ID to
 * located an object (the object can have any class):
 * </p>
 * 
 * <pre>
 * &#064;Produces
 * // Use some synthetic scope to prevent this from interfering with other
 * // resolutions
 * &#064;MyProducer
 * Object produce(InjectionPoint ip)
 * {
 *    String id = ip.getAnnotated().getAnnotation(Id.class).value();
 *    // Lookup and return the object for the id
 * }
 * </pre>
 * 
 * <p>
 * The delegate bean <em>must</em> return an object which can be cast to the
 * type <code>T</code>, otherwise a {@link ClassCastException} will be thrown at
 * runtime when the bean is created.
 * </p>
 * 
 * <p>
 * You can then register a narrowing bean for each type you need:
 * </p>
 * 
 * <pre>
 * event.addBean(new NarrowingBeanBuilder&lt;T&gt;(delegateBean).readFromType(type).create());
 * </pre>
 * 
 * <p>
 * {@link ImmutableNarrowingBean} will use the annotations on
 * <code>defininingType</code> to discover the qualifiers, types, scope,
 * stereotypes of the bean, as well as determine it's name (if any) and whether
 * it is an alternative.
 * </p>
 * 
 * <p>
 * The attributes are immutable, and collections are defensively copied on
 * instantiation. It uses the defaults from the specification for properties if
 * not specified.
 * </p>
 * 
 * @author Pete Muir
 * @see NarrowingBeanBuilder
 * @see ImmutablePassivationCapableNarrowingBean
 */
public class ImmutableNarrowingBean<T> extends AbstractImmutableBean<T>
{

   private final Bean<Object> delegate;

   /**
    * Instantiate a new {@link ImmutableNarrowingBean}.
    * 
    * @param delegate the bean to delegate the lifecycle to
    * @param name the name of the bean
    * @param qualifiers the qualifiers of the bean
    * @param scope the scope of the bean
    * @param stereotypes the bean's stereotypes
    * @param types the types of the bean
    * @param alternative whether the bean is an alternative
    */
   public ImmutableNarrowingBean(Bean<Object> delegate, String name, Set<Annotation> qualifiers, Class<? extends Annotation> scope, Set<Class<? extends Annotation>> stereotypes, Set<Type> types, boolean alternative, boolean nullable, String toString)
   {
      super(delegate.getBeanClass(), name, qualifiers, scope, stereotypes, types, alternative, nullable, delegate.getInjectionPoints(), toString);
      this.delegate = delegate;
   }

   public T create(CreationalContext<T> creationalContext)
   {
      return Reflections.<T> cast(delegate.create(Reflections.<CreationalContext<Object>> cast(creationalContext)));
   }

   public void destroy(T instance, CreationalContext<T> creationalContext)
   {
      delegate.destroy(instance, Reflections.<CreationalContext<Object>> cast(creationalContext));
   }

}
