/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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

package org.jboss.solder.logging;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jboss.solder.messages.MessageBundle;

/**
 * Signify that an interface is a typed logger interface.  A message logger interface may optionally extend other message logger
 * interfaces and message bundle interfaces (see {@link MessageBundle}, as well as the {@link org.jboss.logging.BasicLogger} interface.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@Retention(RUNTIME)
@Target(TYPE)
@Documented
public @interface MessageLogger {

    /**
     * Get the project code for messages that have an associated code.  If no project code is associated
     * with this logger, specify {@code ""} (the empty string).
     *
     * @return the project code
     */
    String projectCode() default "";

    /**
     * The logging class name to use for this message, if any.
     *
     * @return the logging class name
     */
    Class<?> loggingClass() default Void.class;
}
