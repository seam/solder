/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.solder.servlet.support;

import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.solder.messages.Message;
import org.jboss.solder.messages.MessageBundle;

/**
 * Type-safe exception messages for the Solder Servlet module
 *
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@MessageBundle
public interface ServletMessages {
    @Message("Additional qualifiers not permitted at @%s injection point: %s")
    String additionalQualifiersNotPermitted(String injectionPointName, InjectionPoint ip);

    @Message("@%s injection point must be a raw type: %s")
    String rawTypeRequired(String injectionPointName, InjectionPoint ip);

    @Message("No converter available for type at @%s injection point: %s")
    String noConverterForType(String injectionPointName, InjectionPoint ip);
}
