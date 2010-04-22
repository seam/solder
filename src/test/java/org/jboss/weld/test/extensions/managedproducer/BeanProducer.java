package org.jboss.weld.test.extensions.managedproducer;

import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.weld.extensions.annotations.ManagedProducer;

public class BeanProducer
{
   @ManagedProducer
   public ProducedInterface produce(InjectionPoint injectionPoint)
   {
      ProducedBean b = new ProducedBean();
      b.setValue(injectionPoint.getAnnotated().getAnnotation(MPType.class).value());
      return b;
   }
}
