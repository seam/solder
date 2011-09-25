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
package org.jboss.solder.config.xml.parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.solder.config.xml.util.XmlParseException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Parser for xml configration, this class should only be used once and then
 * discarded
 *
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 */
public class ParserMain extends DefaultHandler {

    SaxNode parentNode = null;

    SaxNode currentNode = null;

    Locator locator;

    String currentText = "";

    String document;

    List<Exception> errors;

    public SaxNode parse(InputSource inputSource, String fileUrl, List<Exception> errors) {
        this.errors = errors;
        document = fileUrl;
        try {
            XMLReader xr = XMLReaderFactory.createXMLReader();
            xr.setContentHandler(this);
            xr.setErrorHandler(this);
            xr.parse(inputSource);
            return parentNode;
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        currentText = currentText + new String(ch, start, length);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        Map<String, String> atmap = new HashMap<String, String>();
        for (int i = 0; i < attributes.getLength(); ++i) {
            atmap.put(attributes.getLocalName(i), attributes.getValue(i));
        }
        int pos = 0;
        if (locator != null) {
            pos = locator.getLineNumber();
        }
        SaxNode node = new SaxNode(localName, uri, atmap, currentNode, document, pos);
        if (currentNode == null) {
            parentNode = node;
        } else {
            currentNode.addChild(node);
        }

        currentNode = node;
        currentText = "";
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        currentNode.setInnerText(currentText);
        currentNode = currentNode.getParent();
        currentText = "";
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        errors.add(new XmlParseException(e, document, e.getLineNumber()));
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        errors.add(new XmlParseException(e, document, e.getLineNumber()));
    }
}
