package org.jboss.weld.extensions.bean;

import static org.jboss.weld.extensions.util.Reflections.invokeMethod;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;

public class InjectableMethod<X>
{
   
   private final AnnotatedMethod<X> method;
   private final List<InjectionPoint> parameters;
   private final BeanManager beanManager;
   
   public InjectableMethod(AnnotatedMethod<X> method, Bean<?> bean, BeanManager beanManager)
   {
      this.method = method;
      this.parameters = new ArrayList<InjectionPoint>();
      this.beanManager = beanManager;
      for (AnnotatedParameter<X> parameter : method.getParameters())
      {
         InjectionPoint injectionPoint = new InjectionPointImpl(parameter, beanManager, bean, false, false);
         parameters.add(injectionPoint);
      }
   }
   
   protected Object getParameterValue(InjectionPoint injectionPoint, CreationalContext<?> creationalContext)
   {
      return getBeanManager().getInjectableReference(injectionPoint, creationalContext);
   }
   
   protected BeanManager getBeanManager()
   {
      return beanManager;
   }
   
   protected List<InjectionPoint> getParameters()
   {
      return parameters;
   }
   
   public <T> T invoke(Object receiver, CreationalContext<T> creationalContext)
   {
      List<Object> parameterValues = new ArrayList<Object>();
      for (InjectionPoint parameter : getParameters())
      {
         parameterValues.add(getParameterValue(parameter, creationalContext));
      }
      
      @SuppressWarnings("unchecked")
      T result = (T) invokeMethod(method.getJavaMember(), receiver, parameterValues.toArray());
      
      return result;
   }
   
}