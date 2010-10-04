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
package org.jboss.weld.extensions.core;

import java.util.ArrayList;
import java.util.Collection;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.inject.Named;

import org.jboss.weld.extensions.literal.NamedLiteral;
import org.jboss.weld.extensions.reflection.Reflections;
import org.jboss.weld.extensions.reflection.annotated.AnnotatedTypeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension to install the "core" extensions. Core extensions are those that
 * add additional abilities to CDI applications via annotations.
 * 
 * @author Stuart
 * @author Pete Muir
 * @author Gavin King
 * 
 */
public class CoreExtension implements Extension
{

   private final Collection<Bean<?>> additionalBeans;

   static final Logger log = LoggerFactory.getLogger(CoreExtension.class);

   CoreExtension()
   {
      this.additionalBeans = new ArrayList<Bean<?>>();
   }

   <X> void processAnnotatedType(@Observes final ProcessAnnotatedType<X> pat, BeanManager beanManager)
   {
      // Support for @Veto
      if (pat.getAnnotatedType().isAnnotationPresent(Veto.class))
      {
         pat.veto();
         log.info("Preventing " + pat.getAnnotatedType().getJavaClass() + " from being installed as bean due to @Veto annotation");
         return;
      }

      // support for @Requires
      if (pat.getAnnotatedType().isAnnotationPresent(Requires.class))
      {
         String[] classes = pat.getAnnotatedType().getAnnotation(Requires.class).value();
         for (String i : classes)
         {
            try
            {
               Reflections.classForName(i, pat.getAnnotatedType().getJavaClass().getClassLoader());
            }
            catch (ClassNotFoundException e)
            {
               log.info("Preventing " + pat.getAnnotatedType().getJavaClass() + " from being installed as required class " + i + " could not be found");
               pat.veto();
            }
            catch (LinkageError e)
            {
               // LinkageError is a superclass of NoClassDefFoundError
               log.info("Preventing " + pat.getAnnotatedType().getJavaClass() + " from being installed as a linkage error occurred loading required class " + i + ". The linkage error was " + e.toString());
               pat.veto();
            }
         }
      }

      AnnotatedTypeBuilder<X> builder = null;

      // support for @Named packages
      Package pkg = pat.getAnnotatedType().getJavaClass().getPackage();
      if (pkg.isAnnotationPresent(Named.class) && !pat.getAnnotatedType().isAnnotationPresent(Named.class))
      {
         if (builder == null)
         {
            builder = new AnnotatedTypeBuilder<X>().readFromType(pat.getAnnotatedType());
         }
         builder.addToClass(new NamedLiteral(""));
      }

      // support for @Exact
      // fields
      for (AnnotatedField<? super X> f : pat.getAnnotatedType().getFields())
      {
         if (f.isAnnotationPresent(Exact.class))
         {
            Class<?> type = f.getAnnotation(Exact.class).value();
            if (builder == null)
            {
               builder = new AnnotatedTypeBuilder<X>().readFromType(pat.getAnnotatedType());
            }
            builder.overrideFieldType(f, type);
         }
      }
      // method parameters
      for (AnnotatedMethod<? super X> m : pat.getAnnotatedType().getMethods())
      {
         for (AnnotatedParameter<? super X> p : m.getParameters())
         {
            if (p.isAnnotationPresent(Exact.class))
            {
               Class<?> type = p.getAnnotation(Exact.class).value();
               if (builder == null)
               {
                  builder = new AnnotatedTypeBuilder<X>().readFromType(pat.getAnnotatedType());
               }
               builder.overrideParameterType(p, type);
            }
         }
      }
      // constructor parameters
      for (AnnotatedConstructor<X> c : pat.getAnnotatedType().getConstructors())
      {
         for (AnnotatedParameter<? super X> p : c.getParameters())
         {
            if (p.isAnnotationPresent(Exact.class))
            {
               Class<?> type = p.getAnnotation(Exact.class).value();
               if (builder == null)
               {
                  builder = new AnnotatedTypeBuilder<X>().readFromType(pat.getAnnotatedType());
               }
               builder.overrideParameterType(p, type);
            }
         }
      }
      if (builder != null)
      {
         pat.setAnnotatedType(builder.create());
      }
   }

   void afterBeanDiscovery(@Observes AfterBeanDiscovery abd)
   {
      for (Bean<?> bean : additionalBeans)
      {
         abd.addBean(bean);
      }
   }

}
