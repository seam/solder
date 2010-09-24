package org.jboss.weld.extensions.bean;

import static org.jboss.weld.extensions.util.Arrays2.asSet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Named;

import org.jboss.weld.extensions.literal.AnyLiteral;
import org.jboss.weld.extensions.literal.DefaultLiteral;

/**
 * Builder for {@link NarrowingBean}
 * 
 * @author Pete Muir
 * @see NarrowingBean
 *
 */
public class NarrowingBeanBuilder<T>
{
   
   private final Bean<Object> delegate;
   private Set<Type> types;
   private Set<Annotation> qualifiers;
   private String name;
   private Class<? extends Annotation> scope;
   private boolean alternative;
   private Set<Class<? extends Annotation>> stereotypes;

   public static <T> NarrowingBeanBuilder<T> of(Bean<Object> delegate)
   {
      return new NarrowingBeanBuilder<T>(delegate);
   }

   public NarrowingBeanBuilder(Bean<Object> delegate)
   {
      this.delegate = delegate;
   }
   
   public NarrowingBeanBuilder<T> readFromType(AnnotatedType<T> type, BeanManager beanManager)
   {
      this.types = new HashSet<Type>(type.getTypeClosure());
      this.qualifiers = new HashSet<Annotation>();
      this.stereotypes = new HashSet<Class<? extends Annotation>>();
      String name = null;
      Class<? extends Annotation> scope = Dependent.class;
      for (Annotation annotation : type.getAnnotations())
      {
         if (beanManager.isQualifier(annotation.annotationType()))
         {
            this.qualifiers.add(annotation);
         }
         else if (annotation.annotationType().equals(Named.class))
         {
            name = Named.class.cast(annotation).value();
         }
         else if (beanManager.isScope(annotation.annotationType()))
         {
            scope = annotation.annotationType();
         }
         else if (beanManager.isStereotype(annotation.annotationType()))
         {
            this.stereotypes.add(annotation.annotationType());
         }
      }
      if (qualifiers.isEmpty())
      {
         this.qualifiers.add(DefaultLiteral.INSTANCE);
      }
      this.qualifiers.add(AnyLiteral.INSTANCE);
      this.name = "".equals(name) ? null : name;
      this.scope = scope;
      this.alternative = type.isAnnotationPresent(Alternative.class);
      return this;
   }
   
   public Set<Type> getTypes()
   {
      return types;
   }

   public NarrowingBeanBuilder<T> types(Set<Type> types)
   {
      this.types = types;
      return this;
   }
   
   public NarrowingBeanBuilder<T> types(Type... types)
   {
      this.types = asSet(types);
      return this;
   }
   
   public NarrowingBeanBuilder<T> addType(Type type)
   {
      this.types.add(type);
      return this;
   }
   
   public NarrowingBeanBuilder<T> addTypes(Type... types)
   {
      this.types.addAll(asSet(types));
      return this;
   }
   
   public NarrowingBeanBuilder<T> addTypes(Collection<Type> types)
   {
      this.types.addAll(types);
      return this;
   }

   public Set<Annotation> getQualifiers()
   {
      return qualifiers;
   }

   public NarrowingBeanBuilder<T> qualifiers(Set<Annotation> qualifiers)
   {
      this.qualifiers = qualifiers;
      return this;
   }
   
   public NarrowingBeanBuilder<T> qualifiers(Annotation... qualifiers)
   {
      this.qualifiers = asSet(qualifiers);
      return this;
   }
   
   public NarrowingBeanBuilder<T> addQualifier(Annotation qualifier)
   {
      this.qualifiers.add(qualifier);
      return this;
   }
   
   public NarrowingBeanBuilder<T> addQualifiers(Annotation... qualifiers)
   {
      this.qualifiers.addAll(asSet(qualifiers));
      return this;
   }
   
   public NarrowingBeanBuilder<T> addQualifiers(Collection<Annotation> qualifiers)
   {
      this.qualifiers.addAll(qualifiers);
      return this;
   }

   public String getName()
   {
      return name;
   }

   public NarrowingBeanBuilder<T> name(String name)
   {
      this.name = name;
      return this;
   }

   public Class<? extends Annotation> getScope()
   {
      return scope;
   }

   public NarrowingBeanBuilder<T> scope(Class<? extends Annotation> scope)
   {
      this.scope = scope;
      return this;
   }

   public boolean isAlternative()
   {
      return alternative;
   }

   public NarrowingBeanBuilder<T> alternative(boolean alternative)
   {
      this.alternative = alternative;
      return this;
   }

   public Set<Class<? extends Annotation>> getStereotypes()
   {
      return stereotypes;
   }

   public NarrowingBeanBuilder<T> stereotypes(Set<Class<? extends Annotation>> stereotypes)
   {
      this.stereotypes = stereotypes;
      return this;
   }

   public NarrowingBean<T> create()
   {
      return new NarrowingBean<T>(delegate, types, qualifiers, name, scope, alternative, stereotypes);
   }
}
