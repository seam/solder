package org.jboss.weld.wicket;

import javax.enterprise.context.Conversation;
import javax.inject.Inject;

import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.WebRequestCycleProcessor;
import org.jboss.weld.Container;
import org.jboss.weld.context.ContextLifecycle;
import org.jboss.weld.context.ConversationContext;
import org.jboss.weld.conversation.ConversationManager;

/**
 * WebBeansWebRequestCycleProcessor is a subclass of the standard wicket
 * WebRequestCycleProcessor which saves the conversation id of any long-running
 * cornversation in wicket page metadata. It also cleans up the conversation
 * context.
 * 
 * @author cpopetz
 * 
 */
public class WeldWebRequestCycleProcessor extends WebRequestCycleProcessor
{
   @Inject
   Conversation conversation;
   @Inject
   ConversationManager conversationManager;
   
   /**
    * If a long running conversation has been started, store its id into page
    * metadata
    */
   @Override
   public void respond(RequestCycle requestCycle)
   {
      super.respond(requestCycle);
      if (conversation.isLongRunning())
      {
         Page page = RequestCycle.get().getResponsePage();
         if (page != null)
         {
            page.setMetaData(WeldMetaData.CID, conversation.getId());
         }
      }
      
      //cleanup and deactivate the conversation context
      conversationManager.cleanupConversation();
      
      ConversationContext conversationContext = Container.instance().deploymentServices().get(
            ContextLifecycle.class).getConversationContext();
      conversationContext.setActive(false);
   }
}