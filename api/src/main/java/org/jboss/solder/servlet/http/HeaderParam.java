/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.solder.servlet.http;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Qualifies injection points that should have their values fetched from a corresponding HTTP header.
 * <p/>
 * <p>
 * Example usage:
 * </p>
 * <p/>
 * <pre>
 * &#064;Inject
 * &#064;HeaderParam(&quot;Cache-Control&quot;)
 * private String cacheControl;
 * </pre>
 * <p/>
 * <p>
 * Example usage with default value
 * </p>
 * <p/>
 * <pre>
 * &#064;Inject
 * &#064;HeaderParam(&quot;Cache-Control&quot;)
 * &#064;DefaultValue(&quot;no-cache&quot;)
 * private String cacheControl;
 * </pre>
 * <p/>
 * <p>
 * Because the bean produced is dependent-scoped, use of this annotation on class fields and bean properties is only safe for
 * request-scoped beans. Beans with longer scopes should wrap this bean in a provider and retrieve the value on demand.
 * </p>
 * <p/>
 * <pre>
 * &#064;Inject &#064;HeaderParam(&quot;Cache-Control&quot;)
 * private Instance&lt;String&gt; cacheControlProvider;
 *
 * ...
 *
 * String cacheControl = cacheControlProvider.get();
 * </pre>
 *
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@Qualifier
@Target({TYPE, METHOD, PARAMETER, FIELD})
@Retention(RUNTIME)
@Documented
public @interface HeaderParam {
    @Nonbinding
    public String value() default "";
}
