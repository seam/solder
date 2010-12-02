/**
 * <p>
 * Support for objects not able to obtain CDI injection that need a reference to the 
 * {@link javax.enterprise.inject.spi.BeanManager}. {@link org.jboss.seam.solder.beanManager.BeanManagerProvider}s 
 * can be registered to allow third parties to register custom methods of looking up the 
 * {@link javax.enterprise.inject.spi.BeanManager}.
 * </p>
 * 
 * <p>
 * <b>**WARNING**</b> This package does not provide <b>NOT</b> a clever way to get the BeanManager, and should be 
 * <b>avoided at all costs</b>. If you need a handle to the {@link BeanManager} you should probably register an 
 * {@link Extension} instead of using this package; have you tried using @{@link Inject}?
 * </p>
 * 
 * <p>
 * If you think you need to use this package, chat to the community and make sure you aren't missing an trick!
 * </p>
 * 
 * @see org.jboss.seam.solder.beanManager.BeanManagerAccessor
 * @see org.jboss.seam.solder.beanManager.BeanManagerAware
 * @see org.jboss.seam.solder.beanManager.BeanManagerProvider
 */
package org.jboss.seam.solder.beanManager;

