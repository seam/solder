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

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.solder.exception.control.ExceptionToCatch;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.solder.exception.control.test.common.BaseWebArchive;
import org.jboss.solder.exception.control.test.common.traversal.Exceptions.Exception1;
import org.jboss.solder.exception.control.test.common.traversal.Exceptions.Exception2;
import org.jboss.solder.exception.control.test.common.traversal.Exceptions.Exception3;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertArrayEquals;

@RunWith(Arquillian.class)
public class TraversalPathTest {
    @Inject
    private BeanManager manager;

    @Deployment
    public static Archive<?> createTestArchive() {
        return BaseWebArchive.createBase("traversalPath")
                .addPackage(TraversalPathTest.class.getPackage());

    }

    /**
     * Tests SEAMCATCH-32, see JIRA for more information about this test. https://issues.jboss.org/browse/SEAMCATCH-32
     */
    @Test
    public void testTraversalPathOrder() {
        // create an exception stack E1 -> E2 -> E3
        Exception1 exception = new Exception1(new Exception2(new Exception3()));

        manager.fireEvent(new ExceptionToCatch(exception));

        /*
            handleException3SuperclassBF
            handleException3BF
            handleException3DF
            handleException3SuperclassDF
            handleException2BF
            handleException2DF
            handleException1BF
            handleException1DF
        */
        Object[] expectedOrder = {1, 2, 3, 4, 5, 6, 7, 8};
        assertArrayEquals(expectedOrder, ExceptionHandler.getExecutionorder().toArray());
    }
}
