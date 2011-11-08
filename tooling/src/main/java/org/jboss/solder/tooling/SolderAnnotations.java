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
package org.jboss.solder.tooling;

import java.lang.annotation.Annotation;

import org.jboss.logging.generator.Annotations;
import org.jboss.solder.logging.Log;
import org.jboss.solder.logging.LoggingClass;
import org.jboss.solder.logging.MessageLogger;
import org.jboss.solder.messages.Cause;
import org.jboss.solder.messages.Field;
import org.jboss.solder.messages.Formatter;
import org.jboss.solder.messages.Message;
import org.jboss.solder.messages.MessageBundle;
import org.jboss.solder.messages.Param;
import org.jboss.solder.messages.Property;

/**
 * @author James R. Perkins (jrp) - 19.Feb.2011
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class SolderAnnotations implements Annotations {

    public static final Class<Cause> CAUSE_ANNOTATION = Cause.class;
    public static final Class<Field> FIELD_ANNOTATION = Field.class;
    public static final Class<Formatter> FORMAT_WITH_ANNOTATION = Formatter.class;
    public static final Class<LoggingClass> LOGGER_CLASS_ANNOTATION = LoggingClass.class;
    public static final Class<Log> LOG_MESSAGE_ANNOTATION = Log.class;
    public static final Class<MessageBundle> MESSAGE_BUNDLE_ANNOTATION = MessageBundle.class;
    public static final Class<MessageLogger> MESSAGE_LOGGER_ANNOTATION = MessageLogger.class;
    public static final Class<Message> MESSAGE_ANNOTATION = Message.class;
    public static final Class<Param> PARAM_ANNOTATION = Param.class;
    public static final Class<Property> PROPERTY_ANNOTATION = Property.class;

    @Override
    public Class<? extends Annotation> cause() {
        return CAUSE_ANNOTATION;
    }

    @Override
    public Class<? extends Annotation> field() {
        return FIELD_ANNOTATION;
    }

    @Override
    public Class<? extends Annotation> formatWith() {
        return FORMAT_WITH_ANNOTATION;
    }

    //@Override
    public Class<? extends Annotation> loggingClass() {
        return LOGGER_CLASS_ANNOTATION;
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
    public Class<? extends Annotation> param() {
        return PARAM_ANNOTATION;
    }

    @Override
    public Class<? extends Annotation> property() {
        return PROPERTY_ANNOTATION;
    }
}
