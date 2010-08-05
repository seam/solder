package org.jboss.weld.extensions.test.bean.generic.field;

import javax.enterprise.util.AnnotationLiteral;

public class MessageLiteral extends AnnotationLiteral<Message> implements Message
{
   
   private final String value;
   
   public MessageLiteral(String value)
   {
      this.value = value;
   }

   public String value()
   {
      return value;
   }

}
