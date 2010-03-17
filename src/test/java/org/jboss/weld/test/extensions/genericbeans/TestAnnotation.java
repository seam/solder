package org.jboss.weld.test.extensions.genericbeans;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.jboss.weld.extensions.genericbeans.GenericAnnotation;

@Retention(RetentionPolicy.RUNTIME)
@GenericAnnotation
public @interface TestAnnotation
{
   String value();
}
