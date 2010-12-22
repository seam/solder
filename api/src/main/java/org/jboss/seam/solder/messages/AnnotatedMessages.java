package org.jboss.seam.solder.messages;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.jboss.logging.Message;
import org.jboss.logging.MessageBundle;

/**
 * Messages used for logging in the reflection.annotated package
 * 
 * @author Pete Muir
 */
@MessageBundle
public interface AnnotatedMessages
{
   @Message("annotationType %s already present")
   public String annotationAlreadyPresent(Class<? extends Annotation> annotationType);
   
   @Message("annotationType %s not present")
   public String annotationNotPresent(Class<? extends Annotation> annotationType);
   
   @Message("field %s not present on class %s")
   public String fieldNotPresent(Field field, Class<?> declaringClass);
   
   @Message("method %s not present on class %s")
   public String methodNotPresent(Method method, Class<?> declaringClass);
   
   @Message("parameter %s not present on method %s declared on class %s")
   public String parameterNotPresent(Method method, int parameterPosition, Class<?> declaringClass);
   
   @Message("%s parameter must not be null")
   public String parameterMustNotBeNull(String parameterName);   
}
