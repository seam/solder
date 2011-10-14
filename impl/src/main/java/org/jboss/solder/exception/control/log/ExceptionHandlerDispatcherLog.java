/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the &quot;License&quot;);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.solder.exception.control.log;

import org.jboss.solder.exception.control.HandlerMethod;
import org.jboss.solder.logging.Log;
import org.jboss.solder.logging.Logger;
import org.jboss.solder.logging.MessageLogger;
import org.jboss.solder.messages.Message;

/**
 * @author <a href="http://community.jboss.org/people/LightGuard">Jason Porter</a>
 */
@MessageLogger
public interface ExceptionHandlerDispatcherLog {

    @Log(level = Logger.Level.TRACE) @Message("Starting exception handling for exception %s")
    void enteringExceptionHandlerDispatcher(Throwable exceptionCaught);

    @Log(level = Logger.Level.DEBUG) @Message("Notifying handler %s")
    void notifyingHandler(HandlerMethod<?> eh);

    @Log(level = Logger.Level.DEBUG) @Message("Handler %s returned status %s")
    void returnFromHandler(HandlerMethod<?> handler, String status);

    @Log(level = Logger.Level.TRACE) @Message("Ending exception handling for exception %s")
    void endingExceptionHandlerDispatcher(Throwable exceptionCaught);

    @Log(level = Logger.Level.WARN) @Message("No handlers found for exception %s")
    void noHandlersFound(Throwable e);
}
