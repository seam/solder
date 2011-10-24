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

package org.jboss.solder.exception.control.test.common.event;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.exception.control.ExceptionToCatch;
import org.jboss.solder.exception.control.Handles;
import org.jboss.solder.exception.control.HandlesExceptions;
import org.jboss.solder.exception.control.TraversalMode;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.solder.exception.control.test.common.BaseWebArchive;
import org.jboss.solder.exception.control.test.common.event.literal.EventQualifierLiteral;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
@HandlesExceptions
public class EventTest {
    @Deployment(name = "EventTest")
    public static Archive<?> createTestArchive() {
        return BaseWebArchive.createBase("eventTest")
                .addClasses(EventTest.class, EventQualifier.class, EventQualifierLiteral.class);
    }

    @Inject
    private BeanManager bm;

    private int qualiferCalledCount = 0;

    @Test
    public void assertEventIsCreatedCorrectly() {
        bm.fireEvent(new ExceptionToCatch(new NullPointerException()));
    }

    @Test
    public void assertEventWithQualifiersIsCreatedCorrectly() {
        this.bm.fireEvent(new ExceptionToCatch(new NullPointerException(), EventQualifierLiteral.INSTANCE));
    }

    public void verifyDescEvent(@Handles(during = TraversalMode.BREADTH_FIRST) CaughtException<NullPointerException> event) {
        this.qualiferCalledCount++;
        assertTrue(event.isBreadthFirstTraversal());
        assertFalse(event.isDepthFirstTraversal());
    }

    public void verifyAscEvent(@Handles(during = TraversalMode.DEPTH_FIRST) CaughtException<NullPointerException> event) {
        this.qualiferCalledCount++;
        assertFalse(event.isBreadthFirstTraversal());
        assertTrue(event.isDepthFirstTraversal());
    }

    public void verifyQualifierEvent(@Handles @EventQualifier CaughtException<NullPointerException> event) {
        this.qualiferCalledCount++;
        assertThat(this.qualiferCalledCount, is(1));
    }
}
