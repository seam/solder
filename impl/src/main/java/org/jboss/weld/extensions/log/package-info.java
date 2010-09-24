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
 * <p>
 * Weld Extensions integrates JBoss Logging 3 as it's logging framework of choice. 
 * JBoss Logging 3 is a modern logging framework offering:
 * </p>
 * 
 * <p>
 * <em>A number of the features of JBoss Logging 3 are still under development - at the moment only
 * runtime generation of typed is supported, and these loggers only support the default message
 * placed on the typed logger, and will not look up a localized message.</em>
 * </p>
 * 
 * <ul>
 * <li>Abstracts away from common logging backends and frameworks (such as JDK Logging, log4j and slf4j)</li>
 * <li>Provides a innovative, typed logger (see below for examples)</li>
 * <li>Full support for internationalization and localization
 *   <ul>
 *   <li>Developers can work with interfaces and annotations only</li>
 *   <li>Translators can work with message bundles in properties files</li>
 *   </ul>
 * <li>Build time tooling to generate typed loggers for production, and runtime generation of typed 
 * loggers for development</li>
 * <li>Access to MDC and NDC (if underlying logger supports it)
 * <li>Loggers are serializable</li>
 * </li>
 * </ul>
 * 
 * <p>To use a typed logger, first create the logger definition:</p>
 * 
 * <pre>
 * &#64;MessageLogger
 * interface TrainSpotterLog {
 *
 *    // Define log call with message, using printf-style interpolation of parameters
 *    &#64;LogMessage &#64;Message("Spotted %s diesel trains") 
 *    void dieselTrainsSpotted(int number);
 *
 * }
 * </pre>
 * 
 * <p>You can then inject the typed logger with no further configuration:</p>
 * 
 * <pre>
 *    // Use the train spotter log, with the log category "trains"
 *    &#64;Inject &#64;Category("trains") TrainSpotterLog log;
 * </pre>
 * 
 * <p>and use it:</p>
 * 
 * <pre>
 *    log.dieselTrainsSpotted(7);
 * </pre>
 * 
 * <p>JBoss Logging will use the default locale unless overridden:</p>
 * 
 * <pre>
 *    // Use the train spotter log, with the log category "trains", and select the UK locale
 *    &#64;Inject &#64;Category("trains") &#64;Locale("en_GB") TrainSpotterLog log;
 * </pre>
 * 
 * <p>You can also log exceptions:</p>
 * 
 * <pre>
 * &#64;MessageLogger
 * interface TrainSpotterLog {
 *
 *    // Define log call with message, using printf-style interpolation of parameters
 *    // The exception parameter will be logged as an exception
 *    &#64;LogMessage &#64;Message("Failed to spot train %s") 
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
 * You can also inject a "plain old" Logger:
 * </p>
 * 
 * <pre>
 *    &#64;Inject Logger log;
 * </pre>
 * 
 * <p>
 * Typed loggers also provide internationalization support, simply add the &#64;MessageBundle annotation to
 * the logger interface (not currently supported).
 * </p>
 * 
 * <p>
 * Sometimes you need to access the message directly (for example to localize an exception
 * message). Weld Extensions let's you inject a typed message bundle. First, declare the message
 * bundle:
 * </p>
 * 
 * <pre>
 * &#64;MessageBundle
 * interface TrainMessages {
 *
 *    // Define a message using printf-style interpolation of parameters
 *    &#64;Message("No trains spotted due to %s") 
 *    String noTrainsSpotted(String cause);
 *
 * }
 * </pre>
 * 
 * <p>
 * Inject it:
 * </p>
 * 
 * <pre>
 *    &#64;Inject &#64;MessageBundle TrainMessages messages;
 * </pre>
 * 
 * <p>
 * And use it:
 * </p>
 * 
 * <pre>
 *   throw new BadDayException(messages.noTrainsSpotted("leaves on the line"));
 * </pre>
 * 
 */
package org.jboss.weld.extensions.log;

