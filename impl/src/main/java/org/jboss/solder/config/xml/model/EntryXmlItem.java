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

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.solder.config.xml.core.BeanResult;
import org.jboss.solder.config.xml.util.TypeOccuranceInformation;
import org.jboss.solder.config.xml.util.XmlConfigurationException;

public class EntryXmlItem extends AbstractXmlItem {

    final Set<TypeOccuranceInformation> allowed = new HashSet<TypeOccuranceInformation>();

    KeyXmlItem key;
    ValueXmlItem value;

    public EntryXmlItem(XmlItem parent, String document, int lineno) {
        super(XmlItemType.ENTRY, parent, null, null, null, document, lineno);
        allowed.add(TypeOccuranceInformation.of(XmlItemType.VALUE, 1, 1));
        allowed.add(TypeOccuranceInformation.of(XmlItemType.KEY, 1, 1));
    }

    public Set<TypeOccuranceInformation> getAllowedItem() {
        return allowed;
    }

    @Override
    public boolean resolveChildren(BeanManager manager) {
        if (children.size() != 2) {
            throw new XmlConfigurationException("<entry> tags must have two children, a <key> and a <value>", getDocument(), getLineno());
        }
        for (XmlItem i : children) {
            if (i.getType() == XmlItemType.VALUE) {
                if (value != null) {
                    throw new XmlConfigurationException("<entry> tags must have two children, a <key> and a <value>", getDocument(), getLineno());
                }
                value = (ValueXmlItem) i;
            } else if (i.getType() == XmlItemType.KEY) {
                if (key != null) {
                    throw new XmlConfigurationException("<entry> tags must have two children, a <key> and a <value>", getDocument(), getLineno());
                }
                key = (KeyXmlItem) i;
            }
        }
        return true;
    }

    public KeyXmlItem getKey() {
        return key;
    }

    public ValueXmlItem getValue() {
        return (ValueXmlItem) value;
    }

    /**
     * get the inline beans for the value and the key
     *
     * @param manager
     * @return
     */
    public Set<BeanResult<?>> getBeanResults(BeanManager manager) {
        Set<BeanResult<?>> ret = new HashSet<BeanResult<?>>();
        BeanResult<?> r = value.getBeanResult(manager);
        if (r != null) {
            ret.add(r);
        }
        r = key.getBeanResult(manager);
        if (r != null) {
            ret.add(r);
        }
        return ret;
    }

}
