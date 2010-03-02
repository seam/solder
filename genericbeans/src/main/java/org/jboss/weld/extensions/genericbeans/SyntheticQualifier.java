package org.jboss.weld.extensions.genericbeans;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * qualifier that is added to Generic beans to that the correct one is injected
 * 
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
// even though this is not in a bean archive this is still needed to make
// BeanImpl work
@Qualifier
public @interface SyntheticQualifier
{
   long value();
}
