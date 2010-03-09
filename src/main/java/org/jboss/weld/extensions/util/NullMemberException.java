package org.jboss.weld.extensions.util;

import java.lang.reflect.Method;

/**
 * Exception thrown when a annotation is created with a null value
 * for one of the members. 
 * @author Stuart Douglas
 *
 */
public class NullMemberException extends RuntimeException
{
 
   private static final long serialVersionUID = 8300345829555326883L;
   
   private final Class<?> annotationType;
   private final Method method;

   public NullMemberException(Class<?> annotationType, Method method, String message)
   {
      super(message);
      this.annotationType = annotationType;
      this.method = method;
   }

   public Class<?> getAnnotationType()
   {
      return annotationType;
   }

   public Method getMethod()
   {
      return method;
   }

}
