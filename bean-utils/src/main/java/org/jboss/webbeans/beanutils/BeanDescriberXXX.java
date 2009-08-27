/*
* JBoss, Home of Professional Open Source.
* Copyright 2006, Red Hat Middleware LLC, and individual contributors
* as indicated by the @author tags. See the copyright.txt file in the
* distribution for a full listing of individual contributors. 
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/ 
package org.jboss.webbeans.beanutils;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.ScopeType;
import javax.enterprise.inject.BindingType;
import javax.enterprise.inject.Policy;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.stereotype.Stereotype;

import org.jboss.webbeans.BeanManagerImpl;
import org.jboss.webbeans.DefinitionException;
import org.jboss.webbeans.bean.AbstractClassBean;
import org.jboss.webbeans.introspector.WBClass;
import org.jboss.webbeans.literal.AnyLiteral;
import org.jboss.webbeans.literal.CurrentLiteral;
import org.jboss.webbeans.log.LogProvider;
import org.jboss.webbeans.log.Logging;
import org.jboss.webbeans.metadata.cache.MergedStereotypes;

/**
 * 
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.1 $
 */
public class BeanDescriberXXX<T, E>
{
   // Logger
   private static final LogProvider log = Logging.getLogProvider(AbstractClassBean.class);
   
   WBClass<T> type;
   
   BeanManagerImpl beanManager;
   
   private MergedStereotypes<T, E> mergedStereotypes;
   
   private static final Annotation ANY_LITERAL = new AnyLiteral();

   private static final Annotation CURRENT_LITERAL = new CurrentLiteral();

   
   public BeanDescriberXXX(AnnotatedType<T> type, BeanManager beanManager)
   {
      if (type == null)
         throw new IllegalArgumentException("Null type");
      if (beanManager == null)
         throw new IllegalArgumentException("Null beanManager");
      if (type instanceof WBClass == false)
         throw new IllegalArgumentException("Type is not an instance of WBType");
      if (beanManager instanceof BeanManagerImpl == false)
         throw new IllegalArgumentException("BeanManaget is not an instance of BeanManagerImpl");
      
      this.type = (WBClass<T>)type;
      this.beanManager = (BeanManagerImpl)beanManager;
      
      
   }
   
   
   public Class<? extends Annotation> determineScopeType(AnnotatedType<?> annotatedType)
   {
      if (annotatedType instanceof WBClass == false)
         throw new IllegalArgumentException("Annotated type is not an instance of WBType");
      
      final WBClass<?> type = (WBClass<?>)annotatedType;
      Class<? extends Annotation> scopeType = null;
      
      for (WBClass<?> clazz = type; clazz != null; clazz = clazz.getWBSuperclass())
      {
         Set<Annotation> scopeTypes = clazz.getDeclaredMetaAnnotations(ScopeType.class);
         scopeTypes = clazz.getDeclaredMetaAnnotations(ScopeType.class);
         if (scopeTypes.size() == 1)
         {
            if (type.isAnnotationPresent(scopeTypes.iterator().next().annotationType()))
            {
               scopeType = scopeTypes.iterator().next().annotationType();
               log.trace("Scope " + scopeType + " specified by annotation");
            }
            break;
         }
         else if (scopeTypes.size() > 1)
         {
            throw new DefinitionException("At most one scope may be specified on " + type);
         }
      }

      if (scopeType == null)
      {
         scopeType = initScopeTypeFromStereotype();
      }

      if (scopeType == null)
      {
         scopeType = Dependent.class;
         log.trace("Using default @Dependent scope");
      }
      return scopeType;
   }
   
   public Set<Annotation> determineBindings(boolean includeDefaultBindings)
   {
      Set<Annotation> bindings = new HashSet<Annotation>();
      bindings.addAll(type.getMetaAnnotations(BindingType.class));
      if (includeDefaultBindings)
         initDefaultBindings(bindings);
      log.trace("Using binding types " + bindings + " specified by annotations");
      return bindings;
   }

   
   public Set<Class<? extends Annotation>> determineStereotypes()
   {
      return getMergedStereotypes().getStereotypes();
   }
   
   public boolean determinePolicy()
   {
      if (type.isAnnotationPresent(Policy.class))
      {
         return true;
      }
      else
      {
         return getMergedStereotypes().isPolicy();
      }
   }


   protected Class<? extends Annotation> initScopeTypeFromStereotype()
   {
      Class<? extends Annotation> scopeType = null;
      Set<Annotation> possibleScopeTypes = getMergedStereotypes().getPossibleScopeTypes();
      if (possibleScopeTypes.size() == 1)
      {
         scopeType = possibleScopeTypes.iterator().next().annotationType();
         if (log.isTraceEnabled())
            log.trace("Scope " + scopeType + " specified by stereotype");
         return scopeType;
      }
      else if (possibleScopeTypes.size() > 1)
      {
         throw new DefinitionException("All stereotypes must specify the same scope OR a scope must be specified on " + type);
      }
      else
      {
         return null;
      }
   }

   protected MergedStereotypes<T, E> getMergedStereotypes()
   {
      if (mergedStereotypes == null)
         mergedStereotypes = new MergedStereotypes<T, E>(type.getMetaAnnotations(Stereotype.class), beanManager);

      return mergedStereotypes;
   }
   
   protected void initDefaultBindings(Set<Annotation> bindings)
   {
      if (bindings.size() == 0)
      {
         log.trace("Adding default @Current binding type");
         bindings.add(CURRENT_LITERAL);
      }
      bindings.add(ANY_LITERAL);
   }
}
