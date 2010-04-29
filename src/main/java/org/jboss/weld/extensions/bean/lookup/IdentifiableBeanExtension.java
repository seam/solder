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
package org.jboss.weld.extensions.bean.lookup;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessInjectionTarget;

/**
 * extension that allows the AnnotatedType to be retrieved for a given bean
 * 
 * This is hopefully a temporary workaround until a spec limitation is removed
 * 
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 * 
 */
public class IdentifiableBeanExtension implements Extension
{
   Map<Object, AnnotatedType<?>> types = Collections.synchronizedMap(new WeakHashMap<Object, AnnotatedType<?>>(1000));

   public <T> void processInjectionTarget(@Observes ProcessInjectionTarget<T> event)
   {
      boolean requiresId = false;
      for (Annotation a : event.getAnnotatedType().getAnnotations())
      {
         if (a.annotationType().isAnnotationPresent(RequiresIdentification.class))
         {
            requiresId = true;
            break;
         }
      }
      if (!requiresId)
      {
         for (AnnotatedMethod<? super T> m : event.getAnnotatedType().getMethods())
         {
            for (Annotation a : m.getAnnotations())
            {
               if (a.annotationType().isAnnotationPresent(RequiresIdentification.class))
               {
                  requiresId = true;
                  break;
               }
            }
         }
      }
      if (requiresId)
      {
         event.setInjectionTarget(new IdentifiableInjectionTarget<T>(event.getInjectionTarget(), event.getAnnotatedType(), types));
      }
   }

   public AnnotatedType<?> getAnnotatedType(Object instance)
   {
      return types.get(instance);
   }

}
