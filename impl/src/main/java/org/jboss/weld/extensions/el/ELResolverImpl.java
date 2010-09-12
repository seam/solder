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

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.weld.extensions.defaultbean.DefaultBean;

/**
 * Default EL resolver that delegates to a CompositeELResolver. This resolver
 * can only resolve weld beans
 * 
 * @author Stuart Douglas
 * 
 */
@DefaultBean(ELResolver.class)
@Resolver
public class ELResolverImpl extends ELResolver
{

   @Inject
   BeanManager beanManager;

   private CompositeELResolver delegate;

   @Inject
   private void createELResolver()
   {
      CompositeELResolver resolver = new CompositeELResolver();
      resolver.add(beanManager.getELResolver());
      resolver.add(new MapELResolver());
      resolver.add(new ListELResolver());
      resolver.add(new ArrayELResolver());
      resolver.add(new ResourceBundleELResolver());
      resolver.add(new BeanELResolver());
      delegate = resolver;
   }

   @Override
   public Class<?> getCommonPropertyType(ELContext context, Object base)
   {
      return delegate.getCommonPropertyType(context, base);
   }

   @Override
   public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base)
   {
      return delegate.getFeatureDescriptors(context, base);
   }

   @Override
   public Class<?> getType(ELContext context, Object base, Object property)
   {
      return delegate.getType(context, base, property);
   }

   @Override
   public Object getValue(ELContext context, Object base, Object property)
   {
      return delegate.getValue(context, base, property);
   }

   @Override
   public boolean isReadOnly(ELContext context, Object base, Object property)
   {
      return delegate.isReadOnly(context, base, property);
   }

   @Override
   public void setValue(ELContext context, Object base, Object property, Object value)
   {
      delegate.setValue(context, base, property, value);
   }
}
