package org.jboss.weld.wicket;

import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.request.IRequestCycleProcessor;

/**
 * A convenience subclass of wicket's WebApplication which adds the hooks
 * necessary to use JSR-299 injections in wicket components, as well as manage
 * JSR-299 conversation scopes with Wicket page metadata. If you have your own
 * WebApplication subclass, and can't subclass this class, you just need to do
 * the three things that this class does, i.e. register the
 * WeldComponentInstantiationListener, and override the two methods below to
 * return the RequestCycle and IRequestCycleProcessor subclasses specific to
 * Weld, or your subclasses of those classes.
 * 
 * @author cpopetz
 * 
 * @see WebApplication
 * @see WeldWebRequestCycleProcessor
 * @see WeldRequestCycle
 */
public abstract class WeldApplication extends WebApplication
{
   
   private <T> T getInstanceByType(Class<T> beanType)
   {
      BeanManager manager = BeanManagerLookup.getBeanManager();
      Bean<T> bean = (Bean<T>) ensureUniqueBean(beanType, manager.getBeans(beanType));
      return (T) manager.getReference(bean, beanType, manager.createCreationalContext(bean));
   }
   
   private static Bean<?> ensureUniqueBean(Type type, Set<Bean<?>> beans)
   {
      if (beans.size() == 0)
      {
         throw new UnsatisfiedResolutionException("Unable to resolve any Web Beans of " + type);
      }
      else if (beans.size() > 1)
      {
         throw new AmbiguousResolutionException("More than one bean available for type " + type);
      }
      return beans.iterator().next();
   }

   /**
    */
   public WeldApplication()
   {
   }

   /**
    * Add our component instantiation listener
    * 
    * @see WeldComponentInstantiationListener
    */
   protected void internalInit() 
   {
      super.internalInit();
      addComponentInstantiationListener(getInstanceByType(WeldComponentInstantiationListener.class));
   }


   /**
    * Override to return our Weld-specific request cycle processor
    * 
    * @see WeldWebRequestCycleProcessor
    */
   @Override
   protected IRequestCycleProcessor newRequestCycleProcessor()
   {
      return getInstanceByType(WeldWebRequestCycleProcessor.class);
   }

   /**
    * Override to return our Weld-specific request cycle
    * 
    * @see WeldRequestCycle
    */
   @Override
   public RequestCycle newRequestCycle(final Request request, final Response response)
   {
      return new WeldRequestCycle(this, (WebRequest) request, (WebResponse) response);
   }
}
