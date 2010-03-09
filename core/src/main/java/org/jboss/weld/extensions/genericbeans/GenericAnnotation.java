package org.jboss.weld.extensions.genericbeans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Must be applied to any annotation that is used as a value in @Generic, this
 * may not be needed in the future
 * 
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE })
public @interface GenericAnnotation
{

}
