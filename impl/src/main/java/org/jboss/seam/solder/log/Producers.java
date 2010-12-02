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

package org.jboss.seam.solder.log;

import static org.jboss.logging.Logger.getLogger;
import static org.jboss.logging.Logger.getMessageLogger;
import static org.jboss.logging.Messages.getBundle;
import static org.jboss.seam.solder.reflection.Reflections.getRawType;
import static org.jboss.seam.solder.util.Locales.toLocale;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.logging.Logger;

/**
 * The <code>Producers</code> provides a producer method for all
 * injected loggers and injected typed loggers.
 * 
 * @author David Allen
 * @author Pete Muir
 */
class Producers
{
   
   @Produces
   Logger produceLog(InjectionPoint injectionPoint)
   {
      Annotated annotated = injectionPoint.getAnnotated();
      if (annotated.isAnnotationPresent(Category.class) && annotated.isAnnotationPresent(Suffix.class))
      {
         return getLogger(annotated.getAnnotation(Category.class).value(), annotated.getAnnotation(Suffix.class).value());
      }
      else if (annotated.isAnnotationPresent(Category.class))
      {
         return getLogger(annotated.getAnnotation(Category.class).value());
      }
      else if (annotated.isAnnotationPresent(Suffix.class))
      {
         return getLogger(getRawType(injectionPoint.getType()), annotated.getAnnotation(Suffix.class).value());
      }
      else
      {
         return getLogger(getRawType(injectionPoint.getType()));
      }
   }
   
   @Produces
   @TypedLogger
   Object produceTypedLogger(InjectionPoint injectionPoint)
   {
      Annotated annotated = injectionPoint.getAnnotated();
      if (!annotated.isAnnotationPresent(Category.class))
      {
         throw new IllegalStateException("Must specify @Category for typed loggers at [" + injectionPoint + "]");
      }
      else if (annotated.isAnnotationPresent(Locale.class))
      {         
         return getMessageLogger(getRawType(injectionPoint.getType()), annotated.getAnnotation(Category.class).value(), toLocale(annotated.getAnnotation(Locale.class).value()));
      }
      else
      {
         return getMessageLogger(getRawType(injectionPoint.getType()), annotated.getAnnotation(Category.class).value());
      }
   }
   
   @Produces
   @TypedMessageBundle
   Object produceTypedMessageBundle(InjectionPoint injectionPoint)
   {
      Annotated annotated = injectionPoint.getAnnotated();
      if (annotated.isAnnotationPresent(Locale.class))
      {         
         return getBundle(getRawType(injectionPoint.getType()), toLocale(annotated.getAnnotation(Locale.class).value()));
      }
      else
      {
         return getBundle(getRawType(injectionPoint.getType()));
      }
   }
   
}
