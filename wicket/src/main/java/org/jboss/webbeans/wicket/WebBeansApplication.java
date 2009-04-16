package org.jboss.webbeans.wicket;


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
 * WebBeansComponentInstantiationListener, and override the two methods below to
 * return the RequestCycle and IRequestCycleProcessor subclasses specific to
 * WebBeans, or your subclasses of those classes.
 * 
 * @author cpopetz
 * 
 * @see WebApplication
 * @see WebBeansWebRequestCycleProcessor
 * @see WebBeansRequestCycle
 */
public abstract class WebBeansApplication extends WebApplication
{
   /**
    * Use the constructor to add our component instantiation listener
    * 
    * @see WebBeansComponentInstantiationListener
    */
   public WebBeansApplication()
   {
      addComponentInstantiationListener(new WebBeansComponentInstantiationListener());
   }

   /**
    * Override to return our WebBeans-specific request cycle processor
    * 
    * @see WebBeansWebRequestCycleProcessor
    */
   @Override
   protected IRequestCycleProcessor newRequestCycleProcessor()
   {
      return new WebBeansWebRequestCycleProcessor();
   }

   /**
    * Override to return our WebBeans-specific request cycle
    * 
    * @see WebBeansRequestCycle
    */
   @Override
   public RequestCycle newRequestCycle(final Request request, final Response response)
   {
      return new WebBeansRequestCycle(this, (WebRequest) request, (WebResponse) response);
   }
}
