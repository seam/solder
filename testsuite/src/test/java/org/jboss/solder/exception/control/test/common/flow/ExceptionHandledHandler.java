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

package org.jboss.solder.exception.control.test.common.flow;

import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.exception.control.Handles;
import org.jboss.solder.exception.control.HandlesExceptions;
import org.jboss.solder.exception.control.TraversalMode;

@HandlesExceptions
public class ExceptionHandledHandler {
    public static boolean EX_ASC_CALLED = false;
    public static boolean IAE_ASC_CALLED = false;
    public static boolean NPE_DESC_CALLED = false;

    public void exHandler(@Handles CaughtException<Exception> event) {
        EX_ASC_CALLED = true;
    }

    public void npeHandler(@Handles CaughtException<IllegalArgumentException> event) {
        IAE_ASC_CALLED = true;
        event.handled();
    }

    public void npeDescHandler(@Handles(during = TraversalMode.BREADTH_FIRST) CaughtException<NullPointerException> event) {
        NPE_DESC_CALLED = true;
        event.handled();
    }
}
