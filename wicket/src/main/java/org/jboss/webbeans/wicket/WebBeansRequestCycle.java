package org.jboss.webbeans.wicket;

import javax.enterprise.context.Conversation;
import javax.enterprise.inject.spi.BeanManager;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Page;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.request.target.component.BookmarkablePageRequestTarget;
import org.apache.wicket.request.target.component.IPageRequestTarget;
import org.jboss.webbeans.CurrentManager;
import org.jboss.webbeans.context.ConversationContext;
import org.jboss.webbeans.conversation.ConversationManager;
import org.jboss.webbeans.servlet.ConversationBeanStore;

/**
 * WebBeansRequestCycle is a subclass of the standard wicket WebRequestCycle
 * which:
 * <ul>
 * <li>restores long-running conversations specified in wicket page metadata
 * when a page target is first used.
 * <li>propagates long running conversations to new page targets by specifying
 * the above metadata
 * <li>propagates long running conversations across redirects through the use of
 * a request parameter if the redirect is handled with a BookmarkablePageRequest
 * <li>Sets up the conversational context
 * </ul>
 * 
 * @see WebBeansWebRequestCycleProcessor Which handles propogation of
 *      conversation data for newly-started long running conversations, by
 *      storing their ids in the page metadata
 * @author cpopetz
 * 
 */
public class WebBeansRequestCycle extends WebRequestCycle
{

   public WebBeansRequestCycle(WebApplication application, WebRequest request, Response response)
   {
      super(application, request, response);
   }

   /**
    * Override to set up the conversation context and to choose the conversation
    * if a conversation id is present in target metadata.
    */
   @Override
   protected void onRequestTargetSet(IRequestTarget target)
   {
      super.onRequestTargetSet(target);

      Page page = null;
      if (target instanceof IPageRequestTarget)
      {
         page = ((IPageRequestTarget) target).getPage();
      }

      // Two possible specifications of cid: page metadata or request url; the
      // latter is used to propagate the conversation to mounted (bookmarkable)
      // paths after a redirect

      String specifiedCid = null;
      if (page != null)
      {
         specifiedCid = (String) page.getMetaData(WebBeansMetaData.CID);
      }
      else
      {
         specifiedCid = request.getParameter("cid");
      }

      BeanManager manager = CurrentManager.rootManager();
      Conversation conversation = manager.getInstanceByType(Conversation.class);

      // restore a conversation if it exists
      if (specifiedCid != null)
      {
         // Restore this conversation
         manager.getInstanceByType(ConversationManager.class).beginOrRestoreConversation(specifiedCid);
      }

      // handle propagation of existing long running converstaions to new
      // targets
      if (conversation.isLongRunning())
      {
         // Note that we can't propagate conversations with other redirect
         // targets like RequestRedirectTarget through this mechanism, because
         // it does not provide an interface to modify its target URL. If
         // propagation with those targets is to be supported, it needs a custom
         // Response subclass.
         if (isRedirect() && target instanceof BookmarkablePageRequestTarget)
         {
            BookmarkablePageRequestTarget bookmark = (BookmarkablePageRequestTarget) target;
            // if a cid has already been specified, don't override it
            if (!bookmark.getPageParameters().containsKey("cid"))
               bookmark.getPageParameters().add("cid", conversation.getId());
         }

         // If we have a target page, propagate the conversation to the page's
         // metadata
         if (page != null)
         {
            page.setMetaData(WebBeansMetaData.CID, conversation.getId());
         }
      }

      // Now set up the conversational context if it isn't already
      if (!ConversationContext.instance().isActive())
      {
         ConversationContext.instance().setBeanStore(new ConversationBeanStore(((WebRequest) request).getHttpServletRequest().getSession(), conversation.getId()));
         ConversationContext.instance().setActive(true);
      }
   }
}