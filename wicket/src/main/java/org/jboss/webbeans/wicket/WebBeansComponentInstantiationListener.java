package org.jboss.webbeans.wicket;

import org.apache.wicket.Component;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.jboss.webbeans.CurrentManager;
import org.jboss.webbeans.manager.api.WebBeansManager;

/**
 * This listener uses the WebBeansManager to handle injections for all wicket
 * components.
 * 
 * @author cpopetz
 * @see WebBeansManager
 * 
 */
public class WebBeansComponentInstantiationListener implements IComponentInstantiationListener
{
   public void onInstantiation(Component component)
   {
      ((WebBeansManager) CurrentManager.rootManager()).injectNonContextualInstance(component);
   }
}