package org.jboss.weld.extensions.bean;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;

public interface ParameterValueRedefiner
{
   
   
   public static class ParameterValue
   {
      
      private final int position;
      private final InjectionPoint injectionPoint;
      private final BeanManager beanManager;
      
      ParameterValue(int position, InjectionPoint injectionPoint, BeanManager beanManager)
      {
         this.position = position;
         this.injectionPoint = injectionPoint;
         this.beanManager = beanManager;
      }

      public int getPosition()
      {
         return position;
      }
      
      public InjectionPoint getInjectionPoint()
      {
         return injectionPoint;
      }
      
      public Object getDefaultValue(CreationalContext<?> creationalContext)
      {
         return beanManager.getInjectableReference(injectionPoint, creationalContext);
      }

   }
   
   public Object redefineParameterValue(ParameterValue value);
   
}
