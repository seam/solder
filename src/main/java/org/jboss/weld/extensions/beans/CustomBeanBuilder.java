package org.jboss.weld.extensions.beans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Named;

import org.jboss.weld.extensions.util.AnnotatedTypeUtils;

/**
 * class that can build a bean from an AnnotatedType.
 * 
 * @author stuart
 * 
 */
public class CustomBeanBuilder<T>
{

   final AnnotatedType<T> type;
   final BeanManager beanManager;
   InjectionTarget<T> injectionTarget;
   String name;
   Set<Annotation> qualifiers;
   Class<? extends Annotation> scope;
   Set<Class<? extends Annotation>> stereotypes;
   Set<Type> types = new HashSet<Type>();
   boolean alternative = false;
   boolean nullable = false;
   BeanLifecycle<T> beanLifecycle;
   boolean passivationCapable;
   String id;

   public CustomBeanBuilder(AnnotatedType<T> type, BeanManager beanManager)
   {
      this(type, beanManager, beanManager.createInjectionTarget(type));
   }

   public CustomBeanBuilder(AnnotatedType<T> type, BeanManager beanManager, InjectionTarget<T> injectionTarget)
   {
      this.type = type;
      this.beanManager = beanManager;
      this.injectionTarget = injectionTarget;
      qualifiers = new HashSet<Annotation>();
      stereotypes = new HashSet<Class<? extends Annotation>>();
      for (Annotation a : type.getAnnotations())
      {
         if (beanManager.isQualifier(a.annotationType()))
         {
            qualifiers.add(a);
         }
         else if (beanManager.isScope(a.annotationType()))
         {
            scope = a.annotationType();
         }
         else if (beanManager.isStereotype(a.annotationType()))
         {
            stereotypes.add(a.annotationType());
         }
         if (a instanceof Named)
         {
            Named n = (Named) a;
            name = n.value();
         }
         if (a instanceof Alternative)
         {
            alternative = true;
         }
      }
      if (scope == null)
      {
         scope = Dependent.class;
      }

      Class<?> c = type.getJavaClass();
      do
      {
         types.add(c);
         c = c.getSuperclass();
      }
      while (c != null);
      for (Class<?> i : type.getJavaClass().getInterfaces())
      {
         types.add(i);
      }
      beanLifecycle = new SimpleBeanLifecycle<T>(type.getJavaClass(), beanManager);
      id = CustomBean.class.getName() + ":" + AnnotatedTypeUtils.createTypeId(type);
   }

   public Bean<T> build()
   {
      if (!passivationCapable)
      {
         return new CustomBean<T>(type.getJavaClass(), injectionTarget, name, qualifiers, scope, stereotypes, types, alternative, nullable, beanLifecycle);
      }
      return new PassivationCapableCustomBean<T>(id, type.getJavaClass(), injectionTarget, name, qualifiers, scope, stereotypes, types, alternative, nullable, beanLifecycle);

   }

   public InjectionTarget<T> getInjectionTarget()
   {
      return injectionTarget;
   }

   public void setInjectionTarget(InjectionTarget<T> injectionTarget)
   {
      this.injectionTarget = injectionTarget;
   }
   public Set<Annotation> getQualifiers()
   {
      return qualifiers;
   }

   public void setQualifiers(Set<Annotation> qualifiers)
   {
      this.qualifiers = qualifiers;
   }

   public Class<? extends Annotation> getScope()
   {
      return scope;
   }

   public void setScope(Class<? extends Annotation> scope)
   {
      this.scope = scope;
   }

   public Set<Class<? extends Annotation>> getStereotypes()
   {
      return stereotypes;
   }

   public void setStereotypes(Set<Class<? extends Annotation>> stereotypes)
   {
      this.stereotypes = stereotypes;
   }

   public Set<Type> getTypes()
   {
      return types;
   }

   public void setTypes(Set<Type> types)
   {
      this.types = types;
   }

   public boolean isAlternative()
   {
      return alternative;
   }

   public void setAlternative(boolean alternative)
   {
      this.alternative = alternative;
   }

   public boolean isNullable()
   {
      return nullable;
   }

   public void setNullable(boolean nullable)
   {
      this.nullable = nullable;
   }

   public BeanLifecycle<T> getBeanLifecycle()
   {
      return beanLifecycle;
   }

   public void setBeanLifecycle(BeanLifecycle<T> beanLifecycle)
   {
      this.beanLifecycle = beanLifecycle;
   }

   public AnnotatedType<T> getType()
   {
      return type;
   }

   public BeanManager getBeanManager()
   {
      return beanManager;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public boolean isPassivationCapable()
   {
      return passivationCapable;
   }

   public void setPassivationCapable(boolean passivationCapable)
   {
      this.passivationCapable = passivationCapable;
   }

   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

}
