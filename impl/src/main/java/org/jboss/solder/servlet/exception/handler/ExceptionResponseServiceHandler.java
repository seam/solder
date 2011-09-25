/*
 * JBoss, Home of Professional Open Source
 * Copyright [2010], Red Hat, Inc., and individual contributors
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
package org.jboss.solder.servlet.exception.handler;

import java.lang.reflect.Method;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.servlet.http.HttpServletRequestContext;

public class ExceptionResponseServiceHandler {
    // does this have to be Instance?
    @Inject
    private Instance<HttpServletRequestContext> requestCtxResolver;

    @AroundInvoke
    public Object processException(InvocationContext ctx) throws Exception {
        Method m = ctx.getMethod();
        // FIXME would be helpful if Catch provided a utility to get the CaughtException
        if (ctx.getParameters().length > 0 && ctx.getParameters()[0] instanceof CaughtException) {
            HttpServletRequestContext requestCtx = requestCtxResolver.get();
            // we can't respond w/ this exception handler if response is committed
            // TODO should see if exception is marked handled already
            if (requestCtx.getResponse().isCommitted()) {
                return Void.TYPE;
            }
            CaughtException<?> c = (CaughtException<?>) ctx.getParameters()[0];
            if (m.isAnnotationPresent(SendHttpError.class)) {
                SendHttpError r = m.getAnnotation(SendHttpError.class);
                String message = r.message().trim();
                if (r.message().length() == 0 && r.useExceptionMessageAsDefault()) {
                    message = c.getException().getMessage();
                }

                if (message != null && message.length() > 0) {
                    requestCtx.getResponse().sendError(r.status(), message);
                } else {
                    requestCtx.getResponse().sendError(r.status());
                }
            } else if (m.isAnnotationPresent(SendErrorPage.class)) {
                SendErrorPage r = m.getAnnotation(SendErrorPage.class);
                if (r.redirect()) {
                    requestCtx.getResponse().sendRedirect(requestCtx.getResponse().encodeRedirectURL(r.value()));
                } else {
                    requestCtx.getRequest().getRequestDispatcher(r.value())
                            .forward(requestCtx.getRequest(), requestCtx.getResponse());
                }
            } else {
                // perhaps log a warning?
            }
        }
        return Void.TYPE;
    }
}
