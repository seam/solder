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
package org.jboss.solder.config.xml.test.common;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import org.jboss.solder.config.xml.model.MethodXmlItem;
import org.jboss.solder.config.xml.model.PropertyXmlItem;
import org.jboss.solder.config.xml.model.XmlItem;
import org.jboss.solder.config.xml.model.XmlItemType;
import org.jboss.solder.config.xml.parser.SaxNode;
import org.jboss.solder.config.xml.parser.namespace.CompositeNamespaceElementResolver;
import org.jboss.solder.config.xml.parser.namespace.NamespaceElementResolver;
import org.jboss.solder.config.xml.parser.namespace.PackageNamespaceElementResolver;
import org.jboss.solder.config.xml.test.common.simple.Bean1;
import org.junit.Test;

public class NamespaceResolverTest {

    @Test
    public void testPackageResolver() {
        PackageNamespaceElementResolver resolver = new PackageNamespaceElementResolver("org.jboss.solder.config.xml.test.common.simple");
        testResolver(resolver);
    }

    @Test
    public void testCompositePackageResolver() {
        List<String> namespaces = new ArrayList<String>();
        namespaces.add("java.lang");
        namespaces.add("java.util");
        namespaces.add("org.jboss.solder.config.xml.test.common.simple");
        CompositeNamespaceElementResolver resolver = new CompositeNamespaceElementResolver(namespaces);
        testResolver(resolver);
    }

    public void testResolver(NamespaceElementResolver resolver) {

        XmlItem item = resolver.getItemForNamespace(new SaxNode("Bean1", null, null, null, null, 0), null);
        Assert.assertTrue("Namespace resolver returned wrong class type", item.getJavaClass() == Bean1.class);
        Assert.assertTrue("Namespace resolver did not return class", item.getType() == XmlItemType.CLASS);
        XmlItem method = resolver.getItemForNamespace(new SaxNode("method1", null, null, null, null, 0), item);
        Assert.assertTrue("Item returned wrong type", method.getType() == XmlItemType.METHOD);

        method.resolveChildren(null);

        Assert.assertTrue("Could not resolve method", ((MethodXmlItem) method).getMethod() != null);
        Assert.assertTrue("Wrong method was resolved", ((MethodXmlItem) method).getMethod().getParameterTypes().length == 0);

        XmlItem field = resolver.getItemForNamespace(new SaxNode("field1", null, null, null, null, 0), item);
        Assert.assertTrue("Element of wrong type returned", ((PropertyXmlItem) field).getType() == XmlItemType.FIELD);
        Assert.assertTrue("field was not set", ((PropertyXmlItem) field).getField() != null);

    }

}
