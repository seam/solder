package org.jboss.weld.extensions.unwraps;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This allows for producer methods that emulate a stateless scope.
 * 
 * If a method is annotated with @Unwraps then a bean is registered with CDI
 * with the same types and qualifiers as the @Unwraps method. This bean produces
 * a proxy, whenever a method is invoked on the proxy the @Unwraps method is
 * called, and the method invocation is forwarded to the result.
 * 
 * This allows you to manually control the lifecycle of an object while still
 * allowing it to be injected.
 * 
 * @author Stuart Douglas
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Unwraps
{

}
