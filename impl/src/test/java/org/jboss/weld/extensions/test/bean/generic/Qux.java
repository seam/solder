package org.jboss.weld.extensions.test.bean.generic;

import javax.inject.Inject;

import org.jboss.weld.extensions.bean.generic.Generic;
import org.jboss.weld.extensions.bean.generic.GenericBean;

/**
 * A generic bean for the config annotation Message that uses ctor injection for
 * generic configuration annotations and generic bean injection
 * 
 * @author pmuir
 * 
 */

@Generic(Message.class)
public class Qux
{

   private final Message message;
   private final Bar bar;

   @Inject
   public Qux(Message message, @GenericBean Bar bar)
   {
      this.bar = bar;
      this.message = message;
   }

   public Message getMessage()
   {
      return message;
   }

   public Bar getBar()
   {
      return bar;
   }

}
