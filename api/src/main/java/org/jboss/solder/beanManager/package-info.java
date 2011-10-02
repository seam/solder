/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * <p>
 * A set of utilities for looking up the {@link javax.enterprise.inject.spi.BeanManager} from non-managed classes, which
 * are not able to take advantage of injection.
 * </p>
 *
 * <p>
 * This package includes the {@link BeanManagerProvider} SPI, which may be implemented to allow third parties to
 * register custom methods of looking up the BeanManager in an external context. The implementations will be consulted
 * according to precedence.
 * </p>
 *
 * <p>
 * <b>**WARNING**</b> This package does not provide <b>NOT</b> a clever way to get the BeanManager, and should be
 * <b>avoided at all costs</b>. Are you sure @{@link Inject} is not available? If not, and you still need a reference to
 * the {@link javax.enterprise.inject.spi.BeanManager}, you should probably register a CDI extension instead.
 * </p>
 *
 * <p>
 * If you think you need to use this package, chat to the community and make sure you aren't missing a trick!
 * </p>
 *
 * @see org.jboss.solder.beanManager.BeanManagerLocator
 * @see org.jboss.solder.beanManager.BeanManagerProvider
 * @see org.jboss.solder.beanManager.BeanManagerAware
 */
package org.jboss.solder.beanManager;
