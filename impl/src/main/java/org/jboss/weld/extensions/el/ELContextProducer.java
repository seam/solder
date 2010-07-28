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
package org.jboss.weld.extensions.el;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

public class ELContextProducer
{

   @Inject
   @Mapper
   Instance<FunctionMapper> functionMapper;

   @Inject
   @Mapper
   Instance<VariableMapper> variableMapper;

   @Inject
   @Resolver
   ELResolver resolver;

   @Produces
   public ELContext createELContext(BeanManager beanManager)
   {
      return createELContext(resolver, functionMapper.get(), variableMapper.get());
   }

   private ELContext createELContext(final ELResolver resolver, final FunctionMapper functionMapper, final VariableMapper variableMapper)
   {
      return new ELContext()
      {
         @Override
         public ELResolver getELResolver()
         {
            return resolver;
         }

         @Override
         public FunctionMapper getFunctionMapper()
         {
            return functionMapper;
         }

         @Override
         public VariableMapper getVariableMapper()
         {
            return variableMapper;
         }
      };
   }
}