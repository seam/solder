package org.jboss.weld.extensions.test.bean.generic.field;

import javax.inject.Inject;

import org.jboss.weld.extensions.bean.generic.Generic;
import org.jboss.weld.extensions.bean.generic.GenericBean;

/**
 * A generic bean for the config annotation Message that uses initializer
 * injection for generic configuration annotations and generic bean injection
 * 
 * @author pmuir
 * 
 */

@Generic(Message.class)
public class Garply
{

   private Message message;
   private Qux qux;
   
   @Inject
   public void init(Message message, @GenericBean Qux qux)
   {
      this.qux = qux;
      this.message = message;
   }
   
   public Message getMessage()
   {
      return message;
   }
   
   public Qux getQux()
   {
      return qux;
   }

}
