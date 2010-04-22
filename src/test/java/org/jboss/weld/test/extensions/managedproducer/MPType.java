package org.jboss.weld.test.extensions.managedproducer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MPType
{
   String value();
}
