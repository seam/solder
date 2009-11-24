package org.jboss.weld.extensions;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Allows a bean to be defined by annotating a constructor instead
 * of the bean class. There may be multiple beans defined using
 * <tt>@Constructs</tt> per bean class.
 * 
 * @author Gavin King
 *
 */
@Retention(RUNTIME)
@Target(CONSTRUCTOR)
@Documented
public @interface Constructs {}
