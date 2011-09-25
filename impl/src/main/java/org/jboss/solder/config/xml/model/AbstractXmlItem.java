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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.spi.BeanManager;

public abstract class AbstractXmlItem implements XmlItem {
    protected final XmlItemType type;
    protected final XmlItem parent;
    protected final Class<?> javaClass;

    protected final String innerText;
    protected final Map<String, String> attributes;

    protected final int lineno;

    protected final String document;

    public String getInnerText() {
        return innerText;
    }

    public AbstractXmlItem(XmlItemType type, XmlItem parent, Class<?> javaClass, String innerText, Map<String, String> attributes, String document, int lineno) {
        this.type = type;
        this.parent = parent;
        this.javaClass = javaClass;
        this.innerText = innerText;
        if (attributes == null) {
            this.attributes = new HashMap<String, String>();
        } else {
            this.attributes = new HashMap<String, String>(attributes);
        }
        this.lineno = lineno;
        this.document = document;
    }

    public int getLineno() {
        return lineno;
    }

    public String getDocument() {
        return document;
    }

    final List<XmlItem> children = new ArrayList<XmlItem>();

    public void addChild(XmlItem xmlItem) {
        children.add(xmlItem);
    }

    public XmlItem getParent() {
        return parent;
    }

    public List<XmlItem> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public XmlItemType getType() {
        return type;
    }

    public Class<?> getJavaClass() {
        return javaClass;
    }

    public boolean resolveChildren(BeanManager manager) {
        return true;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public <T> List<T> getChildrenOfType(Class<T> type) {
        List<T> ret = new ArrayList<T>();
        for (XmlItem i : children) {
            if (type.isAssignableFrom(i.getClass())) {
                ret.add((T) i);
            }
        }
        return ret;
    }
}
