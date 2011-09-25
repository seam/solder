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
package org.jboss.solder.config.xml.model;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.solder.config.xml.util.XmlConfigurationException;

public class ArrayXmlItem extends ParameterXmlItem {

    Class<?> javaClass = null;

    int dimensions = 1;

    public ArrayXmlItem(XmlItem parent, Map<String, String> attributes, String document, int lineno) {
        super(parent, null, document, lineno);
        if (attributes.containsKey("dimensions")) {
            try {
                dimensions = Integer.parseInt(attributes.get("dimensions"));
            } catch (NumberFormatException e) {
                throw new XmlConfigurationException("dimensions attribute on <array> must be an integer", document, lineno);
            }
        }
    }

    public boolean resolveChildren(BeanManager manager) {
        List<ClassXmlItem> classXmlItems = getChildrenOfType(ClassXmlItem.class);
        if (classXmlItems.isEmpty()) {
            throw new XmlConfigurationException("<array>  element must have a child specifying the array type", getDocument(), getLineno());
        } else if (classXmlItems.size() != 1) {
            throw new XmlConfigurationException("<array>  element must have a single child specifying the array type", getDocument(), getLineno());
        }
        int[] dims = new int[dimensions];
        for (int i = 0; i < dimensions; ++i) {
            dims[i] = 0;
        }
        Class<?> l = classXmlItems.get(0).getJavaClass();
        javaClass = Array.newInstance(l, dims).getClass();

        return true;
    }

    @Override
    public Class<?> getJavaClass() {
        return javaClass;
    }

}
