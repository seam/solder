/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.solder.exception.control;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

/**
 * Interceptor annotation to wrap a method, or each method of a class, in a try / catch which will pass all exceptions
 * to Solder Catch. The preferred way to use Catch is still going to be to inject an <code>Event<ExceptionToCatch></code>
 * and manually fire the event, this interceptor is added here as another option, however, there is no control over
 * qualifiers being added for the handlers. Also use of this interceptor may result in odd behavior of the application
 * based on returns of the interceptor when exceptions occur, please see the implementation of this interceptor for more detail
 * (org.jboss.solder.ecxeption.control.ExceptionHandledInterceptor).
 *
 * @author <a href="http://community.jboss.org/people/LightGuard">Jason Porter</a>
 */
@InterceptorBinding
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExceptionHandled {
}
