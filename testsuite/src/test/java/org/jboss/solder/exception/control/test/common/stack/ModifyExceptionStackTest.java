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

/**
 *
 * @author <a href="http://community.jboss.org/people/LightGuard">Jason Porter</a>
 */
package org.jboss.solder.exception.control.test.common.stack;

import java.util.ArrayList;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.exception.control.ExceptionStack;
import org.jboss.solder.exception.control.ExceptionToCatch;
import org.jboss.solder.exception.control.Handles;
import org.jboss.solder.exception.control.HandlesExceptions;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.solder.exception.control.test.common.BaseWebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

@RunWith(Arquillian.class)
@HandlesExceptions
public class ModifyExceptionStackTest {
    private Exception startException;

    @Deployment
    public static Archive<?> createTestArchive() {
        return BaseWebArchive.createBase("ModifyExceptionStack")
                .addClasses(ModifyExceptionStackTest.class);
    }

    @SuppressWarnings("serial")
    public static class ApplicationException extends Exception {

    }

    @Test
    public void assertModifyingStack(Event<ExceptionToCatch> event) {
        this.startException = new Exception(new NullPointerException());
        event.fire(new ExceptionToCatch(this.startException));
    }

    public void changeStackObserver(@Observes ExceptionStack stack) {
        ArrayList<Throwable> causes = new ArrayList<Throwable>(stack.getCauseElements());
        causes.set(0, new ApplicationException());
        stack.setCauseElements(causes);
    }

    public void assertionHandler(@Handles CaughtException<Throwable> e) {
        assertThat(e.getExceptionStack().getCauseElements(), not(hasItem((Throwable) this.startException)));
    }
}
