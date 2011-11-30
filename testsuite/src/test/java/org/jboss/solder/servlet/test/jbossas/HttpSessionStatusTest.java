/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the &quot;License&quot;);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.solder.servlet.test.jbossas;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.solder.servlet.http.HttpSessionStatus;
import org.jboss.solder.servlet.test.weld.util.Deployments;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 */
@RunWith(Arquillian.class)
public class HttpSessionStatusTest {

    @Deployment @OverProtocol("Servlet 3.0")
    public static Archive<?> createDeployment() {
        return Deployments
                .createMockableBeanWebArchive();
    }

    @Inject
    HttpSessionStatus httpSessionStatus;

    @Test
    public void checkIfHttpSessionStatusIsActive() {
        httpSessionStatus.isActive();
    }

}
