/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.weld.environment.servlet.jsf;

import javax.el.ELContextListener;
import javax.el.ExpressionFactory;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.jboss.weld.environment.servlet.util.Reflections;

/**
 * @author pmuir
 *
 */
public class WeldApplication extends ForwardingApplication
{
   
   private static final ELContextListener[] EMPTY_LISTENERS = {};
   
   private final Application application;
   private ExpressionFactory expressionFactory;
   private BeanManager beanManager;
   
   public WeldApplication(Application application)
   {
      this.application = application;
   }
   
   private void init()
   {
      if (expressionFactory == null && application.getExpressionFactory() != null && beanManager() != null)
      {
         application.addELContextListener(Reflections.<ELContextListener>newInstance("org.jboss.weld.el.WeldELContextListener"));
         application.addELResolver(beanManager().getELResolver());
         this.expressionFactory = beanManager().wrapExpressionFactory(application.getExpressionFactory());
      }
   }

   @Override
   protected Application delegate()
   {
      init();
      return application;
   }
   
   @Override
   public ExpressionFactory getExpressionFactory()
   {
      init();
      if (expressionFactory == null)
      {
         return application.getExpressionFactory();
      }
      else
      {
         return expressionFactory;
      }
   }
   
   private BeanManager beanManager()
   {
      if (beanManager == null)
      {
         FacesContext facesContext = FacesContext.getCurrentInstance();
         if (!(facesContext.getExternalContext().getContext() instanceof ServletContext))
         {
            throw new IllegalStateException("Not in a servlet environment!");
         }
         ServletContext ctx = (ServletContext) facesContext.getExternalContext().getContext();
         if (ctx.getAttribute(BeanManager.class.getName()) == null)
         {
            return null;
         }
         this.beanManager = (BeanManager) ctx.getAttribute(BeanManager.class.getName());
      }
      return beanManager;
      
   }

}
