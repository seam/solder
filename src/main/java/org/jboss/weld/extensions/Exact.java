package org.jboss.weld.extensions;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * An injection point qualifier that may be used to select
 * the exact bean to be injected, by specifying its implementation
 * class.
 * 
 * @author Gavin King
 *
 */
@Retention(RUNTIME)
@Target({METHOD, TYPE, FIELD, PARAMETER})
@Documented
@Qualifier
public @interface Exact {
	Class<?> value() default void.class;
}
