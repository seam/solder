package org.jboss.weld.test.extensions.genericbeans;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface SomeQualifier
{
   int value();
}
