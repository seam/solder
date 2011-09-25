package org.jboss.solder.servlet.exception.handler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.solder.serviceHandler.ServiceHandlerType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ServiceHandlerType(ExceptionResponseServiceHandler.class)
public @interface ExceptionResponses {
}
