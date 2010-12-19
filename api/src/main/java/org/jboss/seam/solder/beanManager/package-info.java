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
 * @see org.jboss.seam.solder.beanManager.BeanManagerLocator
 * @see org.jboss.seam.solder.beanManager.BeanManagerProvider
 * @see org.jboss.seam.solder.beanManager.BeanManagerAware
 */
package org.jboss.seam.solder.beanManager;
