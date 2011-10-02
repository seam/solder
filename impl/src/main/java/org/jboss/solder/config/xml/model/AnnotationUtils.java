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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.solder.config.xml.util.XmlConfigurationException;
import org.jboss.solder.config.xml.util.XmlObjectConverter;
import org.jboss.solder.reflection.AnnotationInstanceProvider;

/**
 * @author Stuart Douglas
 */
class AnnotationUtils {
    final private static AnnotationInstanceProvider annotationInstanceProvider = new AnnotationInstanceProvider();

    @SuppressWarnings("unchecked")
    static Annotation createAnnotation(AnnotationXmlItem item) {
        Map<String, Object> typedVars = new HashMap<String, Object>();
        Class<?> anClass = item.getJavaClass();
        for (Entry<String, String> e : item.getAttributes().entrySet()) {
            String mname = e.getKey();
            Method m;
            try {
                m = anClass.getDeclaredMethod(mname);
            } catch (Exception e1) {
                throw new XmlConfigurationException("Annotation " + item.getJavaClass().getName() + " does not have a member named " + mname + " ,error in XML", item.getDocument(), item.getLineno());
            }
            Class<?> returnType = m.getReturnType();
            typedVars.put(mname, XmlObjectConverter.convert(returnType, e.getValue()));
        }

        return annotationInstanceProvider.get((Class) item.getJavaClass(), typedVars);
    }
}
