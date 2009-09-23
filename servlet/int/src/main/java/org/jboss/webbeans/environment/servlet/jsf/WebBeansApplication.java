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
package org.jboss.webbeans.environment.servlet.jsf;

import javax.el.ELContextListener;
import javax.el.ExpressionFactory;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.jboss.webbeans.environment.servlet.util.Reflections;
import org.jboss.webbeans.manager.api.WebBeansManager;

/**
 * @author pmuir
 *
 */
public class WebBeansApplication extends ForwardingApplication
{
   
   private static final ELContextListener[] EMPTY_LISTENERS = {};
   
   private final Application application;
   private ExpressionFactory expressionFactory;
   
   public WebBeansApplication(Application application)
   {
      this.application = application;
      BeanManager beanManager = getBeanManager();
      if (beanManager != null)
      {
         application.addELContextListener(Reflections.<ELContextListener>newInstance("org.jboss.webbeans.el.WebBeansELContextListener"));
         application.addELResolver(beanManager.getELResolver());
      }
   }

   @Override
   protected Application delegate()
   {
      return application;
   }
   
   @Override
   public ExpressionFactory getExpressionFactory()
   {
      // Application is multi-threaded, but no need to guard against races (re-
      // creating the cached expression factory doesn't matter) or liveness
      // (the value read by all threads will be the same)
      // We have to do this lazily as Mojarra hasn't set the ExpressionFactory
      // when the object is created
      if (this.expressionFactory == null)
      {
         BeanManager beanManager = getBeanManager();
         if (beanManager != null)
         {
            this.expressionFactory = beanManager.wrapExpressionFactory(delegate().getExpressionFactory());
         }
         else
         {
            // WB failed to initialize properly
            this.expressionFactory = delegate().getExpressionFactory(); 
         }
      }
      return expressionFactory;
   }
   
   private static WebBeansManager getBeanManager()
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      if (!(facesContext.getExternalContext().getContext() instanceof ServletContext))
      {
         throw new IllegalStateException("Not in a servlet environment!");
      }
      ServletContext ctx = (ServletContext) facesContext.getExternalContext().getContext();
      if (ctx.getAttribute(BeanManager.class.getName()) == null)
      {
         throw new IllegalStateException("BeanManager has not been pushed into the ServletContext");
      }
      return (WebBeansManager) ctx.getAttribute(BeanManager.class.getName());
   }

}
