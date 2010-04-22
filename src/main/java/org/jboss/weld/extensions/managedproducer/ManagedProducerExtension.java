package org.jboss.weld.extensions.managedproducer;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.jboss.weld.extensions.annotations.ManagedProducer;

/**
 * An extension that allows the use of @ManagedProducer methods
 * 
 * these methods work in a similar manner to @Unwrap methods in seam 2
 * 
 * @author stuart
 * 
 */
public class ManagedProducerExtension implements Extension
{

   Set<ManagedProducerBean<?>> beans = new HashSet<ManagedProducerBean<?>>();

   public void processAnnotatedType(@Observes ProcessAnnotatedType<?> type, BeanManager manager)
   {
      for (AnnotatedMethod<?> m : type.getAnnotatedType().getMethods())
      {
         if (m.isAnnotationPresent(ManagedProducer.class))
         {
            // we have a managed producer
            // lets make a not of it and register it later

            beans.add(new ManagedProducerBean(m, manager));
         }
      }
   }

   public void afterBeanDiscovery(@Observes AfterBeanDiscovery afterBean)
   {
      for (ManagedProducerBean<?> b : beans)
      {
         afterBean.addBean(b);
      }
   }

}
