package org.jboss.seam.solder.beanManager;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

/**
 * <p>
 * Static accessors for objects not able to obtain CDI injection that need a
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
 * @see BeanManagerAware
 * 
 * @author Pete Muir
 * @author Nicklas Karlsson
 * 
 */
public class BeanManagerAccessor
{

   private BeanManagerAccessor()
   {
   }

   /**
    * Obtain the {@link BeanManager} from the {@link BeanManagerProvider}s
    * 
    * @return the current bean manager for the bean archive
    */
   public static BeanManager getBeanManager()
   {
      return new BeanManagerAware().getBeanManager();
   }

   public static boolean isBeanManagerAvailable()
   {
      return new BeanManagerAware().isBeanManagerAvailable();
   }
}