package org.jboss.weld.extensions.beanManager;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

/**
 * <p>
 * Super-class for non-CDI-native components that need a reference to the
 * {@link BeanManager}. {@link BeanManagerProvider}s can be registered to allow
 * third parties to register custom methods of looking up the BeanManager.
 * </p>
 * 
 * <p>
 * <b>**WARNING**</b> This class is <b>NOT</b> a clever way to get the BeanManager,
 * and should be <b>avoided at all costs</b>. If you need a handle to the 
 * {@link BeanManager} you should probably register an {@link Extension} instead of
 * using this class; have you tried using @{@link Inject}?
 * </p>
 * 
 * <p>
 * If you think you need to use this class, chat to the community and make sure you
 * aren't missing an trick!
 * </p>
 * 
 * @see BeanManagerProvider
 * 
 */
public class BeanManagerAccessor extends BeanManagerAware
{
   public static BeanManager getManager()
   {
      return new BeanManagerAccessor().getBeanManager();
   }
} 