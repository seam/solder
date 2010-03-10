package org.jboss.weld.extensions;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * An injection point qualifier that may be used to specify a resource to inject
 * 
 * @author Pete Muir
 * 
 */
@Retention(RUNTIME)
@Target( { METHOD, TYPE, FIELD, PARAMETER })
@Documented
@Qualifier
public @interface Resource
{
   
   @Nonbinding
   String value();
   
}
