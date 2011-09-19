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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.jboss.solder.messages.Message;
import org.jboss.solder.messages.MessageBundleInvocationHandler;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public class MessageLoggerInvocationHandler extends MessageBundleInvocationHandler {

    private final Logger logger;

    public MessageLoggerInvocationHandler(final Class<?> type, final String category) {
        this(type.getAnnotation(MessageLogger.class), category);
    }

    private MessageLoggerInvocationHandler(final MessageLogger messageLogger, final String category) {
        super(messageLogger.projectCode());
        logger = Logger.getLogger(category);
    }

    public Object invoke(final Object proxy, final Method method, Object[] args) throws Throwable {
        final Message message = method.getAnnotation(Message.class);
        if (message == null) {
            // nothing to do...
            return null;
        }
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        final Log logMessage = method.getAnnotation(Log.class);
        if (logMessage != null) {

            try {
                // See if it's a basic logger method
                if (method.getDeclaringClass().equals(org.jboss.solder.logging.internal.BasicLogger.class)) {
                    // doesn't cover overrides though!
                    return method.invoke(logger, args);
                }

                // it's a log message
                final Logger.Level level = logMessage.level();
                if (logger.isEnabled(level)) {
                    String formatString = getFormatString(message);
                    if (formatString == null) {
                        return null;
                    }
                    ArrayList<Object> newArgs = new ArrayList<Object>();
                    Throwable cause = extractCause(parameterAnnotations, args, newArgs);
                    final Message.Format format = message.format();
                    switch (format) {
                        case PRINTF: {
                            logger.logf(level, cause, formatString, newArgs.toArray());
                            return null;
                        }
                        case MESSAGE_FORMAT: {
                            logger.logv(level, cause, formatString, newArgs.toArray());
                            return null;
                        }
                        default: {
                            return null;
                        }
                    }
                }
            } catch (Throwable ignored) {
            }
        } else {
            return super.invoke(proxy, method, args);
        }
        return null;
    }
}
