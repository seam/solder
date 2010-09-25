package org.jboss.weld.extensions.bean;

import static org.jboss.weld.extensions.reflection.Reflections.EMPTY_OBJECT_ARRAY;
import static org.jboss.weld.extensions.reflection.Reflections.invokeMethod;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.weld.extensions.bean.ParameterValueRedefiner.ParameterValue;

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
         InjectionPoint injectionPoint = wrapParameterInjectionPoint(new InjectionPointImpl(parameter, beanManager, bean, false, false));
         parameters.add(injectionPoint);
      }
   }
   
   protected InjectionPoint wrapParameterInjectionPoint(InjectionPoint injectionPoint)
   {
      return injectionPoint;
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
      return invoke(receiver, creationalContext, null);
   }
   
   public <T> T invoke(Object receiver, CreationalContext<T> creationalContext, ParameterValueRedefiner redefinition)
   {
      List<Object> parameterValues = new ArrayList<Object>();
      for (int i = 0; i < getParameters().size(); i++)
      {
         
         if (redefinition != null)
         {
            ParameterValue value = new ParameterValue(i, getParameters().get(i), getBeanManager());
            parameterValues.add(redefinition.redefineParameterValue(value));
         }
         else
         {
            parameterValues.add(getBeanManager().getInjectableReference(getParameters().get(i), creationalContext));
         }
      }
      
      @SuppressWarnings("unchecked")
      T result = (T) invokeMethod(method.getJavaMember(), receiver, parameterValues.toArray(EMPTY_OBJECT_ARRAY));
      
      return result;
   }
   
}