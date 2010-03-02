package org.jboss.weld.extensions.genericbeans;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * synthetic qualifier that is added to Generic beans to signify configuration annotation injection points
 * 
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectConfiguration
{
}
