package org.jboss.weld.log;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.inject.Nonbinding;
import javax.inject.Qualifier;

/**
 * Injects a log
 * 
 * @author Gavin King
 */
@Target({METHOD, FIELD, PARAMETER, TYPE})
@Retention(RUNTIME)
@Documented
@Qualifier
public @interface Logger 
{
   /**
    * @return the log category
    */
   @Nonbinding
   String value() default "";
}
