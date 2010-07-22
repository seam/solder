package org.jboss.weld.extensions.test.autoproxy;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PersonName
{
   String value();
}
