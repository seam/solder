package org.jboss.weld.extensions.beanlookup;

import java.util.Map;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
/**
 * wrapper around InjectionTarget that maps the instance to it's annotated type
 * 
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 *
 * @param <T>
 */
public class IdentifiableInjectionTarget<T> implements InjectionTarget<T>
{
   InjectionTarget<T> delegate;
   
   AnnotatedType<?> type;
   
   Map<Object, AnnotatedType<?>> typeMap;
   
   IdentifiableInjectionTarget(InjectionTarget<T> delegate, AnnotatedType<?> type, Map<Object, AnnotatedType<?>> typeMap)
   {
      this.delegate = delegate;
      this.type=type;
      this.typeMap=typeMap;
   }
   
   
   public void inject(T instance, CreationalContext<T> ctx)
   {
      typeMap.put(instance, type);
      delegate.inject(instance, ctx);
   }

   public void postConstruct(T instance)
   {
      delegate.postConstruct(instance);
   }

   public void preDestroy(T instance)
   {
      delegate.preDestroy(instance);
   }

   public void dispose(T instance)
   {
      typeMap.remove(instance);
      delegate.dispose(instance);
   }

   public Set<InjectionPoint> getInjectionPoints()
   {
      return delegate.getInjectionPoints();
   }

   public T produce(CreationalContext<T> ctx)
   {
      return delegate.produce(ctx);
   }

}
