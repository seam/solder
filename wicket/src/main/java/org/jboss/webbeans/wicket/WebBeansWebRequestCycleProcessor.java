package org.jboss.webbeans.wicket;

import javax.context.Conversation;

import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.WebRequestCycleProcessor;
import org.jboss.webbeans.CurrentManager;
import org.jboss.webbeans.context.ConversationContext;
import org.jboss.webbeans.conversation.ConversationManager;

/**
 * WebBeansWebRequestCycleProcessor is a subclass of the standard wicket
 * WebRequestCycleProcessor which saves the conversation id of any long-running
 * cornversation in wicket page metadata. It also cleans up the conversation
 * context.
 * 
 * @author cpopetz
 * 
 */
public class WebBeansWebRequestCycleProcessor extends WebRequestCycleProcessor
{
   /**
    * If a long running conversation has been started, store its id into page
    * metadata
    */
   @Override
   public void respond(RequestCycle requestCycle)
   {
      super.respond(requestCycle);
      Conversation conversation = CurrentManager.rootManager().getInstanceByType(Conversation.class);
      if (conversation.isLongRunning())
      {
         Page page = RequestCycle.get().getResponsePage();
         if (page != null)
         {
            page.setMetaData(WebBeansMetaData.CID, conversation.getId());
         }
      }
      
      //cleanup and deactivate the conversation context
      
      CurrentManager.rootManager().getInstanceByType(ConversationManager.class).cleanupConversation();
      ConversationContext.INSTANCE.setActive(false);
   }
}