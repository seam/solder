/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.weld.extensions.beanManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.jboss.weld.extensions.util.Sortable;
import org.jboss.weld.extensions.util.service.ServiceLoader;

/**
 * Super-class for non-CDI-native components that need a reference to the
 * {@link BeanManager}
 * <p>
 * <b>**WARNING**</b> This class is <b>NEVER</b> safe to use outside of specific
 * seam-faces implementation classes, and should be <b>avoided at all costs</b>.
 * If you need a handle to the {@link BeanManager} you should probably register
 * an {@link Extension} instead of using this class; have you tried using @
 * {@link Inject}?
 * 
 * @author Nicklas Karlsson
 */
public class BeanManagerAware
{
   private BeanManager beanManager;

   private final List<BeanManagerProvider> beanManagerProviders = new ArrayList<BeanManagerProvider>();

   private void loadServices()
   {
      beanManagerProviders.clear();
      Iterator<BeanManagerProvider> providers = ServiceLoader.load(BeanManagerProvider.class).iterator();
      while (providers.hasNext())
      {
         beanManagerProviders.add(providers.next());
      }
   }

   protected BeanManager getBeanManager()
   {
      if (beanManager == null)
      {
         if (beanManagerProviders.isEmpty())
         {
            loadServices();
            Collections.sort(beanManagerProviders, new Sortable.Comparator());
         }
         beanManager = lookupBeanManager();
      }
      if (beanManager == null)
      {
         throw new IllegalStateException("Could not locate a BeanManager from the providers " + providersToString());
      }
      return beanManager;
   }

   private String providersToString()
   {
      StringBuffer out = new StringBuffer();
      int i = 0;
      for (BeanManagerProvider provider : beanManagerProviders)
      {
         if (i > 0)
         {
            out.append(", ");
         }
         out.append(provider.getClass().getName());
         out.append("(");
         out.append(provider.getPrecedence());
         out.append(")");
         i++;
      }
      return out.toString();
   }

   private BeanManager lookupBeanManager()
   {
      BeanManager beanManager = null;
      for (BeanManagerProvider provider : beanManagerProviders)
      {
         beanManager = provider.getBeanManager();
         if (beanManager != null)
         {
            break;
         }
      }
      return beanManager;
   }
}
