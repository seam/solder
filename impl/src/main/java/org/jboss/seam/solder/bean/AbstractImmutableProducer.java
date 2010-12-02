package org.jboss.seam.solder.bean;

import static java.util.Collections.unmodifiableSet;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.Producer;

/**
 * A base class for implementing {@link Producer}. The attributes are immutable,
 * and collections are defensively copied on instantiation.
 * 
 * @author Pete Muir
 * 
 */
public abstract class AbstractImmutableProducer<T> implements Producer<T>
{

   private final Set<InjectionPoint> injectionPoints;

   public AbstractImmutableProducer(Set<InjectionPoint> injectionPoints)
   {
      this.injectionPoints = new HashSet<InjectionPoint>(injectionPoints);
   }

   public Set<InjectionPoint> getInjectionPoints()
   {
      return unmodifiableSet(injectionPoints);
   }

}
