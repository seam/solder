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
import org.jboss.solder.exception.control.Precedence;
import org.jboss.solder.exception.control.TraversalMode;

@HandlesExceptions
public class ProceedCauseHandler {
    public static int BREADTH_FIRST_NPE_CALLED = 0;
    public static int BREADTH_FIRST_NPE_LOWER_PRECEDENCE_CALLED = 0;

    public static int DEPTH_FIRST_NPE_CALLED = 0;
    public static int DEPTH_FIRST_NPE_HIGHER_PRECEDENCE_CALLED = 0;

    public void npeInboundHandler(
            @Handles(during = TraversalMode.BREADTH_FIRST) CaughtException<NullPointerException> event) {
        BREADTH_FIRST_NPE_CALLED++;
        event.dropCause();
    }

    public void npeLowerPrecedenceInboundHandler(
            @Handles(precedence = Precedence.FRAMEWORK, during = TraversalMode.BREADTH_FIRST) CaughtException<NullPointerException> event) {
        BREADTH_FIRST_NPE_LOWER_PRECEDENCE_CALLED++;
        event.markHandled();
    }

    public void npeOutboundHandler(@Handles CaughtException<NullPointerException> event) {
        DEPTH_FIRST_NPE_CALLED++;
        event.dropCause();
    }

    public void npeHigherPrecedenceOutboundHandler(@Handles(precedence = Precedence.LOW) CaughtException<NullPointerException> event) {
        DEPTH_FIRST_NPE_HIGHER_PRECEDENCE_CALLED++;
        event.markHandled();
    }
}
