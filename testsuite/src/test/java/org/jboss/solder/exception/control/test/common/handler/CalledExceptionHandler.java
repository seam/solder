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

package org.jboss.solder.exception.control.test.common.handler;

import java.sql.SQLException;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.exception.control.Handles;
import org.jboss.solder.exception.control.HandlesExceptions;
import org.jboss.solder.exception.control.TraversalMode;

@HandlesExceptions
public class CalledExceptionHandler {
    public static boolean OUTBOUND_HANDLER_CALLED = false;
    public static int OUTBOUND_HANDLER_TIMES_CALLED = 0;
    public static boolean PROTECTED_HANDLER_CALLED = false;
    public static int INBOUND_HANDLER_TIMES_CALLED = 0;
    public static boolean BEANMANAGER_INJECTED = false;
    public static boolean LOCATION_DIFFER_BEANMANAGER_INJECTED = false;

    public void basicHandler(@Handles CaughtException<Exception> event) {
        OUTBOUND_HANDLER_CALLED = true;
        OUTBOUND_HANDLER_TIMES_CALLED++;
    }

    public void basicInboundHandler(@Handles(during = TraversalMode.BREADTH_FIRST) CaughtException<Exception> event) {
        INBOUND_HANDLER_TIMES_CALLED++;
        event.markHandled();
    }

    public void extraInjections(@Handles CaughtException<IllegalArgumentException> event, BeanManager bm) {
        if (bm != null) {
            BEANMANAGER_INJECTED = true;
        }
    }

    void protectedHandler(@Handles CaughtException<IllegalStateException> event) {
        PROTECTED_HANDLER_CALLED = true;

        if (!event.isMarkedHandled()) {
            event.markHandled();
        }
    }

    @SuppressWarnings("unused")
    private void handlerLocationInjections(BeanManager bm, @Handles CaughtException<SQLException> event) {
        if (bm != null) {
            LOCATION_DIFFER_BEANMANAGER_INJECTED = true;
        }
    }
}
