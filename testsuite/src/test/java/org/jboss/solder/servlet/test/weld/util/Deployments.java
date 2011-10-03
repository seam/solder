/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.solder.servlet.test.weld.util;

import java.io.File;

import org.jboss.solder.servlet.support.ServletMessages;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * A utility class to create seed archives for Arquillian tests.
 * 
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
public class Deployments {

    private static final String SOLDER_API_JAR = "../api/target/solder-api.jar";
    private static final String SOLDER_IMPL_JAR = "../impl/target/solder-impl.jar";
    private static final String SOLDER_LOGGING_JAR = "../logging/target/solder-logging.jar";

    public static final Archive<?>[] SEAM_SOLDER = {
                ShrinkWrap.create(
                    ZipImporter.class, "solder-api.jar")
                        .importFrom(new File(SOLDER_API_JAR))
                        .as(JavaArchive.class),
                ShrinkWrap.create(
                    ZipImporter.class, "solder-impl.jar")
                        .importFrom(new File(SOLDER_IMPL_JAR))
                        .as(JavaArchive.class),
                ShrinkWrap.create(
                    ZipImporter.class, "solder-logging.jar")
                        .importFrom(new File(SOLDER_LOGGING_JAR))
                        .as(JavaArchive.class)};

    public static JavaArchive createBeanArchive() {
        return ShrinkWrap.create(JavaArchive.class, "test.jar").addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    public static JavaArchive filteredArchive(JavaArchive archive, Filter<ArchivePath> filter) {
        if (filter != null) {
             return ShrinkWrap.create(JavaArchive.class, archive.getName()).merge(archive, filter);
        }
        return archive;
    }

    public static WebArchive createBeanWebArchive(Filter<ArchivePath> filter) {
        return ShrinkWrap
                .create(WebArchive.class, "test.war")
                // add packages to include generated classes
                .addPackages(false, ServletMessages.class.getPackage())
                .addAsLibraries(
                    filteredArchive(
                        ShrinkWrap.create(
                            ZipImporter.class, "solder-api.jar")
                                .importFrom(new File(SOLDER_API_JAR))
                                .as(JavaArchive.class), filter),
                    filteredArchive(
                        ShrinkWrap.create(
                            ZipImporter.class, "solder-impl.jar")
                                .importFrom(new File(SOLDER_IMPL_JAR))
                                .as(JavaArchive.class), filter),
                    filteredArchive(
                        ShrinkWrap.create(
                            ZipImporter.class, "solder-logging.jar")
                                .importFrom(new File(SOLDER_LOGGING_JAR))
                                .as(JavaArchive.class), filter))
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    public static WebArchive createMockableBeanWebArchive(Filter<ArchivePath> filter) {
        return createBeanWebArchive(filter);
    }

    public static WebArchive createMockableBeanWebArchive() {
        return createBeanWebArchive(null);
    }

    public static Filter<ArchivePath> exclude(final Class<?>... classes) {
        return new Filter<ArchivePath>() {
            public boolean include(ArchivePath ap) {
                String path = ap.get().replace('$', '=');
                for (Class<?> c : classes) {
                    if (path.matches("^/" + c.getName().replace('.', '/') + "(=[1-9])?.class$")) {
                        return false;
                    }
                }
                return true;
            }

        };
    }
}
