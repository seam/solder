package org.jboss.weld.test.junit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.jboss.weld.test.core.TestCore;
import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class CDIRunner extends BlockJUnit4ClassRunner
{
   private TestCore core;

   public CDIRunner(Class<?> klass) throws InitializationError
   {
      super(klass);
      initialize();
   }

   /*
    * Try to resolve the appropriate environment and initialize it.
    */
   private void initialize() throws InitializationError
   {
      try
      {
         core = new TestCore();
         core.start();
      } catch (Exception e)
      {
         throw new InitializationError(Arrays.asList((Throwable) e));
      }
   }
   
   /*
    * Override the validation of TestMethods to support methods with arguments
    */
   @Override
   protected void validateTestMethods(List<Throwable> errors)
   {
      validatePublicVoid(Test.class, false, errors);
   }

   protected void validatePublicVoid(Class<? extends Annotation> annotation,
         boolean isStatic, List<Throwable> errors)
   {
      List<FrameworkMethod> methods = getTestClass().getAnnotatedMethods(
            annotation);

      for (FrameworkMethod eachTestMethod : methods)
      {
         eachTestMethod.validatePublicVoid(isStatic, errors);
         validateBindingTypeParameters(eachTestMethod.getMethod(), errors);
      }
   }

   protected void validateBindingTypeParameters(Method method,
         List<Throwable> errors)
   {
      Annotation[][] parameterAnnotations = method.getParameterAnnotations();
      Class<?>[] parameterTypes = method.getParameterTypes();

      for (int i = 0; i < parameterTypes.length; i++)
      {
         Class<?> parameterType = parameterTypes[i];
         if (!TestCore.hasBindTypeAnnotation(parameterAnnotations[i]))
         {
            errors.add(new Exception("Parameter of type "
                  + parameterType.getName() + " on method ("
                  + method.toGenericString() + ") "
                  + " should have a associated BindingType annotation"));
         }
      }
   }

   /*
    * Override to inject TestCase class fields.
    */
   @Override
   protected Object createTest() throws Exception
   {
      Object object = super.createTest();

      core.injectFields(object);

      return object;
   }

   /*
    * Override to invoke TestMethod with injectable args.
    */
   @Override
   protected Statement methodInvoker(FrameworkMethod method, Object test)
   {
      return new ParameterInvokerMethod(method, test);
   }

   private class ParameterInvokerMethod extends Statement
   {

      private final FrameworkMethod fTestMethod;

      private Object fTarget;

      public ParameterInvokerMethod(FrameworkMethod testMethod, Object target)
      {
         fTestMethod = testMethod;
         fTarget = target;
      }

      @Override
      public void evaluate() throws Throwable
      {
         fTestMethod.invokeExplosively(fTarget, getParameters());
      }

      private Object[] getParameters() throws Exception
      {
         Annotation[][] parameterAnnotations = fTestMethod.getMethod()
               .getParameterAnnotations();
         Class<?>[] parameterTypes = fTestMethod.getMethod()
               .getParameterTypes();
         Object[] parameters = new Object[parameterTypes.length];
         for (int i = 0; i < parameterTypes.length; i++)
         {
            Class<?> parameterType = parameterTypes[i];
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            parameters[i] = core.getInstanceByType(parameterType,
                  parameterAnnotation);
         }
         return parameters;
      }
   }
}
