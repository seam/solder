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
package org.jboss.solder.config.xml.util;

public class XmlConfigurationException extends RuntimeException {
    int lineno;
    String document;

    public XmlConfigurationException(String message, String document, int lineno) {
        super(message);
        this.document = document;
        this.lineno = lineno;
    }

    public XmlConfigurationException(String message, String document, int lineno, Throwable cause) {
        super(message, cause);
        this.document = document;
        this.lineno = lineno;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " at " + document + ":" + lineno;
    }
}
