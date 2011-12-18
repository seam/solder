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
package org.jboss.solder.config.xml.bootstrap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jboss.solder.resourceLoader.ResourceLoaderManager;
import org.jboss.solder.servlet.resource.WebResourceLocator;
import org.xml.sax.InputSource;

/**
 * Document Provider that loads XML documents from the classpath
 *
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 */
public class ResourceLoaderXmlDocumentProvider implements XmlDocumentProvider {

    private final ResourceLoaderManager manager = new ResourceLoaderManager();

    static final String[] DEFAULT_RESOURCES = {"WEB-INF/beans.xml", "WEB-INF/seam-beans.xml", "META-INF/seam-beans.xml", "META-INF/beans.xml"};

    final String[] resources;

    InputStream stream;

    public ResourceLoaderXmlDocumentProvider() {
        this(DEFAULT_RESOURCES);
    }

    public ResourceLoaderXmlDocumentProvider(String[] resources) {
        this.resources = resources;
    }

    List<URL> docs;

    ListIterator<URL> iterator;

    DocumentBuilderFactory factory;
    DocumentBuilder builder;

    public void open() {
        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e1) {
            throw new RuntimeException(e1);
        }
        docs = new ArrayList<URL>();

        for (String i : resources) {
            if (i.contains("WEB-INF")) {
                final WebResourceLocator wrl = new WebResourceLocator();
                final URL resourceUrl = wrl.getWebResourceUrl("/" + i);

                if (resourceUrl != null) {
                    docs.add(wrl.getWebResourceUrl("/" + i));
                }
            } else {
                docs.addAll(manager.getResources(i));
            }
        }
        iterator = docs.listIterator();
    }

    public void close() {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public XmlDocument getNextDocument() {
        if (stream != null) {
            try {
                stream.close();
                stream = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            while (iterator.hasNext()) {
                final URL url = iterator.next();
                // ignore empty files
                InputStream test = null;
                try {
                    test = url.openStream();
                    if (test.available() == 0) {
                        continue;
                    }
                } catch (FileNotFoundException e) {
                    // one of the expected locations didn't exist, move on.
                    continue;
                } finally {
                    if (test != null) {
                        test.close();
                    }
                }

                return new XmlDocument() {

                    public InputSource getInputSource() {
                        try {
                            stream = url.openStream();
                            return new InputSource(stream);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    public String getFileUrl() {
                        return url.toString();
                    }
                };
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
