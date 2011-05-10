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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.Loggers;

/**
 * @author James R. Perkins (jrp) - 20.Feb.2011
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public class SolderLoggers implements Loggers {

    @Override
    public Class<?> loggerClass() {
        return Logger.class;
    }

    @Override
    public Class<?> basicLoggerClass() {
        return BasicLogger.class;
    }

    @Override
    public List<Method> basicLoggerMethods() {
        return Arrays.asList(basicLoggerClass().getMethods());
    }

}
