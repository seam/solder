package org.jboss.weld.extensions.genericbeans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a generic bean
 * 
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE })
public @interface Generic
{
   Class<?> value();
}
