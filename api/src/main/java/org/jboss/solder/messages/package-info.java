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
 * Solder provides injectable typed message bundles (suitable for
 * internationalization and localization)
 *
 * <p>
 * Often times you need to access a localized message. For example, you need to
 * localize an exception message.  Solder let's you retrieve this message
 * from a typed message logger to avoid having to use hard-coded string
 * messages.
 * </p>
 *
 * <p>
 * First, declare the message bundle as an annotated interface with methods
 * configured as message accessors. You can configured the messages to use
 * printf-style interpolations of parameters (%s).
 * </p>
 *
 * <pre>
 * &#64;MessageBundle
 * public interface TrainMessages {
 *
 *    &#64;Message("No trains spotted due to %s")
 *    String noTrainsSpotted(String cause);
 *
 * }
 * </pre>
 *
 * <p>
 * Now inject the interface:
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
 * @see org.jboss.solder.messages.Message
 * @see org.jboss.solder.messages.MessageBundle
 * @see org.jboss.solder.messages.Locale
 */
package org.jboss.solder.messages;

