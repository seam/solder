package org.jboss.weld.extensions.beanlookup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * annotation that should be applied to an interceptor binding to signify that it requires access to AnnotatedType information
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface RequiresIdentification
{
   
}
