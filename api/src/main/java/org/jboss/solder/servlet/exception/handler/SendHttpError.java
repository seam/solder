package org.jboss.solder.servlet.exception.handler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.servlet.http.HttpServletResponse;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SendHttpError {
    int status() default HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

    String message() default "";

    boolean useExceptionMessageAsDefault() default true;
}
