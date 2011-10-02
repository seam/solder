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

import org.jboss.solder.config.xml.util.TypeOccuranceInformation;

/**
 * represents a parameter of a constructor or method
 *
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 */
public class ParameterXmlItem extends AbstractXmlItem {
    HashSet<TypeOccuranceInformation> allowed = new HashSet<TypeOccuranceInformation>();

    public ParameterXmlItem(XmlItem parent, Class<?> c, String document, int lineno) {
        super(XmlItemType.PARAMETER, parent, c, null, null, document, lineno);
        allowed.add(TypeOccuranceInformation.of(XmlItemType.ANNOTATION, null, null));
        allowed.add(TypeOccuranceInformation.of(XmlItemType.CLASS, null, 1));
        allowed.add(TypeOccuranceInformation.of(XmlItemType.ARRAY, null, 1));
    }

    public Set<TypeOccuranceInformation> getAllowedItem() {
        return allowed;
    }

}
