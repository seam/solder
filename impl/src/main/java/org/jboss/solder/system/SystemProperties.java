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
package org.jboss.solder.system;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.jboss.solder.core.System;

/**
 * Access to map of system {@link Properties}.
 * 
 * @author <a href="mailto:ssachtleben@gmail.com">Sebastian Sachtleben</a>
 */
@Named("sysProp")
public class SystemProperties implements Map<Object, Object>, Serializable {

    private static final long serialVersionUID = -6998532426617911086L;

    private Properties properties = java.lang.System.getProperties();

    @Produces
    @System
    private Properties getSystemProperties() {
        return properties;
    }

    @Override
    public int size() {
        return getSystemProperties().size();
    }

    @Override
    public boolean isEmpty() {
        return getSystemProperties().isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return getSystemProperties().containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return getSystemProperties().containsValue(value);
    }

    @Override
    public String get(final Object key) {
        return getSystemProperties().getProperty(key.toString());
    }

    @Override
    public Object put(final Object key, final Object value) {
        return getSystemProperties().put(key, value);
    }

    @Override
    public String remove(final Object key) {
        return getSystemProperties().remove(key).toString();
    }

    @Override
    public void putAll(final Map<? extends Object, ? extends Object> m) {
        getSystemProperties().putAll(m);
    }

    @Override
    public void clear() {
        getSystemProperties().clear();
    }

    @Override
    public Set<Object> keySet() {
        return getSystemProperties().keySet();
    }

    @Override
    public Collection<Object> values() {
        return getSystemProperties().values();
    }

    @Override
    public Set<java.util.Map.Entry<Object, Object>> entrySet() {
        return getSystemProperties().entrySet();
    }

}
