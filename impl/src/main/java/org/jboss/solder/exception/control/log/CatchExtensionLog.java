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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Set;

import org.jboss.solder.exception.control.HandlerMethod;
import org.jboss.solder.exception.control.TraversalMode;
import org.jboss.solder.logging.Log;
import org.jboss.solder.logging.Logger;
import org.jboss.solder.logging.MessageLogger;
import org.jboss.solder.messages.Message;

/**
 * @author <a href="http://community.jboss.org/people/LightGuard">Jason Porter</a>
 */
@MessageLogger
public interface CatchExtensionLog {

    @Log @Message("Adding handler %s to known handlers")
    void addingHandler(HandlerMethod<?> handler);

    @Log(level = Logger.Level.TRACE) @Message("Found handlers %s for exception type %s, qualifiers %s, traversal mode %s")
    void foundHandlers(Collection<HandlerMethod<? extends Throwable>> handlers, Type exceptionType, Set<Annotation> qualifiers,
                       TraversalMode traversalMode);
}
