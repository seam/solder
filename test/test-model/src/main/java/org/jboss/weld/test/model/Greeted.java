package org.jboss.weld.test.model;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

@Target( { PARAMETER, FIELD })
@Retention(RUNTIME)
@Qualifier
public @interface Greeted
{

}
