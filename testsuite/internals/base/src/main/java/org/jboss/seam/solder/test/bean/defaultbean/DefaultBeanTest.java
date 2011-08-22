/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.seam.solder.test.bean.defaultbean;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.solder.test.util.Deployments;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This test verifies that {@link @GenericBean} works as expected.
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 */
@RunWith(Arquillian.class)
public class DefaultBeanTest {
    @Inject
    private Airplane airplane;
    @Inject
    private Vehicle vehicle;

    @Deployment(name = "DefaultBean")
    public static WebArchive createDeployment() {
        return Deployments.baseDeployment()
            .addPackage(DefaultBeanTest.class.getPackage());
    }

    @Test
    public void testDefaultProducerUsed() {
        assertEquals("Cessna 172", airplane.getName());
    }

    @Test
    public void testDefaultProducerNotUsed() {
        assertEquals("Seat Ibiza", vehicle.getName());
    }
}
