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
package org.jboss.weld.extensions.beanManager;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.extensions.util.Sortable;

/**
 * BeanManagerProvider is an SPI which allows third parties to
 * register a way of obtaining the BeanManager outside of CDI
 * managed objects.
 * 
 * Built in providers are {@link DefaultJndiBeanManagerProvider}
 * and {@link JBossJndiBeanManagerProvider}.
 * 
 * Providers can specify a precedence, allowing a provider to be a 
 * "last resort" provider only.
 * 
 * Precedence about 100 is reserved for providers that should always
 * be used. Precedence below 10 is reserved for providers of last
 * resort.
 * 
 * @author Nicklas Karlsson
 * 
 */
public interface BeanManagerProvider extends Sortable
{
   /**
    * Try to obtain a BeanManager
    * 
    * @return The BeanManager (or null if non found at this location)
    */
   public abstract BeanManager getBeanManager();
}
