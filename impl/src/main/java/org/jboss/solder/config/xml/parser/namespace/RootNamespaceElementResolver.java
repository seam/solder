/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.solder.config.xml.parser.namespace;

import org.jboss.solder.config.xml.model.ArrayXmlItem;
import org.jboss.solder.config.xml.model.ClassXmlItem;
import org.jboss.solder.config.xml.model.EntryXmlItem;
import org.jboss.solder.config.xml.model.KeyXmlItem;
import org.jboss.solder.config.xml.model.ModifiesXmlItem;
import org.jboss.solder.config.xml.model.ParameterXmlItem;
import org.jboss.solder.config.xml.model.ParametersXmlItem;
import org.jboss.solder.config.xml.model.ReplacesXmlItem;
import org.jboss.solder.config.xml.model.ValueXmlItem;
import org.jboss.solder.config.xml.model.XmlItem;
import org.jboss.solder.config.xml.model.XmlItemType;
import org.jboss.solder.config.xml.parser.SaxNode;

public class RootNamespaceElementResolver implements NamespaceElementResolver {

    private final CompositeNamespaceElementResolver delegate;
    static final String[] namspaces = {"java.lang", "java.util", "javax.annotation", "javax.inject", "javax.enterprise.inject", "javax.enterprise.context", "javax.enterprise.event", "javax.decorator", "javax.interceptor", "org.jboss.solder.config.xml.annotations.internal", "org.jboss.solder.core", "org.jboss.solder.unwraps", "org.jboss.solder.resourceLoader"};

    public RootNamespaceElementResolver() {
        delegate = new CompositeNamespaceElementResolver(namspaces);
    }

    public XmlItem getItemForNamespace(SaxNode node, XmlItem parent) {
        XmlItem ret = getRootItem(node, parent);
        if (ret != null)
            return ret;
        return delegate.getItemForNamespace(node, parent);

    }

    XmlItem getRootItem(SaxNode node, XmlItem parent) {
        String item = node.getName();
        if (item.equals(XmlItemType.VALUE.getElementName()) || item.equals(XmlItemType.VALUE.getAlias())) {
            return new ValueXmlItem(parent, node.getInnerText(), node.getDocument(), node.getLineNo());
        } else if (item.equals(XmlItemType.KEY.getElementName()) || item.equals(XmlItemType.KEY.getAlias())) {
            return new KeyXmlItem(parent, node.getInnerText(), node.getDocument(), node.getLineNo());
        } else if (item.equals(XmlItemType.ENTRY.getElementName()) || item.equals(XmlItemType.ENTRY.getAlias())) {
            return new EntryXmlItem(parent, node.getDocument(), node.getLineNo());
        } else if (item.equals(XmlItemType.ARRAY.getElementName())) {
            return new ArrayXmlItem(parent, node.getAttributes(), node.getDocument(), node.getLineNo());
        } else if (item.equals(XmlItemType.REPLACE.getElementName())) {
            return new ReplacesXmlItem(parent, node.getDocument(), node.getLineNo());
        } else if (item.equals(XmlItemType.MODIFIES.getElementName())) {
            return new ModifiesXmlItem(parent, node.getDocument(), node.getLineNo());
        } else if (item.equals(XmlItemType.PARAMETERS.getElementName())) {
            return new ParametersXmlItem(parent, node.getDocument(), node.getLineNo());
        }
        // now deal with primitive types

        Class<?> primType = null;
        if (item.equals("int")) {
            primType = int.class;
        } else if (item.equals("short")) {
            primType = short.class;
        } else if (item.equals("long")) {
            primType = long.class;
        } else if (item.equals("byte")) {
            primType = byte.class;
        } else if (item.equals("char")) {
            primType = char.class;
        } else if (item.equals("double")) {
            primType = double.class;
        } else if (item.equals("float")) {
            primType = float.class;
        } else if (item.equals("boolean")) {
            primType = boolean.class;
        }
        if (primType != null) {
            if (parent != null && parent.getType() == XmlItemType.PARAMETERS) {
                return new ParameterXmlItem(parent, primType, node.getDocument(), node.getLineNo());
            } else {
                return new ClassXmlItem(parent, primType, node.getAttributes(), node.getDocument(), node.getLineNo());
            }
        }

        return null;
    }

}
