package org.jboss.webbeans.wicket;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.apache.wicket.Component;
import org.apache.wicket.application.IComponentInstantiationListener;

/**
 * This listener uses the BeanManager to handle injections for all wicket components.
 * 
 * @author cpopetz
 * 
 */
public class WebBeansComponentInstantiationListener implements IComponentInstantiationListener
{
	@Inject
   BeanManager manager;
	
   public void onInstantiation(Component component)
   {
      /*
       * The manager could be null in unit testing environments
       */
      if (manager != null)
      {
         manager.createInjectionTarget(manager.createAnnotatedType((Class) component.getClass()))
            .inject(component, manager.createCreationalContext(null));
      }
   }
}