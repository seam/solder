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
package org.jboss.solder.exception.control.test.common;

import javax.enterprise.inject.spi.Extension;

import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.exception.control.extension.CatchExtension;
import org.jboss.solder.bean.Beans;
import org.jboss.solder.bean.ImmutableInjectionPoint;
import org.jboss.solder.literal.AnyLiteral;
import org.jboss.solder.reflection.AnnotationInspector;
import org.jboss.solder.reflection.annotated.InjectableMethod;
import org.jboss.solder.reflection.annotated.ParameterValueRedefiner;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

/**
 * @author <a href="http://community.jboss.org/people/LightGuard">Jason Porter</a>
 */
public final class BaseWebArchive {

    public synchronized static JavaArchive createBase(final String name) {

        return ShrinkWrap.create(JavaArchive.class, name)
                .addPackage(CaughtException.class.getPackage())
                .addClass(CatchExtension.class)
                .addAsServiceProvider(Extension.class, CatchExtension.class)
                // Solder classes used in Catch
                .addClasses(Beans.class, ImmutableInjectionPoint.class, AnyLiteral.class,
                        InjectableMethod.class, ParameterValueRedefiner.class)
                .addPackage(AnnotationInspector.class.getPackage())
                // Logging in AS7
                .addAsManifestResource(new StringAsset("<jboss-deployment-structure>\n" +
                        "  <deployment>\n" +
                        "    <dependencies>\n" +
                        "      <module name=\"org.jboss.logmanager\" />\n" +
                        "    </dependencies>\n" +
                        "  </deployment>\n" +
                        "</jboss-deployment-structure>"), "jboss-deployment-structure.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
    }
}
