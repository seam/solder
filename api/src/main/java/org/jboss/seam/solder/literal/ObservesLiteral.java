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
package org.jboss.seam.solder.literal;

import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.util.AnnotationLiteral;


public class ObservesLiteral extends AnnotationLiteral<Observes> implements Observes
{
   private static final long serialVersionUID = 338211360805208962L;
   private final TransactionPhase during;
   private final Reception notifyObserver;
   private final Reception receives;

   public ObservesLiteral(TransactionPhase during, Reception notifyObserver, Reception receives)
   {
      this.during = during;
      this.notifyObserver = notifyObserver;
      this.receives = receives;
   }

   public TransactionPhase during()
   {
      return during;
   }

   public Reception notifyObserver()
   {
      return notifyObserver;
   }

   public Reception receive()
   {
      return receives;
   }

}
