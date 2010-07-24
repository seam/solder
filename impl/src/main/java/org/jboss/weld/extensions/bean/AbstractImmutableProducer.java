package org.jboss.weld.extensions.bean;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.Producer;

/**
 * A base class for implementing {@link Producer}. The base class is immutable,
 * and uses defensive copying for collections
 * 
 * @author Pete Muir
 * 
 */
public abstract class AbstractImmutableProducer<T> implements Producer<T>
{

   private final Set<InjectionPoint> injectionPoints;

   public AbstractImmutableProducer(Set<InjectionPoint> injectionPoints)
   {
      this.injectionPoints = injectionPoints;
   }

   public Set<InjectionPoint> getInjectionPoints()
   {
      return new HashSet<InjectionPoint>(injectionPoints);
   }

}
