/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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

/**
 * Solder integrates with JBoss Logging 3 to provide injectable native
 * loggers or typed message loggers (suitable for internationalization and
 * localization) while still offering a choice of logging backend
 *
 * <p>Solder builds on its typed message bundles support combined with JBoss
 * Logging 3 to provide the following feature set:</p>
 *
 * <ul>
 * <li>An abstraction over common logging backends and frameworks (such as JDK Logging, log4j and slf4j)</li>
 * <li>An innovative, typed message logger (and bundle) defined using an interface (see below for examples)</li>
 * <li>Full support for internationalization and localization</li>
 * <li>Build time tooling to generate typed loggers for production, and runtime generation of typed loggers for development</li>
 * <li>Access to MDC and NDC (if underlying logger supports it)</li>
 * <li>Serializable loggers</li>
 * </ul>
 *
 * <p>To define a typed message logger, first create an annotated interface
 * with methods configured as log commands. The log messages to use
 * printf-style interpolations of parameters (%s).</p>
 *
 * <pre>
 * &#64;MessageLogger
 * public interface TrainSpotterLog {
 *
 *    &#64;Log &#64;Message("Spotted %s diesel trains")
 *    void dieselTrainsSpotted(int number);
 *
 * }
 * </pre>
 *
 * <p>You can then inject the typed logger with no further configuration
 * necessary. You use another annotation to set the category of the logger to
 * "trains" at the injection point:</p>
 *
 * <pre>
 *    &#64;Inject &#64;Category("trains") TrainSpotterLog log;
 * </pre>
 *
 * <p>You log a message by simply invoking a method of the message logger
 * interface:</p>
 *
 * <pre>
 *    log.dieselTrainsSpotted(7);
 * </pre>
 *
 * <p>The default locale will be used unless overridden. Here we configure the
 * logger to use the UK locale.</p>
 *
 * <pre>
 *    &#64;Inject &#64;Category("trains") &#64;Locale("en_GB") TrainSpotterLog log;
 * </pre>
 *
 * <p>You can also log exceptions:</p>
 *
 * <pre>
 * &#64;MessageLogger
 * public interface TrainSpotterLog {
 *
 *    &#64;Log &#64;Message("Failed to spot train %s")
 *    void missedTrain(String trainNumber, &#64;Cause Exception exception);
 *
 * }
 * </pre>
 *
 * <p>You can then log a message with exception:</p>
 *
 * <pre>
 *    log.missedTrain("RH1", cause);
 * </pre>
 *
 * <p>
 * You can also inject a native Logger from the JBoss Logging 3 API:
 * </p>
 *
 * <pre>
 *    &#64;Inject Logger log;
 * </pre>
 *
 * @see org.jboss.solder.logging.Log
 * @see org.jboss.solder.logging.Category
 * @see org.jboss.solder.logging.TypedCategory
 * @see org.jboss.solder.logging..Suffix
 * @see org.jboss.solder.messages.Cause
 * @see org.jboss.solder.messages.Message
 * @see org.jboss.solder.messages.Locale
 * @see org.jboss.solder.messages.Formatter
 */
package org.jboss.solder.logging;
