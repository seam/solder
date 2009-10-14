package org.jboss.weld.wicket;

import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.request.IRequestCycleProcessor;
import org.jboss.weld.wicket.util.NonContextual;

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
 * @author pmuir
 * 
 * @see WebApplication
 * @see WeldWebRequestCycleProcessor
 * @see WeldRequestCycle
 */
public abstract class WeldApplication extends WebApplication
{
   
   private final NonContextual<WeldComponentInstantiationListener> weldComponentInstantiationListener;
   private final NonContextual<WeldWebRequestCycleProcessor> weldWebRequestCycleProcessor;

   /**
    */
   public WeldApplication()
   {
      this.weldComponentInstantiationListener = new NonContextual<WeldComponentInstantiationListener>(BeanManagerLookup.getBeanManager(), WeldComponentInstantiationListener.class);
      this.weldWebRequestCycleProcessor = new NonContextual<WeldWebRequestCycleProcessor>(BeanManagerLookup.getBeanManager(), WeldWebRequestCycleProcessor.class);
   }

   /**
    * Add our component instantiation listener
    * 
    * @see WeldComponentInstantiationListener
    */
   @Override
   protected void internalInit() 
   {
      super.internalInit();
      addComponentInstantiationListener(weldComponentInstantiationListener.newInstance().produce().inject().get());
   }


   /**
    * Override to return our Weld-specific request cycle processor
    * 
    * @see WeldWebRequestCycleProcessor
    */
   @Override
   protected IRequestCycleProcessor newRequestCycleProcessor()
   {
      return weldWebRequestCycleProcessor.newInstance().produce().inject().get();
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
