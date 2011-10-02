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
package org.jboss.solder.exception.control;

/**
 * Base class for setting up messages and responses for exceptions. This class is mutable.
 */
public class ExceptionResponse {
    private Class<? extends Throwable> forType;
    private String message;

    /**
     * Basic constructor, needed to make the class a bean, please don't use.
     */
    public ExceptionResponse() {
    }

    /**
     * Basic constructor setting all the internal state
     * @param forType Exception type for this instance.
     * @param message Message for the exception.
     */
    public ExceptionResponse(Class<? extends Throwable> forType, String message) {
        this.forType = forType;
        this.message = message;
    }

    public Class<? extends Throwable> getForType() {
        return forType;
    }

    public void setForType(Class<? extends Throwable> forType) {
        this.forType = forType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
