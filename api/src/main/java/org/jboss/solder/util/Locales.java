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
package org.jboss.solder.util;

import java.util.Locale;

/**
 * Utilities for working with locales.
 *
 * @author Pete Muir
 */
public class Locales {

    private Locales() {
    }

    /**
     * Utility to convert a string using the standard format for specifying a
     * Locale to a {@link Locale} object.
     *
     * @param localeName the string providing the locale.
     * @return the encoded {@link Locale}
     */
    public static Locale toLocale(String localeName) {
        if (localeName == null) {
            return Locale.getDefault();
        }
        if (localeName.contains("_")) {
            String[] split = localeName.split("_");
            if (split.length == 2) {
                return new Locale(split[0], split[1]);
            } else if (split.length == 3) {
                return new Locale(split[0], split[1], split[2]);
            }
        }
        return new Locale(localeName);
    }

}
