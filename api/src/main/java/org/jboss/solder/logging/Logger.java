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
package org.jboss.solder.logging;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

/**
 * A Logger implementation that forwards all calls to the {@link #delegate()}.
 * 
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class Logger implements Serializable {

    private static final long serialVersionUID = 2699068144024070551L;

    private final org.jboss.solder.logging.internal.Logger delegate;

    /**
     * Levels used by this logging API.
     */
    public enum Level {
        FATAL, ERROR, WARN, INFO, DEBUG, TRACE,
    }

    Logger(String name) {
        this.delegate = org.jboss.solder.logging.internal.Logger.getLogger(name);
    }

    /**
     * Return the name of this logger.
     * 
     * @return The name of this logger.
     */
    public String getName() {
        return delegate.getName();
    }

    /**
     * Check to see if the specified level is enabled for this logger.
     * 
     * @param level the level
     * @return {@code true} if messages logged at {@link Level#level} may be accepted, {@code false} otherwise
     */
    public boolean isEnabled(Level level) {
        return delegate.isEnabled(translate(level));
    }

    /**
     * Check to see if the {@code TRACE} level is enabled for this logger.
     * 
     * @return {@code true} if messages logged at {@link Level#TRACE} may be accepted, {@code false} otherwise
     */
    public boolean isTraceEnabled() {
        return delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.TRACE);
    }

    /**
     * Issue a log message with a level of TRACE.
     * 
     * @param message the message
     */
    public void trace(Object message) {
        delegate.trace(message);
    }

    /**
     * Issue a log message and throwable with a level of TRACE.
     * 
     * @param message the message
     * @param t the throwable
     */
    public void trace(Object message, Throwable t) {
        delegate.trace(message, t);
    }

    /**
     * Issue a log message and throwable with a level of TRACE and a specific logger class name.
     * 
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param t the throwable
     */
    public void trace(String loggerFqcn, Object message, Throwable t) {
        delegate.trace(loggerFqcn, message, t);
    }

    /**
     * Issue a log message with parameters and a throwable with a level of TRACE.
     * 
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param params the message parameters
     * @param t the throwable
     */
    public void trace(String loggerFqcn, Object message, Object[] params, Throwable t) {
        delegate.trace(loggerFqcn, message, params, t);
    }

    /**
     * Issue a log message with a level of TRACE using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param format the message format string
     * @param params the parameters
     */
    public void tracev(String format, Object... params) {
        delegate.tracev(format, params);
    }

    /**
     * Issue a log message with a level of TRACE using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void tracev(String format, Object param1) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.TRACE)) {
            delegate.tracev(format, param1);
        }
    }

    /**
     * Issue a log message with a level of TRACE using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void tracev(String format, Object param1, Object param2) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.TRACE)) {
            delegate.tracev(format, param1, param2);
        }
    }

    /**
     * Issue a log message with a level of TRACE using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void tracev(String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.TRACE)) {
            delegate.tracev(format, param1, param2, param3);
        }
    }

    /**
     * Issue a log message with a level of TRACE using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param t the throwable
     * @param format the message format string
     * @param params the parameters
     */
    public void tracev(Throwable t, String format, Object... params) {
        delegate.tracev(t, format, params);
    }

    /**
     * Issue a log message with a level of TRACE using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param t the throwable
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void tracev(Throwable t, String format, Object param1) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.TRACE)) {
            delegate.tracev(t, format, param1);
        }
    }

    /**
     * Issue a log message with a level of TRACE using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void tracev(Throwable t, String format, Object param1, Object param2) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.TRACE)) {
            delegate.tracev(t, format, param1, param2);
        }
    }

    /**
     * Issue a log message with a level of TRACE using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void tracev(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.TRACE)) {
            delegate.tracev(t, format, param1, param2, param3);
        }
    }

    /**
     * Issue a formatted log message with a level of TRACE.
     * 
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param params the parameters
     */
    public void tracef(String format, Object... params) {
        delegate.tracev(format, params);
    }

    /**
     * Issue a formatted log message with a level of TRACE.
     * 
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the sole parameter
     */
    public void tracef(String format, Object param1) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.TRACE)) {
            delegate.tracev(format, param1);
        }
    }

    /**
     * Issue a formatted log message with a level of TRACE.
     * 
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void tracef(String format, Object param1, Object param2) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.TRACE)) {
            delegate.tracev(format, param1, param2);
        }
    }

    /**
     * Issue a formatted log message with a level of TRACE.
     * 
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void tracef(String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.TRACE)) {
            delegate.tracev(format, param1, param2, param3);
        }
    }

    /**
     * Issue a formatted log message with a level of TRACE.
     * 
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param params the parameters
     */
    public void tracef(Throwable t, String format, Object... params) {
        delegate.tracev(t, format, params);
    }

    /**
     * Issue a formatted log message with a level of TRACE.
     * 
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the sole parameter
     */
    public void tracef(Throwable t, String format, Object param1) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.TRACE)) {
            delegate.tracev(t, format, param1);
        }
    }

    /**
     * Issue a formatted log message with a level of TRACE.
     * 
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void tracef(Throwable t, String format, Object param1, Object param2) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.TRACE)) {
            delegate.tracev(t, format, param1, param2);
        }
    }

    /**
     * Issue a formatted log message with a level of TRACE.
     * 
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void tracef(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.TRACE)) {
            delegate.tracev(t, format, param1, param2, param3);
        }
    }

    /**
     * Check to see if the {@code DEBUG} level is enabled for this logger.
     * 
     * @return {@code true} if messages logged at {@link Level#DEBUG} may be accepted, {@code false} otherwise
     */
    public boolean isDebugEnabled() {
        return delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.DEBUG);
    }

    /**
     * Issue a log message with a level of DEBUG.
     * 
     * @param message the message
     */
    public void debug(Object message) {
        delegate.debug(message);
    }

    /**
     * Issue a log message and throwable with a level of DEBUG.
     * 
     * @param message the message
     * @param t the throwable
     */
    public void debug(Object message, Throwable t) {
        delegate.debug(message, t);
    }

    /**
     * Issue a log message and throwable with a level of DEBUG and a specific logger class name.
     * 
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param t the throwable
     */
    public void debug(String loggerFqcn, Object message, Throwable t) {
        delegate.debug(loggerFqcn, message, t);
    }

    /**
     * Issue a log message with parameters and a throwable with a level of DEBUG.
     * 
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param params the message parameters
     * @param t the throwable
     */
    public void debug(String loggerFqcn, Object message, Object[] params, Throwable t) {
        delegate.debug(loggerFqcn, message, params, t);
    }

    /**
     * Issue a log message with a level of DEBUG using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param format the message format string
     * @param params the parameters
     */
    public void debugv(String format, Object... params) {
        delegate.debugv(format, params);
    }

    /**
     * Issue a log message with a level of DEBUG using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void debugv(String format, Object param1) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.DEBUG)) {
            delegate.debugv(format, param1);
        }
    }

    /**
     * Issue a log message with a level of DEBUG using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void debugv(String format, Object param1, Object param2) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.DEBUG)) {
            delegate.debugv(format, param1, param2);
        }
    }

    /**
     * Issue a log message with a level of DEBUG using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void debugv(String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.DEBUG)) {
            delegate.debugv(format, param1, param2, param3);
        }
    }

    /**
     * Issue a log message with a level of DEBUG using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param t the throwable
     * @param format the message format string
     * @param params the parameters
     */
    public void debugv(Throwable t, String format, Object... params) {
        delegate.debugv(t, format, params);
    }

    /**
     * Issue a log message with a level of DEBUG using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param t the throwable
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void debugv(Throwable t, String format, Object param1) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.DEBUG)) {
            delegate.debugv(t, format, param1);
        }
    }

    /**
     * Issue a log message with a level of DEBUG using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void debugv(Throwable t, String format, Object param1, Object param2) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.DEBUG)) {
            delegate.debugv(t, format, param1, param2);
        }
    }

    /**
     * Issue a log message with a level of DEBUG using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void debugv(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.DEBUG)) {
            delegate.debugv(t, format, param1, param2, param3);
        }
    }

    /**
     * Issue a formatted log message with a level of DEBUG.
     * 
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param params the parameters
     */
    public void debugf(String format, Object... params) {
        delegate.debugf(format, params);
    }

    /**
     * Issue a formatted log message with a level of DEBUG.
     * 
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the sole parameter
     */
    public void debugf(String format, Object param1) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.DEBUG)) {
            delegate.debugf(format, param1);
        }
    }

    /**
     * Issue a formatted log message with a level of DEBUG.
     * 
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void debugf(String format, Object param1, Object param2) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.DEBUG)) {
            delegate.debugf(format, param1, param2);
        }
    }

    /**
     * Issue a formatted log message with a level of DEBUG.
     * 
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void debugf(String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.DEBUG)) {
            delegate.debugf(format, param1, param2, param3);
        }
    }

    /**
     * Issue a formatted log message with a level of DEBUG.
     * 
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param params the parameters
     */
    public void debugf(Throwable t, String format, Object... params) {
        delegate.debugf(t, format, params);
    }

    /**
     * Issue a formatted log message with a level of DEBUG.
     * 
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the sole parameter
     */
    public void debugf(Throwable t, String format, Object param1) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.DEBUG)) {
            delegate.debugf(t, format, param1);
        }
    }

    /**
     * Issue a formatted log message with a level of DEBUG.
     * 
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void debugf(Throwable t, String format, Object param1, Object param2) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.DEBUG)) {
            delegate.debugf(t, format, param1, param2);
        }
    }

    /**
     * Issue a formatted log message with a level of DEBUG.
     * 
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void debugf(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.DEBUG)) {
            delegate.debugf(t, format, param1, param2, param3);
        }
    }

    /**
     * Check to see if the {@code INFO} level is enabled for this logger.
     * 
     * @return {@code true} if messages logged at {@link Level#INFO} may be accepted, {@code false} otherwise
     */
    public boolean isInfoEnabled() {
        return delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.INFO);
    }

    /**
     * Issue a log message with a level of INFO.
     * 
     * @param message the message
     */
    public void info(Object message) {
        delegate.info(message);
    }

    /**
     * Issue a log message and throwable with a level of INFO.
     * 
     * @param message the message
     * @param t the throwable
     */
    public void info(Object message, Throwable t) {
        delegate.info(message, t);
    }

    /**
     * Issue a log message and throwable with a level of INFO and a specific logger class name.
     * 
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param t the throwable
     */
    public void info(String loggerFqcn, Object message, Throwable t) {
        delegate.info(loggerFqcn, message, t);
    }

    /**
     * Issue a log message with parameters and a throwable with a level of INFO.
     * 
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param params the message parameters
     * @param t the throwable
     */
    public void info(String loggerFqcn, Object message, Object[] params, Throwable t) {
        delegate.info(loggerFqcn, message, params, t);
    }

    /**
     * Issue a log message with a level of INFO using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param format the message format string
     * @param params the parameters
     */
    public void infov(String format, Object... params) {
        delegate.infov(format, params);
    }

    /**
     * Issue a log message with a level of INFO using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void infov(String format, Object param1) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.INFO)) {
            delegate.infov(format, param1);
        }
    }

    /**
     * Issue a log message with a level of INFO using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void infov(String format, Object param1, Object param2) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.INFO)) {
            delegate.infov(format, param1, param2);
        }
    }

    /**
     * Issue a log message with a level of INFO using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void infov(String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.INFO)) {
            delegate.infov(format, param1, param2, param3);
        }
    }

    /**
     * Issue a log message with a level of INFO using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param t the throwable
     * @param format the message format string
     * @param params the parameters
     */
    public void infov(Throwable t, String format, Object... params) {
        delegate.infov(t, format, params);
    }

    /**
     * Issue a log message with a level of INFO using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param t the throwable
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void infov(Throwable t, String format, Object param1) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.INFO)) {
            delegate.infov(t, format, param1);
        }
    }

    /**
     * Issue a log message with a level of INFO using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void infov(Throwable t, String format, Object param1, Object param2) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.INFO)) {
            delegate.infov(t, format, param1, param2);
        }
    }

    /**
     * Issue a log message with a level of INFO using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void infov(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.INFO)) {
            delegate.infov(t, format, param1, param2, param3);
        }
    }

    /**
     * Issue a formatted log message with a level of INFO.
     * 
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param params the parameters
     */
    public void infof(String format, Object... params) {
        delegate.infof(format, params);
    }

    /**
     * Issue a formatted log message with a level of INFO.
     * 
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the sole parameter
     */
    public void infof(String format, Object param1) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.INFO)) {
            delegate.infof(format, param1);
        }
    }

    /**
     * Issue a formatted log message with a level of INFO.
     * 
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void infof(String format, Object param1, Object param2) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.INFO)) {
            delegate.infof(format, param1, param2);
        }
    }

    /**
     * Issue a formatted log message with a level of INFO.
     * 
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void infof(String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.INFO)) {
            delegate.infof(format, param1, param2, param3);
        }
    }

    /**
     * Issue a formatted log message with a level of INFO.
     * 
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param params the parameters
     */
    public void infof(Throwable t, String format, Object... params) {
        delegate.infof(t, format, params);
    }

    /**
     * Issue a formatted log message with a level of INFO.
     * 
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the sole parameter
     */
    public void infof(Throwable t, String format, Object param1) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.INFO)) {
            delegate.infof(t, format, param1);
        }
    }

    /**
     * Issue a formatted log message with a level of INFO.
     * 
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void infof(Throwable t, String format, Object param1, Object param2) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.INFO)) {
            delegate.infof(t, format, param1, param2);
        }
    }

    /**
     * Issue a formatted log message with a level of INFO.
     * 
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void infof(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.INFO)) {
            delegate.infof(t, format, param1, param2, param3);
        }
    }

    /**
     * Issue a log message with a level of WARN.
     * 
     * @param message the message
     */
    public void warn(Object message) {
        delegate.warn(message);
    }

    /**
     * Issue a log message and throwable with a level of WARN.
     * 
     * @param message the message
     * @param t the throwable
     */
    public void warn(Object message, Throwable t) {
        delegate.warn(message, t);
    }

    /**
     * Issue a log message and throwable with a level of WARN and a specific logger class name.
     * 
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param t the throwable
     */
    public void warn(String loggerFqcn, Object message, Throwable t) {
        delegate.warn(loggerFqcn, message, t);
    }

    /**
     * Issue a log message with parameters and a throwable with a level of WARN.
     * 
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param params the message parameters
     * @param t the throwable
     */
    public void warn(String loggerFqcn, Object message, Object[] params, Throwable t) {
        delegate.warn(loggerFqcn, message, params, t);
    }

    /**
     * Issue a log message with a level of WARN using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param format the message format string
     * @param params the parameters
     */
    public void warnv(String format, Object... params) {
        delegate.warnv(format, params);
    }

    /**
     * Issue a log message with a level of WARN using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void warnv(String format, Object param1) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.WARN)) {
            delegate.warnv(format, param1);
        }
    }

    /**
     * Issue a log message with a level of WARN using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void warnv(String format, Object param1, Object param2) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.WARN)) {
            delegate.warnv(format, param1, param2);
        }
    }

    /**
     * Issue a log message with a level of WARN using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void warnv(String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.WARN)) {
            delegate.warnv(format, param1, param2, param3);
        }
    }

    /**
     * Issue a log message with a level of WARN using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param t the throwable
     * @param format the message format string
     * @param params the parameters
     */
    public void warnv(Throwable t, String format, Object... params) {
        delegate.warnv(t, format, params);
    }

    /**
     * Issue a log message with a level of WARN using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param t the throwable
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void warnv(Throwable t, String format, Object param1) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.WARN)) {
            delegate.warnv(t, format, param1);
        }
    }

    /**
     * Issue a log message with a level of WARN using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void warnv(Throwable t, String format, Object param1, Object param2) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.WARN)) {
            delegate.warnv(t, format, param1, param2);
        }
    }

    /**
     * Issue a log message with a level of WARN using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void warnv(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.WARN)) {
            delegate.warnv(t, format, param1, param2, param3);
        }
    }

    /**
     * Issue a formatted log message with a level of WARN.
     * 
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param params the parameters
     */
    public void warnf(String format, Object... params) {
        delegate.warnf(format, params);
    }

    /**
     * Issue a formatted log message with a level of WARN.
     * 
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the sole parameter
     */
    public void warnf(String format, Object param1) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.WARN)) {
            delegate.warnf(format, param1);
        }
    }

    /**
     * Issue a formatted log message with a level of WARN.
     * 
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void warnf(String format, Object param1, Object param2) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.WARN)) {
            delegate.warnf(format, param1, param2);
        }
    }

    /**
     * Issue a formatted log message with a level of WARN.
     * 
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void warnf(String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.WARN)) {
            delegate.warnf(format, param1, param2, param3);
        }
    }

    /**
     * Issue a formatted log message with a level of WARN.
     * 
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param params the parameters
     */
    public void warnf(Throwable t, String format, Object... params) {
        delegate.warnf(t, format, params);
    }

    /**
     * Issue a formatted log message with a level of WARN.
     * 
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the sole parameter
     */
    public void warnf(Throwable t, String format, Object param1) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.WARN)) {
            delegate.warnf(t, format, param1);
        }
    }

    /**
     * Issue a formatted log message with a level of WARN.
     * 
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void warnf(Throwable t, String format, Object param1, Object param2) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.WARN)) {
            delegate.warnf(t, format, param1, param2);
        }
    }

    /**
     * Issue a formatted log message with a level of WARN.
     * 
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void warnf(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.WARN)) {
            delegate.warnf(t, format, param1, param2, param3);
        }
    }

    /**
     * Issue a log message with a level of ERROR.
     * 
     * @param message the message
     */
    public void error(Object message) {
        delegate.error(message);
    }

    /**
     * Issue a log message and throwable with a level of ERROR.
     * 
     * @param message the message
     * @param t the throwable
     */
    public void error(Object message, Throwable t) {
        delegate.error(message, t);
    }

    /**
     * Issue a log message and throwable with a level of ERROR and a specific logger class name.
     * 
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param t the throwable
     */
    public void error(String loggerFqcn, Object message, Throwable t) {
        delegate.error(loggerFqcn, message, t);
    }

    /**
     * Issue a log message with parameters and a throwable with a level of ERROR.
     * 
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param params the message parameters
     * @param t the throwable
     */
    public void error(String loggerFqcn, Object message, Object[] params, Throwable t) {
        delegate.error(loggerFqcn, message, params, t);
    }

    /**
     * Issue a log message with a level of ERROR using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param format the message format string
     * @param params the parameters
     */
    public void errorv(String format, Object... params) {
        delegate.errorv(format, params);
    }

    /**
     * Issue a log message with a level of ERROR using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void errorv(String format, Object param1) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.ERROR)) {
            delegate.errorv(format, param1);
        }
    }

    /**
     * Issue a log message with a level of ERROR using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void errorv(String format, Object param1, Object param2) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.ERROR)) {
            delegate.errorv(format, param1, param2);
        }
    }

    /**
     * Issue a log message with a level of ERROR using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void errorv(String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.ERROR)) {
            delegate.errorv(format, param1, param2, param3);
        }
    }

    /**
     * Issue a log message with a level of ERROR using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param t the throwable
     * @param format the message format string
     * @param params the parameters
     */
    public void errorv(Throwable t, String format, Object... params) {
        delegate.errorv(t, format, params);
    }

    /**
     * Issue a log message with a level of ERROR using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param t the throwable
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void errorv(Throwable t, String format, Object param1) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.ERROR)) {
            delegate.errorv(t, format, param1);
        }
    }

    /**
     * Issue a log message with a level of ERROR using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void errorv(Throwable t, String format, Object param1, Object param2) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.ERROR)) {
            delegate.errorv(t, format, param1, param2);
        }
    }

    /**
     * Issue a log message with a level of ERROR using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void errorv(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.ERROR)) {
            delegate.errorv(t, format, param1, param2, param3);
        }
    }

    /**
     * Issue a formatted log message with a level of ERROR.
     * 
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param params the parameters
     */
    public void errorf(String format, Object... params) {
        delegate.errorf(format, params);
    }

    /**
     * Issue a formatted log message with a level of ERROR.
     * 
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the sole parameter
     */
    public void errorf(String format, Object param1) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.ERROR)) {
            delegate.errorf(format, param1);
        }
    }

    /**
     * Issue a formatted log message with a level of ERROR.
     * 
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void errorf(String format, Object param1, Object param2) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.ERROR)) {
            delegate.errorf(format, param1, param2);
        }
    }

    /**
     * Issue a formatted log message with a level of ERROR.
     * 
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void errorf(String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.ERROR)) {
            delegate.errorf(format, param1, param2, param3);
        }
    }

    /**
     * Issue a formatted log message with a level of ERROR.
     * 
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param params the parameters
     */
    public void errorf(Throwable t, String format, Object... params) {
        delegate.errorf(t, format, params);
    }

    /**
     * Issue a formatted log message with a level of ERROR.
     * 
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the sole parameter
     */
    public void errorf(Throwable t, String format, Object param1) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.ERROR)) {
            delegate.errorf(t, format, param1);
        }
    }

    /**
     * Issue a formatted log message with a level of ERROR.
     * 
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void errorf(Throwable t, String format, Object param1, Object param2) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.ERROR)) {
            delegate.errorf(t, format, param1, param2);
        }
    }

    /**
     * Issue a formatted log message with a level of ERROR.
     * 
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void errorf(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.ERROR)) {
            delegate.errorf(t, format, param1, param2, param3);
        }
    }

    /**
     * Issue a log message with a level of FATAL.
     * 
     * @param message the message
     */
    public void fatal(Object message) {
        delegate.fatal(message);
    }

    /**
     * Issue a log message and throwable with a level of FATAL.
     * 
     * @param message the message
     * @param t the throwable
     */
    public void fatal(Object message, Throwable t) {
        delegate.fatal(message, t);
    }

    /**
     * Issue a log message and throwable with a level of FATAL and a specific logger class name.
     * 
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param t the throwable
     */
    public void fatal(String loggerFqcn, Object message, Throwable t) {
        delegate.fatal(loggerFqcn, message, t);
    }

    /**
     * Issue a log message with parameters and a throwable with a level of FATAL.
     * 
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param params the message parameters
     * @param t the throwable
     */
    public void fatal(String loggerFqcn, Object message, Object[] params, Throwable t) {
        delegate.fatal(loggerFqcn, message, params, t);
    }

    /**
     * Issue a log message with a level of FATAL using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param format the message format string
     * @param params the parameters
     */
    public void fatalv(String format, Object... params) {
        delegate.fatalv(format, params);
    }

    /**
     * Issue a log message with a level of FATAL using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void fatalv(String format, Object param1) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.FATAL)) {
            delegate.fatalv(format, param1);
        }
    }

    /**
     * Issue a log message with a level of FATAL using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void fatalv(String format, Object param1, Object param2) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.FATAL)) {
            delegate.fatalv(format, param1, param2);
        }
    }

    /**
     * Issue a log message with a level of FATAL using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void fatalv(String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.FATAL)) {
            delegate.fatalv(format, param1, param2, param3);
        }
    }

    /**
     * Issue a log message with a level of FATAL using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param t the throwable
     * @param format the message format string
     * @param params the parameters
     */
    public void fatalv(Throwable t, String format, Object... params) {
        delegate.fatalv(t, format, params);
    }

    /**
     * Issue a log message with a level of FATAL using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param t the throwable
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void fatalv(Throwable t, String format, Object param1) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.FATAL)) {
            delegate.fatalv(t, format, param1);
        }
    }

    /**
     * Issue a log message with a level of FATAL using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void fatalv(Throwable t, String format, Object param1, Object param2) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.FATAL)) {
            delegate.fatalv(t, format, param1, param2);
        }
    }

    /**
     * Issue a log message with a level of FATAL using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void fatalv(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.FATAL)) {
            delegate.fatalv(t, format, param1, param2, param3);
        }
    }

    /**
     * Issue a formatted log message with a level of FATAL.
     * 
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param params the parameters
     */
    public void fatalf(String format, Object... params) {
        delegate.fatalf(format, params);
    }

    /**
     * Issue a formatted log message with a level of FATAL.
     * 
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the sole parameter
     */
    public void fatalf(String format, Object param1) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.FATAL)) {
            delegate.fatalf(format, param1);
        }
    }

    /**
     * Issue a formatted log message with a level of FATAL.
     * 
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void fatalf(String format, Object param1, Object param2) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.FATAL)) {
            delegate.fatalf(format, param1, param2);
        }
    }

    /**
     * Issue a formatted log message with a level of FATAL.
     * 
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void fatalf(String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.FATAL)) {
            delegate.fatalf(format, param1, param2, param3);
        }
    }

    /**
     * Issue a formatted log message with a level of FATAL.
     * 
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param params the parameters
     */
    public void fatalf(Throwable t, String format, Object... params) {
        delegate.fatalf(t, format, params);
    }

    /**
     * Issue a formatted log message with a level of FATAL.
     * 
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the sole parameter
     */
    public void fatalf(Throwable t, String format, Object param1) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.FATAL)) {
            delegate.fatalf(t, format, param1);
        }
    }

    /**
     * Issue a formatted log message with a level of FATAL.
     * 
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void fatalf(Throwable t, String format, Object param1, Object param2) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.FATAL)) {
            delegate.fatalf(t, format, param1, param2);
        }
    }

    /**
     * Issue a formatted log message with a level of FATAL.
     * 
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void fatalf(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(org.jboss.solder.logging.internal.Logger.Level.FATAL)) {
            delegate.fatalf(t, format, param1, param2, param3);
        }
    }

    /**
     * Log a message at the given level.
     * 
     * @param level the level
     * @param message the message
     */
    public void log(Level level, Object message) {
        delegate.log(translate(level), message);
    }

    /**
     * Issue a log message and throwable at the given log level.
     * 
     * @param level the level
     * @param message the message
     * @param t the throwable
     */
    public void log(Level level, Object message, Throwable t) {
        delegate.log(translate(level), message, t);
    }

    /**
     * Issue a log message and throwable at the given log level and a specific logger class name.
     * 
     * @param level the level
     * @param loggerFqcn the logger class name
     * @param message the message
     * @param t the throwable
     */
    public void log(Level level, String loggerFqcn, Object message, Throwable t) {
        delegate.log(translate(level), loggerFqcn, message, t);
    }

    /**
     * Issue a log message with parameters and a throwable at the given log level.
     * 
     * @param loggerFqcn the logger class name
     * @param level the level
     * @param message the message
     * @param params the message parameters
     * @param t the throwable
     */
    public void log(String loggerFqcn, Level level, Object message, Object[] params, Throwable t) {
        delegate.log(loggerFqcn, translate(level), message, params, t);
    }

    /**
     * Issue a log message at the given log level using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param level the level
     * @param format the message format string
     * @param params the parameters
     */
    public void logv(Level level, String format, Object... params) {
        delegate.logv(translate(level), format, params);
    }

    /**
     * Issue a log message at the given log level using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param level the level
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void logv(Level level, String format, Object param1) {
        if (delegate.isEnabled(translate(level))) {
            delegate.logv(translate(level), format, param1);
        }
    }

    /**
     * Issue a log message at the given log level using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param level the level
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void logv(Level level, String format, Object param1, Object param2) {
        if (delegate.isEnabled(translate(level))) {
            delegate.logv(translate(level), format, param1, param2);
        }
    }

    /**
     * Issue a log message at the given log level using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param level the level
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void logv(Level level, String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(translate(level))) {
            delegate.logv(translate(level), format, param1, param2, param3);
        }
    }

    /**
     * Issue a log message at the given log level using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param level the level
     * @param t the throwable
     * @param format the message format string
     * @param params the parameters
     */
    public void logv(Level level, Throwable t, String format, Object... params) {
        delegate.logv(translate(level), t, format, params);
    }

    /**
     * Issue a log message at the given log level using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param level the level
     * @param t the throwable
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void logv(Level level, Throwable t, String format, Object param1) {
        if (delegate.isEnabled(translate(level))) {
            delegate.logv(translate(level), t, format, param1);
        }
    }

    /**
     * Issue a log message at the given log level using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param level the level
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void logv(Level level, Throwable t, String format, Object param1, Object param2) {
        if (delegate.isEnabled(translate(level))) {
            delegate.logv(translate(level), t, format, param1, param2);
        }
    }

    /**
     * Issue a log message at the given log level using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param level the level
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void logv(Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(translate(level))) {
            delegate.logv(translate(level), t, format, param1, param2, param3);
        }
    }

    /**
     * Issue a log message at the given log level using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param loggerFqcn the logger class name
     * @param level the level
     * @param t the throwable
     * @param format the message format string
     * @param params the parameters
     */
    public void logv(String loggerFqcn, Level level, Throwable t, String format, Object... params) {
        delegate.logv(loggerFqcn, translate(level), t, format, params);
    }

    /**
     * Issue a log message at the given log level using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param loggerFqcn the logger class name
     * @param level the level
     * @param t the throwable
     * @param format the message format string
     * @param param1 the sole parameter
     */
    public void logv(String loggerFqcn, Level level, Throwable t, String format, Object param1) {
        if (delegate.isEnabled(translate(level))) {
            delegate.logv(loggerFqcn, translate(level), t, format, param1);
        }
    }

    /**
     * Issue a log message at the given log level using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param loggerFqcn the logger class name
     * @param level the level
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void logv(String loggerFqcn, Level level, Throwable t, String format, Object param1, Object param2) {
        if (delegate.isEnabled(translate(level))) {
            delegate.logv(loggerFqcn, translate(level), t, format, param1, param2);
        }
    }

    /**
     * Issue a log message at the given log level using {@link java.text.MessageFormat}-style formatting.
     * 
     * @param loggerFqcn the logger class name
     * @param level the level
     * @param t the throwable
     * @param format the message format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void logv(String loggerFqcn, Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(translate(level))) {
            delegate.logv(loggerFqcn, translate(level), t, format, param1, param2, param3);
        }
    }

    /**
     * Issue a formatted log message at the given log level.
     * 
     * @param level the level
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param params the parameters
     */
    public void logf(Level level, String format, Object... params) {
        delegate.logf(translate(level), format, params);
    }

    /**
     * Issue a formatted log message at the given log level.
     * 
     * @param level the level
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the sole parameter
     */
    public void logf(Level level, String format, Object param1) {
        if (delegate.isEnabled(translate(level))) {
            delegate.logf(translate(level), format, param1);
        }
    }

    /**
     * Issue a formatted log message at the given log level.
     * 
     * @param level the level
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void logf(Level level, String format, Object param1, Object param2) {
        if (delegate.isEnabled(translate(level))) {
            delegate.logf(translate(level), format, param1, param2);
        }
    }

    /**
     * Issue a formatted log message at the given log level.
     * 
     * @param level the level
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void logf(Level level, String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(translate(level))) {
            delegate.logf(translate(level), format, param1, param2, param3);
        }
    }

    /**
     * Issue a formatted log message at the given log level.
     * 
     * @param level the level
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param params the parameters
     */
    public void logf(Level level, Throwable t, String format, Object... params) {
        delegate.logf(translate(level), t, format, params);
    }

    /**
     * Issue a formatted log message at the given log level.
     * 
     * @param level the level
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the sole parameter
     */
    public void logf(Level level, Throwable t, String format, Object param1) {
        if (delegate.isEnabled(translate(level))) {
            delegate.logf(translate(level), t, format, param1);
        }
    }

    /**
     * Issue a formatted log message at the given log level.
     * 
     * @param level the level
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void logf(Level level, Throwable t, String format, Object param1, Object param2) {
        if (delegate.isEnabled(translate(level))) {
            delegate.logf(translate(level), t, format, param1, param2);
        }
    }

    /**
     * Issue a formatted log message at the given log level.
     * 
     * @param level the level
     * @param t the throwable
     * @param format the format string, as per {@link String#format(String, Object...)}
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void logf(Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(translate(level))) {
            delegate.logf(translate(level), t, format, param1, param2, param3);
        }
    }

    /**
     * Log a message at the given level.
     * 
     * @param loggerFqcn the logger class name
     * @param level the level
     * @param t the throwable cause
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the sole parameter
     */
    public void logf(String loggerFqcn, Level level, Throwable t, String format, Object param1) {
        if (delegate.isEnabled(translate(level))) {
            delegate.logf(loggerFqcn, translate(level), t, format, param1);
        }
    }

    /**
     * Log a message at the given level.
     * 
     * @param loggerFqcn the logger class name
     * @param level the level
     * @param t the throwable cause
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    public void logf(String loggerFqcn, Level level, Throwable t, String format, Object param1, Object param2) {
        if (delegate.isEnabled(translate(level))) {
            delegate.logf(loggerFqcn, translate(level), t, format, param1, param2);
        }
    }

    /**
     * Log a message at the given level.
     * 
     * @param loggerFqcn the logger class name
     * @param level the level
     * @param t the throwable cause
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param param1 the first parameter
     * @param param2 the second parameter
     * @param param3 the third parameter
     */
    public void logf(String loggerFqcn, Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
        if (delegate.isEnabled(translate(level))) {
            delegate.logf(loggerFqcn, translate(level), t, format, param1, param2, param3);
        }
    }

    /**
     * Log a message at the given level.
     * 
     * @param loggerFqcn the logger class name
     * @param level the level
     * @param t the throwable cause
     * @param format the format string as per {@link String#format(String, Object...)} or resource bundle key therefor
     * @param params the message parameters
     */
    public void logf(String loggerFqcn, Level level, Throwable t, String format, Object... params) {
        delegate.logf(loggerFqcn, translate(level), t, format, params);
    }

    /**
     * Get a Logger instance given the logger name.
     * 
     * @param name the logger name
     * 
     * @return the logger
     */
    public static Logger getLogger(String name) {
        return new Logger(name);
    }

    /**
     * Get a Logger instance given the logger name with the given suffix.
     * <p/>
     * <p>
     * This will include a logger separator between logger name and suffix.
     * 
     * @param name the logger name
     * @param suffix a suffix to append to the logger name
     * 
     * @return the logger
     */
    public static Logger getLogger(String name, String suffix) {
        return getLogger(name == null || name.length() == 0 ? suffix : name + "." + suffix);
    }

    /**
     * Get a Logger instance given the name of a class. This simply calls create(clazz.getName()).
     * 
     * @param clazz the Class whose name will be used as the logger name
     * 
     * @return the logger
     */
    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    /**
     * Get a Logger instance given the name of a class with the given suffix.
     * <p/>
     * <p>
     * This will include a logger separator between logger name and suffix
     * 
     * @param clazz the Class whose name will be used as the logger name
     * @param suffix a suffix to append to the logger name
     * 
     * @return the logger
     */
    public static Logger getLogger(Class<?> clazz, String suffix) {
        return getLogger(clazz.getName(), suffix);
    }

    /**
     * Get a typed logger which implements the given interface. The current default locale will be used for the new logger.
     * 
     * @param type the interface to implement
     * @param category the logger category
     * @param <T> the logger type
     * @return the typed logger
     */
    public static <T> T getMessageLogger(Class<T> type, String category) {
        return getMessageLogger(type, category, Locale.getDefault());
    }

    /**
     * Get a typed logger which implements the given interface. The given locale will be used for the new logger.
     * 
     * @param type the interface to implement
     * @param category the logger category
     * @param locale the locale for the new logger
     * @param <T> the logger type
     * @return the typed logger
     */
    public static <T> T getMessageLogger(Class<T> type, String category, Locale locale) {
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();

        Class<? extends T> loggerClass = null;
        if (variant != null && variant.length() > 0)
            try {
                loggerClass = Class.forName(join(type.getName(), "$logger", language, country, variant), true,
                        type.getClassLoader()).asSubclass(type);
            } catch (ClassNotFoundException e) {
                // ignore
            }
        if (loggerClass == null && country != null && country.length() > 0)
            try {
                loggerClass = Class.forName(join(type.getName(), "$logger", language, country, null), true,
                        type.getClassLoader()).asSubclass(type);
            } catch (ClassNotFoundException e) {
                // ignore
            }
        if (loggerClass == null && language != null && language.length() > 0)
            try {
                loggerClass = Class.forName(join(type.getName(), "$logger", language, null, null), true, type.getClassLoader())
                        .asSubclass(type);
            } catch (ClassNotFoundException e) {
                // ignore
            }
        if (loggerClass == null)
            try {
                loggerClass = Class.forName(join(type.getName(), "$logger", null, null, null), true, type.getClassLoader())
                        .asSubclass(type);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Invalid logger " + type + " (implementation not found)");
            }
        final Constructor<? extends T> constructor;
        try {
            constructor = loggerClass.getConstructor(Logger.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Logger implementation " + loggerClass + " has no matching constructor");
        }
        try {
            return constructor.newInstance(Logger.getLogger(category));
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Logger implementation " + loggerClass + " could not be instantiated", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Logger implementation " + loggerClass + " could not be instantiated", e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Logger implementation " + loggerClass + " could not be instantiated",
                    e.getCause());
        }
    }

    private static String join(String interfaceName, String a, String b, String c, String d) {
        final StringBuilder build = new StringBuilder();
        build.append(interfaceName).append('_').append(a);
        if (b != null && b.length() > 0) {
            build.append('_');
            build.append(b);
        }
        if (c != null && c.length() > 0) {
            build.append('_');
            build.append(c);
        }
        if (d != null && d.length() > 0) {
            build.append('_');
            build.append(d);
        }
        return build.toString();
    }

    private static org.jboss.solder.logging.internal.Logger.Level translate(final Level level) {
        if (level != null)
            switch (level) {
                case FATAL:
                    return org.jboss.solder.logging.internal.Logger.Level.FATAL;
                case ERROR:
                    return org.jboss.solder.logging.internal.Logger.Level.ERROR;
                case WARN:
                    return org.jboss.solder.logging.internal.Logger.Level.WARN;
                case INFO:
                    return org.jboss.solder.logging.internal.Logger.Level.INFO;
                case DEBUG:
                    return org.jboss.solder.logging.internal.Logger.Level.DEBUG;
                case TRACE:
                    return org.jboss.solder.logging.internal.Logger.Level.TRACE;
            }
        return null;
    }

}