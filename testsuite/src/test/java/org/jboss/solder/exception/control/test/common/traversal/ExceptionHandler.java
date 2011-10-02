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
package org.jboss.solder.exception.control.test.common.traversal;

import java.util.ArrayList;
import java.util.List;

import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.exception.control.Handles;
import org.jboss.solder.exception.control.HandlesExceptions;
import org.jboss.solder.exception.control.TraversalMode;

import static org.jboss.solder.exception.control.test.common.traversal.Exceptions.Exception1;
import static org.jboss.solder.exception.control.test.common.traversal.Exceptions.Exception2;
import static org.jboss.solder.exception.control.test.common.traversal.Exceptions.Exception3;

@HandlesExceptions
public class ExceptionHandler {
    private static final List<Integer> executionOrder = new ArrayList<Integer>();

    public void handleException1BF(@Handles(during = TraversalMode.BREADTH_FIRST) CaughtException<Exception1> event) {
        executionOrder.add(7);
    }

    public void handleException2BF(@Handles(during = TraversalMode.BREADTH_FIRST) CaughtException<Exception2> event) {
        executionOrder.add(5);
    }

    public void handleException3DF(@Handles(during = TraversalMode.DEPTH_FIRST) CaughtException<Exception3> event) {
        executionOrder.add(3);
    }

    public void handleException3BF(@Handles(during = TraversalMode.BREADTH_FIRST) CaughtException<Exception3> event) {
        executionOrder.add(2);
    }

    public void handleException3SuperclassBF(@Handles(during = TraversalMode.BREADTH_FIRST) CaughtException<Exceptions.Exception3Super> event) {
        executionOrder.add(1);
    }

    public void handleException3SuperclassDF(@Handles(during = TraversalMode.DEPTH_FIRST) CaughtException<Exceptions.Exception3Super> event) {
        executionOrder.add(4);
    }

    public void handleException2DF(@Handles(during = TraversalMode.DEPTH_FIRST) CaughtException<Exception2> event) {
        executionOrder.add(6);
    }

    public void handleException1DF(@Handles(during = TraversalMode.DEPTH_FIRST) CaughtException<Exception1> event) {
        executionOrder.add(8);
    }

    public static List<Integer> getExecutionorder() {
        return executionOrder;
    }
}
