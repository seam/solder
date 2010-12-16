package org.jboss.seam.solder.test.bean.generic.method;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jboss.seam.solder.bean.generic.GenericType;

@Retention(RUNTIME)
@Target( { METHOD, FIELD, PARAMETER, TYPE })
@GenericType(Kitchen.class)
public @interface Room
{

}
