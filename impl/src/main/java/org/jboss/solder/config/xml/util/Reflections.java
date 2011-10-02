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

import java.lang.reflect.Field;

public class Reflections {
    /**
     * returns the field or null if not found
     */
    public static Field getField(Class<?> c, String name) {
        Class<?> i = c;
        while (i != Object.class && i != null) {
            try {
                return i.getDeclaredField(name);
            } catch (Exception e) {

            }
            i = i.getSuperclass();
        }
        return null;
    }
}
