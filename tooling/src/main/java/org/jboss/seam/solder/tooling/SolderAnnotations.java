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
package org.jboss.seam.solder.tooling;

import java.lang.annotation.Annotation;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.jboss.logging.Annotations;
import org.jboss.logging.Logger;
import org.jboss.seam.solder.logging.Log;
import org.jboss.seam.solder.logging.MessageLogger;
import org.jboss.seam.solder.messages.Cause;
import org.jboss.seam.solder.messages.Formatter;
import org.jboss.seam.solder.messages.Message;
import org.jboss.seam.solder.messages.MessageBundle;

/**
 *
 * @author James R. Perkins (jrp) - 19.Feb.2011
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class SolderAnnotations implements Annotations {

    public static final Class<Formatter> FORMAT_WITH_ANNOTATION = Formatter.class;
    public static final Class<Cause> CAUSE_ANNOTATION = Cause.class;
    public static final Class<MessageBundle> MESSAGE_BUNDLE_ANNOTATION = MessageBundle.class;
    public static final Class<MessageLogger> MESSAGE_LOGGER_ANNOTATION = MessageLogger.class;
    public static final Class<Log> LOG_MESSAGE_ANNOTATION = Log.class;
    public static final Class<Message> MESSAGE_ANNOTATION = Message.class;

    @Override
    public Class<? extends Annotation> cause() {
        return CAUSE_ANNOTATION;
    }

    @Override
    public Class<? extends Annotation> formatWith() {
        return FORMAT_WITH_ANNOTATION;
    }

    @Override
    public Class<? extends Annotation> logMessage() {
        return LOG_MESSAGE_ANNOTATION;
    }

    @Override
    public Class<? extends Annotation> message() {
        return MESSAGE_ANNOTATION;
    }

    @Override
    public Class<? extends Annotation> messageBundle() {
        return MESSAGE_BUNDLE_ANNOTATION;
    }

    @Override
    public Class<? extends Annotation> messageLogger() {
        return MESSAGE_LOGGER_ANNOTATION;
    }

    @Override
    public FormatType messageFormat(final ExecutableElement method) {
        FormatType result = null;
        final Message message = method.getAnnotation(MESSAGE_ANNOTATION);
        if (message != null) {
            switch (message.format()) {
                case MESSAGE_FORMAT:
                    result = FormatType.MESSAGE_FORMAT;
                    break;
                case PRINTF:
                    result = FormatType.PRINTF;
                    break;
            }
        }
        return result;
    }

    @Override
    public String projectCode(final TypeElement intf) {
        String result = null;
        final MessageBundle bundle = intf.getAnnotation(MESSAGE_BUNDLE_ANNOTATION);
        final MessageLogger logger = intf.getAnnotation(MESSAGE_LOGGER_ANNOTATION);
        if (bundle != null) {
            result = bundle.projectCode();
        } else if (logger != null) {
            result = logger.projectCode();
        }
        return result;
    }

    @Override
    public boolean hasMessageId(final ExecutableElement method) {
        final Message message = method.getAnnotation(MESSAGE_ANNOTATION);
        return (message == null ? false : (message.id() > Message.NONE));
    }

    @Override
    public int messageId(final ExecutableElement method) {
        final Message message = method.getAnnotation(MESSAGE_ANNOTATION);
        return (message == null ? Message.NONE : message.id());
    }

    @Override
    public String messageValue(final ExecutableElement method) {
        final Message message = method.getAnnotation(MESSAGE_ANNOTATION);
        return (message == null ? null : message.value());
    }

    @Override
    public String loggerMethod(final ExecutableElement method, final FormatType formatType) {
        String result = null;
        final Log logMessage = method.getAnnotation(LOG_MESSAGE_ANNOTATION);
        if (logMessage != null) {
            final Logger.Level logLevel = (logMessage.level() == null ? Logger.Level.INFO : logMessage.level());
            result = String.format("%s%c", logLevel.name().toLowerCase(), formatType.logType());
        }
        return result;
    }
}
