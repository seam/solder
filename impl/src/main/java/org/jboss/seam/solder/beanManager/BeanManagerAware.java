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
package org.jboss.seam.solder.beanManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.jboss.seam.solder.util.Sortable;
import org.jboss.seam.solder.util.service.ServiceLoader;

/**
 * <p>
 * Super-class for objects not able to obtain CDI injection that need a
 * reference to the {@link BeanManager}. {@link BeanManagerProvider}s can be
 * registered to allow third parties to register custom methods of looking up
 * the BeanManager.
 * </p>
 * 
 * <p>
 * <b>**WARNING**</b> This class is <b>NOT</b> a clever way to get the
 * BeanManager, and should be <b>avoided at all costs</b>. If you need a handle
 * to the {@link BeanManager} you should probably register an {@link Extension}
 * instead of using this class; have you tried using @{@link Inject}?
 * </p>
 * 
 * <p>
 * If you think you need to use this class, chat to the community and make sure
 * you aren't missing an trick!
 * </p>
 * 
 * @see BeanManagerProvider
 * @see BeanManagerAccessor
 * 
 * @author Pete Muir
 * @author Nicklas Karlsson
 */
public class BeanManagerAware
{

   // This might be a managed bean, so try for injection
   @Inject
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

   protected BeanManagerAware()
   {
   }

   /**
    * Obtain the {@link BeanManager} from the {@link BeanManagerProvider}s
    * 
    * @return the current bean manager for the bean archive
    */
   protected BeanManager getBeanManager()
   {
      if (beanManager == null)
      {
         beanManager = lookupBeanManager();
      }
      if (beanManager == null)
      {
         throw new IllegalStateException("Could not locate a BeanManager from the providers " + providersToString());
      }
      return beanManager;
   }

   /**
    * Check if {@link BeanManagerAware} has been able to find the bean manager
    * 
    * @return <code>true</code> if the bean manager has been found, otherwise
    *         <code>false</code>
    */
   protected boolean isBeanManagerAvailable()
   {
      if (beanManager == null)
      {
         return lookupBeanManager() != null;
      }
      else
      {
         return true;
      }
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
      if (beanManagerProviders.isEmpty())
      {
         loadServices();
         Collections.sort(beanManagerProviders, new Sortable.Comparator());
      }
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
