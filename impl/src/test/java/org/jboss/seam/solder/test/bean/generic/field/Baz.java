/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.solder.test.bean.generic.field;

import java.io.Serializable;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.seam.solder.bean.generic.ApplyScope;
import org.jboss.seam.solder.bean.generic.Generic;
import org.jboss.seam.solder.bean.generic.GenericConfiguration;
import org.jboss.seam.solder.unwraps.Unwraps;

/**
 * A generic bean for the config annotation Message
 * 
 * @author pmuir
 *
 */

@GenericConfiguration(Message.class)
@ApplyScope
public class Baz implements Serializable
{

   private static final long serialVersionUID = 6807449196645110050L;

   private Fred fred = new Fred("Hello Fred");

   @Inject @Generic
   private Bar bar;

   @Inject
   private Corge corge;
   
   @Inject 
   @Generic
   private Message message;

   public Bar getBar()
   {
      return bar;
   }
   
   public Corge getCorge()
   {
      return corge;
   }
   
   public Message getMessage()
   {
      return message;
   }

   @Produces @Wibble
   public String getCorge(Wobble wobble)
   {
      return wobble.getName() + message.value();
   }
   
   @Unwraps
   public Fred getFred()
   {
      return fred;
   }

   public void observe(@Observes @Any Plugh event)
   {
      // Set the message if we are in a generic bean
      if (message != null)
      {
         event.setMessage(message);
      }
      // Set the message if not previously
      else if (event.getMessage() == null)
      {
         event.setMessage(new MessageLiteral("base"));
      }
   }

   public void setFred(Fred fred)
   {
      this.fred = fred;
   }
}
