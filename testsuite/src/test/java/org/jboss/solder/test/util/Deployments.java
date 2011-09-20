/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.solder.test.util;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class Deployments {

    private static final String SOLDER_API_JAR = "../api/target/solder-api.jar";
    private static final String SOLDER_IMPL_JAR = "../impl/target/solder-impl.jar";
    private static final String SOLDER_LOGGING_JAR = "../logging/target/solder-logging.jar";

    public static WebArchive baseDeployment() {
        return baseDeployment(true);
    }

    public static WebArchive baseDeployment(boolean includeEmptyBeansXml) {
        
        WebArchive archive;       
        archive = ShrinkWrap.create(WebArchive.class, "test.war")
            .addAsLibraries(
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
                        .as(JavaArchive.class));
        
        if (includeEmptyBeansXml) {
            archive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        }

        return archive;
    }

}
