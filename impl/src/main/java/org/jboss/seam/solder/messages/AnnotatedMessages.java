package org.jboss.seam.solder.messages;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.jboss.logging.Message;
import org.jboss.seam.solder.log.MessageBundle;

/**
 * Messages used for logging in the reflection.annotated package
 * 
 * @author Pete Muir
 *
 */
@MessageBundle
public interface AnnotatedMessages
{

   @Message("%s parameter must not be null")
   public String parameterMustNotBeNull(String parameterName);
   
   @Message("annotationType %s already present")
   public String alreadyPresent(Class<? extends Annotation> annotationType);
   
   @Message("annotationType %s not present")
   public String notPresent(Class<? extends Annotation> annotationType);
   
   @Message("field %s not present on class %s")
   public String notPresent(Field field, Class<?> declaringClass);
   
   @Message("method %s not present on class %s")
   public String notPresent(Method method, Class<?> declaringClass);
   
   @Message("parameter %s not present on method %s declared on class %s")
   public String notPresent(Method method, int parameterPosition, Class<?> declaringClass);

}
