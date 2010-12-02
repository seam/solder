package org.jboss.seam.solder.test.bean.generic.method;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * A qualifier
 * 
 * @author Stuart Douglas
 * 
 */

@Retention(RUNTIME)
@Target( { METHOD, FIELD, PARAMETER, TYPE })
@Qualifier
public @interface Formatted
{

}
