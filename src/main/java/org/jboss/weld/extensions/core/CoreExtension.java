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

import static org.jboss.weld.extensions.util.ReflectionUtils.getAnnotationsWithMetatype;
import static org.jboss.weld.extensions.util.ReflectionUtils.getMemberType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;

import org.jboss.weld.extensions.annotatedType.AnnotatedTypeBuilder;
import org.jboss.weld.extensions.annotatedType.AnnotationBuilder;
import org.jboss.weld.extensions.annotatedType.MemberAnnotationRedefiner;
import org.jboss.weld.extensions.annotatedType.Parameter;
import org.jboss.weld.extensions.annotatedType.ParameterAnnotationRedefiner;
import org.jboss.weld.extensions.bean.CustomBeanBuilder;
import org.jboss.weld.extensions.core.Exact.ExactLiteral;

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

      AnnotatedTypeBuilder<X> builder = AnnotatedTypeBuilder.newInstance(pat.getAnnotatedType()).readAnnotationsFromUnderlyingType();

      // support for @Named packages
      Package pkg = pat.getAnnotatedType().getJavaClass().getPackage();
      if (pkg.isAnnotationPresent(Named.class))
      {
         final String packageName = getPackageName(pkg);

         builder.redefineMembers(Named.class, new MemberAnnotationRedefiner<Named>()
         {

            public Named redefine(Named annotation, Member member, AnnotationBuilder annotations)
            {
               if (annotations.isAnnotationPresent(Produces.class))
               {
                  String memberName = member.getName();
                  String beanName = getName(annotation, memberName);
                  return new NamedLiteral(packageName + '.' + beanName);
               }
               else
               {
                  return annotation;
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
      Set<Annotation> qualfiers = getAnnotationsWithMetatype(pat.getAnnotatedType().getAnnotations(), Qualifier.class);
      if (qualfiers.isEmpty() || (qualfiers.size() == 1 && qualfiers.iterator().next().annotationType() == Named.class))
      {
         builder.addToClass(DefaultLiteral.INSTANCE);
      }
      builder.addToClass(new Exact.ExactLiteral(pat.getAnnotatedType().getJavaClass()));
      builder.redefineMembers(Exact.class, new MemberAnnotationRedefiner<Exact>()
      {

         public Exact redefine(Exact annotation, Member annotated, AnnotationBuilder annotations)
         {
            if (annotation.value() == void.class)
            {
               return new ExactLiteral(getMemberType(annotated));
            }
            else
            {
               return annotation;
            }
         }
      });

      builder.redefineMemberParameters(Exact.class, new ParameterAnnotationRedefiner<Exact>()
      {

         public Exact redefine(Exact annotation, Parameter annotated, AnnotationBuilder annotations)
         {
            if (annotation.value() == void.class)
            {
               return new ExactLiteral(getMemberType(annotated.getDeclaringMember()));
            }
            else
            {
               return annotation;
            }
         }

      });

      pat.setAnnotatedType(builder.create());

      // support for @Constructs
      for (AnnotatedConstructor<X> constructor : pat.getAnnotatedType().getConstructors())
      {
         if (constructor.isAnnotationPresent(Constructs.class))
         {
            AnnotatedTypeBuilder<X> annotatedTypeBuilder = AnnotatedTypeBuilder.newInstance(pat.getAnnotatedType()).readAnnotationsFromUnderlyingType();
            // remove class-level @Named annotation
            annotatedTypeBuilder.removeFromClass(Named.class);
            // remove bean constructors annotated @Inject
            for (AnnotatedConstructor<X> constructor2 : pat.getAnnotatedType().getConstructors())
            {
               annotatedTypeBuilder.removeFromConstructor(constructor2.getJavaMember(), Inject.class);
            }
            // make the constructor annotated @Constructs the bean constructor
            annotatedTypeBuilder.addToConstructor(constructor.getJavaMember(), InjectLiteral.INSTANCE);
            // add all the annotations of this constructor to the class
            for (Annotation ann : constructor.getAnnotations())
            {
               annotatedTypeBuilder.addToClass(ann);
            }
            AnnotatedType<X> construtsAnnotatedType = builder.create();
            additionalBeans.add(new CustomBeanBuilder<X>(construtsAnnotatedType, beanManager).build());
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
