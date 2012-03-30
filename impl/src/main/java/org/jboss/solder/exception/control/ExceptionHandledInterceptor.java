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

import javax.enterprise.event.ObserverException;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * @author <a href="http://community.jboss.org/people/LightGuard">Jason Porter</a>
 */
@Interceptor
@ExceptionHandled
public class ExceptionHandledInterceptor {

    @Inject
    private BeanManager bm;

    /**
     * Around invoke method implementation.
     *
     * @param ctx InvocationContext as defined by the Interceptor Spec.
     * @return value of {@link javax.interceptor.InvocationContext#proceed()}, unless an exception occurs, if the method returns a primitive the value
     *         will be 0 for int, short, long, float and false for boolean.
     */
    @AroundInvoke
    public Object passExceptionsToSolderCatch(final InvocationContext ctx) throws Exception {
        Object result = null;
        try {
            result = ctx.proceed();
        } catch (final Throwable e) {
            try {
                bm.fireEvent(new ExceptionToCatch(e));
            } catch (Exception ex) {
                if (ex.getClass().equals(ObserverException.class)) {
                    if (ex.getCause() instanceof Exception) {
                        throw (Exception) ex.getCause();
                    } else if (ex.getCause() instanceof Error) {
                        throw (Error) ex.getCause();
                    } else {
                        throw new RuntimeException(ex.getCause());
                    }
                }
            }
        }

        if (ctx.getMethod().getReturnType().equals(Integer.TYPE) || ctx.getMethod().getReturnType().equals(Short.TYPE)
                || ctx.getMethod().getReturnType().equals(Long.TYPE) || ctx.getMethod().getReturnType().equals(Float.TYPE)
                || ctx.getMethod().getReturnType().equals(Byte.TYPE) || ctx.getMethod().getReturnType().equals(Double.TYPE))
            return 0;

        if (ctx.getMethod().getReturnType().equals(Character.TYPE))
            return '\u0000';

        if (ctx.getMethod().getReturnType().equals(Boolean.TYPE))
            return false;

        return result;
    }
}
