package org.jboss.solder.servlet.event;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Qualifies observer methods to select HTTP lifecycle events for a particular Servlet path. A leading '/' should not be used in
 * the value as base URIs are treated as if they ended in '/'.
 *
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@Qualifier
@Target({PARAMETER, FIELD})
@Retention(RUNTIME)
@Documented
public @interface Path {
    String value();
}
