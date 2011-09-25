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
package org.jboss.solder.config.xml.parser.namespace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.solder.config.xml.model.XmlItem;
import org.jboss.solder.config.xml.parser.SaxNode;

/**
 * Namespace resolver that searches through a list of packages
 *
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 */
public class CompositeNamespaceElementResolver implements NamespaceElementResolver {

    private final Set<String> notFound = new HashSet<String>();
    private final List<PackageNamespaceElementResolver> resolvers = new ArrayList<PackageNamespaceElementResolver>();

    public CompositeNamespaceElementResolver(Collection<String> packages) {
        for (String s : packages) {
            resolvers.add(new PackageNamespaceElementResolver(s));
        }
    }

    public CompositeNamespaceElementResolver(String[] packages) {
        for (String s : packages) {
            resolvers.add(new PackageNamespaceElementResolver(s));
        }
    }

    public XmlItem getItemForNamespace(SaxNode node, XmlItem parent) {
        if (notFound.contains(node.getName())) {
            return null;
        }

        for (PackageNamespaceElementResolver p : resolvers) {
            XmlItem xi = p.getItemForNamespace(node, parent);
            if (xi != null) {
                return xi;
            }
        }
        notFound.add(node.getName());
        return null;
    }

}
