package org.jboss.weld.extensions.bean.generic;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Allows the product of the generic producer to be injected back into the generic bean
 * 
 * <ul>
 * <li>Only works on injected fields</li>
 * </ul>
 * 
 * @author Pete Muir
 *
 */
@Retention(RUNTIME)
@Target({ METHOD, FIELD, PARAMETER, TYPE })
@Qualifier
public @interface GenericProduct
{

}
