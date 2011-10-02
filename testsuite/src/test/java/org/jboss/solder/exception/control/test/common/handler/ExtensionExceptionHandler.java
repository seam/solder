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
import org.jboss.solder.exception.control.Precedence;
import org.jboss.solder.exception.control.TraversalMode;
import org.jboss.solder.exception.control.test.common.extension.Account;
import org.jboss.solder.exception.control.test.common.extension.Arquillian;
import org.jboss.solder.exception.control.test.common.extension.CatchQualifier;

@HandlesExceptions
public class ExtensionExceptionHandler {
    public void catchDescException(@Handles(during = TraversalMode.BREADTH_FIRST) CaughtException<Exception> event) {
        // Nothing to do currently
    }

    public void catchFrameworkDescException(@Handles(during = TraversalMode.BREADTH_FIRST, precedence = Precedence.FRAMEWORK) CaughtException<Exception> event) {
        // Nothing to do here
    }

    public void catchRuntime(@Handles(during = TraversalMode.DEPTH_FIRST) CaughtException<RuntimeException> event) {
        // Nothing to do currently
    }

    public void catchThrowableBreadthFirst(
            @Handles(precedence = 10, during = TraversalMode.BREADTH_FIRST) CaughtException<Throwable> event) {
        // Nothing to do currently
    }

    public void catchThrowableP20BreadthFirst(
            @Handles(precedence = 20, during = TraversalMode.BREADTH_FIRST) CaughtException<Throwable> event) {
        // Nothing to do currently
    }

    public void catchThrowable(
            @Handles(precedence = 10, during = TraversalMode.DEPTH_FIRST) CaughtException<Throwable> event) {
        // Nothing to do currently
    }

    public void catchThrowableP20(
            @Handles(precedence = 20, during = TraversalMode.DEPTH_FIRST) CaughtException<Throwable> event) {
        // Nothing to do currently
    }

    public void catchIAE(@Handles CaughtException<IllegalArgumentException> event) {
        // Nothing to do currently
    }

    public void qualifiedHandler(@Handles @CatchQualifier CaughtException<Exception> event) {
        // Method to verify the qualifiers are working correctly for handlers
    }

    public void arqHandler(@Handles @Arquillian CaughtException<Throwable> event) {
        // Method to verify the qualifiers are working correctly for handlers
    }

    public void arqTestingHandler(@Handles @Arquillian @CatchQualifier CaughtException<Throwable> event) {
        // Method to verify the qualifiers are working correctly for handlers
    }

    public void differentParamHandlerLocationHandler(Account act, BeanManager bm,
                                                     @Handles CaughtException<SQLException> event) {
        // Nothing here, just need to make sure this handler is picked up
    }

    public void doNothingMethod() {
        // Method to make sure only @Handles methods are found
    }

    public void doNothingTwo(String p1, String p2, int p3) {
        // Same as above
    }
}
