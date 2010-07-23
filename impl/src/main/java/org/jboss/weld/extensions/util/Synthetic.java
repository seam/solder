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
package org.jboss.weld.extensions.util;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.enterprise.util.AnnotationLiteral;

/**
 * A synthetic qualifier that can be used to replace other user-supplied configuration at deployment 
 * 
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 * @author Pete Muir
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Synthetic
{
   
   long index();
   
   String namespace();
   
   public static class SyntheticLiteral extends AnnotationLiteral<Synthetic> implements Synthetic
   {
      
      private final Long index;
      
      private final String namespace;
      
      public SyntheticLiteral(String namespace, Long index)
      {
         this.namespace = namespace;
         this.index = index;
      }

      public long index()
      {
         return index;
      }
      
      public String namespace()
      {
         return namespace;
      }
      
   }
   
   public static class Provider
   {
      
      //Map of generic Annotation instance to a SyntheticQualifier
      private final Map<Annotation, Synthetic> synthetics;
      private final String namespace;
      
      private AtomicLong count;
      
      public Provider(String namespace)
      {
         this.synthetics = new HashMap<Annotation, Synthetic>();
         this.namespace = namespace;
         this.count = new AtomicLong();
      }

      public Synthetic get(Annotation annotation)
      {
         if (!synthetics.containsKey(annotation))
         {
            synthetics.put(annotation, new Synthetic.SyntheticLiteral(namespace, count.getAndIncrement()));
         }
         return synthetics.get(annotation);
      }
   }
   
}
