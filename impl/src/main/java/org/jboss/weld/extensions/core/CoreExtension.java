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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.weld.extensions.annotated.AnnotatedTypeBuilder;
import org.jboss.weld.extensions.annotated.AnnotationRedefiner;
import org.jboss.weld.extensions.annotated.RedefinitionContext;
import org.jboss.weld.extensions.bean.BeanBuilder;

/**
 * Extension to install the "core" extensions. Core extensions are those that
 * add additional abilities to CDI applications via annotations.
 * 
 * @author Stuart
 * @author Pete Muir
 * @author Gavin King
 * 
 */
class CoreExtension implements Extension
{

   private final Collection<Bean<?>> additionalBeans;

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
         return;
      }

      AnnotatedTypeBuilder<X> builder = null;

      // support for @Named packages
      Package pkg = pat.getAnnotatedType().getJavaClass().getPackage();
      if (pkg.isAnnotationPresent(Named.class))
      {
         final String packageName = getPackageName(pkg);
         if (builder == null)
         {
            builder = new AnnotatedTypeBuilder<X>().readFromType(pat.getAnnotatedType());
         }
         builder.redefine(Named.class, new AnnotationRedefiner<Named>()
         {
            
            public void redefine(RedefinitionContext<Named> ctx)
            {
               if (ctx.getAnnotatedElement().isAnnotationPresent(Produces.class))
               {
                  String memberName = ctx.getElementName();
                  String beanName = getName(ctx.getAnnotatedElement().getAnnotation(Named.class), memberName);
                  ctx.getAnnotationBuilder().add(new NamedLiteral(packageName + '.' + beanName));
               }
            }

         });

         if (pat.getAnnotatedType().isAnnotationPresent(Named.class))
         {
            String className = pat.getAnnotatedType().getJavaClass().getSimpleName();
            String beanName = getName(pat.getAnnotatedType().getAnnotation(Named.class), className);
            builder.addToClass(new NamedLiteral(packageName + '.' + beanName));
         }

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

      // support for @Constructs
      for (AnnotatedConstructor<X> constructor : pat.getAnnotatedType().getConstructors())
      {
         if (constructor.isAnnotationPresent(Constructs.class))
         {
            AnnotatedTypeBuilder<X> annotatedTypeBuilder = new AnnotatedTypeBuilder<X>().readFromType(pat.getAnnotatedType());
            // remove class-level @Named annotation
            annotatedTypeBuilder.removeFromClass(Named.class);
            // remove bean constructors annotated @Inject
            for (AnnotatedConstructor<X> constructor2 : pat.getAnnotatedType().getConstructors())
            {
               annotatedTypeBuilder.removeFromConstructor(constructor2, Inject.class);
            }
            // make the constructor annotated @Constructs the bean constructor
            annotatedTypeBuilder.addToConstructor(constructor, InjectLiteral.INSTANCE);
            // add all the annotations of this constructor to the class
            for (Annotation ann : constructor.getAnnotations())
            {
               annotatedTypeBuilder.addToClass(ann);
            }
            AnnotatedType<X> construtsAnnotatedType = builder.create();
            additionalBeans.add(new BeanBuilder<X>(beanManager).defineBeanFromAnnotatedType(construtsAnnotatedType).create());
         }
      }
   }

   void afterBeanDiscovery(@Observes AfterBeanDiscovery abd)
   {
      for (Bean<?> bean : additionalBeans)
      {
         abd.addBean(bean);
      }
   }

   private String getPackageName(Package pkg)
   {
      String packageName = pkg.getAnnotation(Named.class).value();
      if (packageName.length() == 0)
      {
         packageName = pkg.getName();
      }
      return packageName;
   }

   private <X> String getName(Named named, String defaultName)
   {
      String beanName = named.value();
      if (beanName.length() == 0)
      {
         beanName = defaultName.substring(0, 1).toLowerCase() + defaultName.substring(1);
      }
      return beanName;
   }

}
