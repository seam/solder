package org.jboss.webbeans.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.BindingType;

/**
 * Injects a log
 * 
 * @author Gavin King
 */
@Target(FIELD)
@Retention(RUNTIME)
@Documented
@BindingType
public @interface Logger 
{
   /**
    * @return the log category
    */
   String value() default "";
}
