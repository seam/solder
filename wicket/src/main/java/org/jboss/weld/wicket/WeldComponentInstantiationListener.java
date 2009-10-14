package org.jboss.weld.wicket;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.apache.wicket.Component;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.jboss.weld.wicket.util.NonContextual;

/**
 * This listener uses the BeanManager to handle injections for all wicket components.
 * 
 * @author cpopetz
 * 
 */
public class WeldComponentInstantiationListener implements IComponentInstantiationListener
{
   
	@Inject
   private BeanManager manager;
	
   public void onInstantiation(Component component)
   {
      /*
       * The manager could be null in unit testing environments
       */
      if (manager != null)
      {
         // TODO Cache the NonContextual!
         new NonContextual<Component>(manager, component.getClass()).existingInstance(component).inject();
      }
   }
}