package org.jboss.weld.extensions.log;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

/**
 * Adds LoggerProducer to the deployment 
 * 
 * @author pmuir
 *
 */
public class LoggerExtension implements Extension
{
   
   void beforeBeanDiscovery(@Observes BeforeBeanDiscovery event, BeanManager beanManager)
   {
      event.addAnnotatedType(beanManager.createAnnotatedType(LoggerProducer.class));
   }

}
