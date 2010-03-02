package org.jboss.weld.extensions.genericbeans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * used to mark where a generic bean should be inejcted in place of the normal
 * @Inject, this may not be required in the future
 * 
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD, ElementType.PARAMETER })
public @interface InjectGeneric
{

}
