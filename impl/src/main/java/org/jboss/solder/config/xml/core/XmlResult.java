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
package org.jboss.solder.config.xml.core;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores the result of parsing an XML document
 *
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 */
public class XmlResult implements Comparable<XmlResult> {

    private final Map<Class<? extends Annotation>, Annotation[]> stereotypes = new HashMap<Class<? extends Annotation>, Annotation[]>();

    private final List<Class<? extends Annotation>> qualifiers = new ArrayList<Class<? extends Annotation>>();

    private final List<Class<? extends Annotation>> interceptorBindings = new ArrayList<Class<? extends Annotation>>();

    private final List<Class<?>> veto = new ArrayList<Class<?>>();

    private final List<String> problems = new ArrayList<String>();

    private final List<BeanResult<?>> beans = new ArrayList<BeanResult<?>>();

    private final String sortKey;

    public XmlResult(String fileUrl) {
        StringBuilder keyBuilder = new StringBuilder(fileUrl.length());
        for (int i = fileUrl.length() - 1; i >= 0; --i) {
            keyBuilder.append(fileUrl.charAt(i));
        }
        sortKey = keyBuilder.toString();
    }

    public void addStereotype(Class<? extends Annotation> an, Annotation[] values) {
        stereotypes.put(an, values);
    }

    public Map<Class<? extends Annotation>, Annotation[]> getStereotypes() {
        return stereotypes;
    }

    public void addQualifier(Class<? extends Annotation> qualifier) {
        qualifiers.add(qualifier);
    }

    public List<Class<? extends Annotation>> getQualifiers() {
        return qualifiers;
    }

    public void addInterceptorBinding(Class<? extends Annotation> binding) {
        interceptorBindings.add(binding);
    }

    public List<Class<? extends Annotation>> getInterceptorBindings() {
        return interceptorBindings;
    }

    public void addBean(BeanResult<?> bean) {
        beans.add(bean);
    }

    public List<BeanResult<?>> getBeans() {
        return beans;
    }

    /**
     * Gets all beans from the result, including inline beans
     *
     * @return
     */
    public List<BeanResult<?>> getFlattenedBeans() {
        List<BeanResult<?>> results = new ArrayList<BeanResult<?>>();
        for (BeanResult<?> a : beans) {
            getFlattenedBeans(a, results);
        }
        return results;
    }

    private void getFlattenedBeans(BeanResult<?> r, List<BeanResult<?>> results) {
        results.add(r);
        for (BeanResult<?> a : r.getInlineBeans()) {
            getFlattenedBeans(a, results);
        }
    }

    public List<String> getProblems() {
        return problems;
    }

    public void addProblem(String p) {
        problems.add(p);
    }

    public void addVeto(Class<?> clazz) {
        veto.add(clazz);
    }

    public List<Class<?>> getVeto() {
        return veto;
    }

    public int compareTo(XmlResult o) {
        return sortKey.compareTo(o.sortKey);
    }

}
