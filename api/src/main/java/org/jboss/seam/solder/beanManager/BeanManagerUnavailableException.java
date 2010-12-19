package org.jboss.seam.solder.beanManager;

import java.util.List;

import javax.enterprise.inject.spi.BeanManager;

/**
 * A runtime exception that is thrown when the attempt to resolve
 * the BeanManager using the {@link BeanManagerProvider} service fails
 * to locate the {@link BeanManager}. 
 * 
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
public class BeanManagerUnavailableException extends RuntimeException
{
   private List<BeanManagerProvider> providers;
   
   public BeanManagerUnavailableException(List<BeanManagerProvider> providers)
   {
      this.providers = providers;
   }
   
   public List<BeanManagerProvider> getProviders()
   {
      return providers;
   }
   
   public String getProvidersAsString()
   {
      StringBuffer out = new StringBuffer();
      int i = 0;
      for (BeanManagerProvider provider : providers)
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

   @Override
   public String getMessage()
   {
      return "Failed to locate BeanManager using any of these providers: " + getProvidersAsString();
   }
}
