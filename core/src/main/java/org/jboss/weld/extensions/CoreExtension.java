package org.jboss.weld.extensions;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;

import org.jboss.weld.extensions.util.BeanImpl;
import org.jboss.weld.extensions.util.reannotated.AnnotationRedefinition;
import org.jboss.weld.extensions.util.reannotated.Reannotated;
import org.jboss.weld.extensions.util.reannotated.ReannotatedMember;
import org.jboss.weld.extensions.util.reannotated.ReannotatedType;

public class CoreExtension implements Extension
{

   Collection<Bean<?>> additionalBeans = new ArrayList<Bean<?>>();

   <X> void processAnnotatedType(@Observes final ProcessAnnotatedType<X> pat, BeanManager bm)
   {
      final AnnotatedType<X> at = pat.getAnnotatedType();

      ReannotatedType<X> rt = new ReannotatedType<X>(at);

      // support for @Named packages
      Package pkg = at.getJavaClass().getPackage();
      if (pkg.isAnnotationPresent(Named.class))
      {
         final String packageName = getPackageName(pkg);
         if (at.isAnnotationPresent(Named.class))
         {
            String className = at.getJavaClass().getSimpleName();
            String beanName = getName(at, className);
            rt.define(new NamedLiteral(packageName + '.' + beanName));
         }
         rt.redefineMembers(Named.class, new AnnotationRedefinition<Named>()
         {
            
            @SuppressWarnings("unchecked")
            public Named redefine(Named annotation, Reannotated reannotated)
            {
               if (reannotated.isAnnotationPresent(Produces.class))
               {
                  String memberName = ((ReannotatedMember<? super X>) reannotated).getJavaMember().getName();
                  String beanName = getName(reannotated, memberName);
                  return new NamedLiteral(packageName + '.' + beanName);
               }
               else
               {
                  return annotation;
               }
            }

         });
      }

      // support for @Exact
      Set<Annotation> qualfiers = rt.getAnnotationsWithMetatype(Qualifier.class);
      boolean defaultQualifier = qualfiers.isEmpty() || (qualfiers.size() == 1 && qualfiers.iterator().next().annotationType() == Named.class);
      if (defaultQualifier)
      {
         rt.define(new AnnotationLiteral<Default>()
         {
         });
      }
      rt.define(new ExactLiteral(at.getJavaClass()));
      rt.redefineMembersAndParameters(Exact.class, new AnnotationRedefinition<Exact>()
      {
         
         public Exact redefine(Exact annotation, Reannotated reannotated)
         {
            if (annotation.value() == void.class)
            {
               return new ExactLiteral(reannotated.getJavaClass());
            }
            else
            {
               return annotation;
            }
         }
      });

      pat.setAnnotatedType(rt);

      // support for @Constructs
      for (AnnotatedConstructor<X> constructor : at.getConstructors())
      {
         if (constructor.isAnnotationPresent(Constructs.class))
         {
            ReannotatedType<X> rtc = new ReannotatedType<X>(at);
            // remove class-level @Named annotation
            rtc.redefine(Named.class, new AnnotationRedefinition<Named>()
            {
               
               public Named redefine(Named annotation, Reannotated reannotated)
               {
                  return null;
               }
            });
            // remove bean constructor annotated @Inject
            rtc.redefineConstructors(Inject.class, new AnnotationRedefinition<Inject>()
            {
               
               public Inject redefine(Inject annotation, Reannotated reannotated)
               {
                  return null;
               }
            });
            // make the constructor annotated @Constructs the bean constructor
            rtc.getConstructor(constructor.getJavaMember()).define(new AnnotationLiteral<Inject>()
            {
            });
            // add all the annotations of this constructor to the class
            for (Annotation ann : constructor.getAnnotations())
            {
               rtc.define(ann);
            }

            additionalBeans.add(new BeanImpl<X>(bm.createInjectionTarget(rtc), rtc));
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
      if (packageName.isEmpty())
      {
         packageName = pkg.getName();
      }
      return packageName;
   }

   private <X> String getName(Annotated annotated, String defaultName)
   {
      String beanName = annotated.getAnnotation(Named.class).value();
      if (beanName.isEmpty())
      {
         beanName = defaultName.substring(0, 1).toLowerCase() + defaultName.substring(1);
      }
      return beanName;
   }

}
