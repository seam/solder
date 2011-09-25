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
package org.jboss.solder.config.xml.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Utility class that reads a file or URL into a String
 *
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 */
public class FileDataReader {

    public static String readUrl(URL u) throws IOException {
        InputStream s = u.openStream();
        String res = readFile(s);
        s.close();
        return res;
    }

    public static String readFile(InputStream file) throws IOException {
        InputStreamReader reader = new InputStreamReader(file);
        StringBuilder fileData = new StringBuilder();
        char[] buf = new char[1024];
        int numRead = 0;

        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        return fileData.toString();
    }
}
