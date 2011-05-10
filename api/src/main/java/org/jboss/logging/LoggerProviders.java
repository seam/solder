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

package org.jboss.logging;

import java.util.logging.LogManager;

final class LoggerProviders {
    static final LoggerProvider PROVIDER = findProvider();

    private static LoggerProvider findProvider() {
        final LogManager jdkLogManager = LogManager.getLogManager();
        final ClassLoader cl = getClassLoader();
        try {
            if (jdkLogManager.getClass().getName().equals("org.jboss.logmanager.LogManager")) {
                return (LoggerProvider) Class.forName("org.jboss.logging.JBossLogManagerProvider", true, cl).newInstance();
            }
        } catch (Throwable t) {
            // nope...
        }
        try {
            Class.forName("org.apache.log4j.LogManager", true, cl);
            return (LoggerProvider) Class.forName("org.jboss.logging.Log4jLoggerProvider", true, cl).newInstance();
        } catch (Throwable t) {
            // nope...
        }
        try {
            // only use slf4j if Logback is in use
            Class.forName("ch.qos.logback.classic.Logger", false, cl);
            return (LoggerProvider) Class.forName("org.jboss.logging.Slf4jLoggerProvider", true, cl).newInstance();
        } catch (Throwable t) {
            // nope...
        }
        return new JDKLoggerProvider();
    }

    private static ClassLoader getClassLoader() {
        // Since the impl classes refer to the back-end frameworks directly, if this classloader can't find the target
        // log classes, then it doesn't really matter if they're possibly available from the TCCL because we won't be
        // able to find it anyway
        return LoggerProviders.class.getClassLoader();
    }

    private LoggerProviders() {
    }
}
