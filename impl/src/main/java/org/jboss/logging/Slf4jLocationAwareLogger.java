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

import java.text.MessageFormat;

import org.slf4j.spi.LocationAwareLogger;

final class Slf4jLocationAwareLogger extends Logger {

    private static final long serialVersionUID = 8685757928087758380L;

    private final LocationAwareLogger logger;

    Slf4jLocationAwareLogger(final String name, final LocationAwareLogger logger) {
        super(name);
        this.logger = logger;
    }

    public boolean isEnabled(final Level level) {
        if (level != null) switch (level) {
            case FATAL:
                return logger.isErrorEnabled();
            case ERROR:
                return logger.isErrorEnabled();
            case WARN:
                return logger.isWarnEnabled();
            case INFO:
                return logger.isInfoEnabled();
            case DEBUG:
                return logger.isDebugEnabled();
            case TRACE:
                return logger.isTraceEnabled();
        }
        return true;
    }

    protected void doLog(final Level level, final String loggerClassName, final Object message, final Object[] parameters, final Throwable thrown) {
        if (isEnabled(level)) {
            final String text = parameters == null || parameters.length == 0 ? String.valueOf(message) : MessageFormat.format(String.valueOf(message), parameters);
            logger.log(null, loggerClassName, translate(level), text, thrown);
        }
    }

    protected void doLogf(final Level level, final String loggerClassName, final String format, final Object[] parameters, final Throwable thrown) {
        if (isEnabled(level)) {
            final String text = parameters == null ? String.format(format) : String.format(format, parameters);
            logger.log(null, loggerClassName, translate(level), text, thrown);
        }
    }

    private static int translate(Level level) {
        if (level != null) switch (level) {
            case FATAL:
            case ERROR:
                return LocationAwareLogger.ERROR_INT;
            case WARN:
                return LocationAwareLogger.WARN_INT;
            case INFO:
                return LocationAwareLogger.INFO_INT;
            case DEBUG:
                return LocationAwareLogger.DEBUG_INT;
            case TRACE:
                return LocationAwareLogger.TRACE_INT;
        }
        return LocationAwareLogger.TRACE_INT;
    }
}
