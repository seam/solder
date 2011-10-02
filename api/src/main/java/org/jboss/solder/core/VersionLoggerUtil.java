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
package org.jboss.solder.core;

import org.jboss.solder.logging.Logger;

/**
 * Utility class for logging the version number of class based on package.
 *
 * @author johnament
 */
@Veto
public class VersionLoggerUtil {

    private static final String DEFAULT_LOG_VERSION_INFO = "Version: %s (build id: %s)";

    /**
     * By inspecting specified Class, getting package and specification version/implementation version
     * we write a log message with the provided message format.
     *
     * @param clazz         the class where package will be determined from.
     * @param formatPattern the string format.
     * @param logCategory   the category to log against.
     */
    public static void logVersionInformation(Class<?> clazz, String formatPattern, String logCategory) {
        Logger.getLogger(logCategory)
                .info(createVersionMessage(clazz, formatPattern));
    }

    /**
     * By inspecting specified Class, getting package and specification version/implementation version
     * we write a log message with the provided message format.
     *
     * @param clazz         the class where package will be determined from.
     * @param formatPattern the string format.
     */
    public static void logVersionInformation(Class<?> clazz, String formatPattern) {
        Logger.getLogger(clazz)
                .info(createVersionMessage(clazz, formatPattern));
    }

    /**
     * Logs version information for the given class, using the default format.
     *
     * @param clazz the class where package will be determiend from.
     */
    public static void logVersionInformation(Class<?> clazz) {
        logVersionInformation(clazz, DEFAULT_LOG_VERSION_INFO);
    }

    /**
     * Creates the version message for the class based on the given format.
     * Given format should include 2 string format markers.
     *
     * @param clazz  the class where package will be determiend from.
     * @param format to compile against, should have 2 format markers.
     * @return a string representation of the version information.
     */
    public static String createVersionMessage(Class<?> clazz, String format) {
        return String.format(format,
                clazz.getPackage().getSpecificationVersion(),
                clazz.getPackage().getImplementationVersion());
    }

    /**
     * Creates the version message for the given class using default format.
     *
     * @param clazz the class where package will be determiend from.
     * @return a string representation of the version information.
     */
    public static String createVersionMessage(Class<?> clazz) {
        return createVersionMessage(clazz, DEFAULT_LOG_VERSION_INFO);
    }
}
