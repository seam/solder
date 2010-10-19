package org.jboss.weld.extensions.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation &#064;FullQualified, when used on a bean type, producer method
 * or producer field, indicates that the standard bean name that would be
 * assigned should first be prepended with the package in which the bean
 * resides, thus resulting in a fully-qualified bean name (FQBN).
 * 
 * <p>
 * <strong>FQBN syntax</strong>
 * </p>
 * <pre>
 * PackageName:
 *    Package name of type or containing type
 * Period:
 *    A period character (i.e., '.')
 * BeanName:
 *    The standard bean name for the element 
 * </pre>
 * 
 * <p>
 * <strong>Processing rules</strong> - &#064;FullyQualified is permitted on a
 * bean type, producer method, producer field or a Java package. This annotation
 * is only processed by the extension that provides it when used in combination
 * with &#064;Named on a bean type, producer method or producer field. Though,
 * that does not exclude it from being used as a common annotation for other
 * purposes.
 * </p>
 * 
 * <p>
 * <strong>Motivation</strong> - The default behavior of &#064;Named (as
 * documented in {@link javax.inject.Named}) is the most common use case for
 * application developers. However, framework writers should avoid trampling on
 * the "root" bean namespace. Instead, frameworks should specify qualified names
 * for built-in components. The motivation is the same as qualifying Java types.
 * The &#064;FullyQualified provides this facility without sacrificing
 * type-safety.
 * </p>
 * 
 * 
 * @author Dan Allen
 * @see {@link javax.inject.Named}
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FullyQualified
{
   /**
    * A class from the package that should be used as the namespace
    * that is prepended to the bean name. The special value
    * {@link Class}.class specifies that the current package should be used.
    */
   Class<?> value() default Class.class;
}
