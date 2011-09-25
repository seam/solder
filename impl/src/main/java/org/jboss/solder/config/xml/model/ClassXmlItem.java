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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.solder.config.xml.core.BeanResult;
import org.jboss.solder.config.xml.core.BeanResultType;
import org.jboss.solder.config.xml.core.VirtualProducerField;
import org.jboss.solder.config.xml.fieldset.FieldValueObject;
import org.jboss.solder.config.xml.util.TypeOccuranceInformation;
import org.jboss.solder.config.xml.util.XmlConfigurationException;
import org.jboss.solder.literal.InjectLiteral;
import org.jboss.solder.properties.Properties;
import org.jboss.solder.properties.Property;
import org.jboss.solder.properties.query.NamedPropertyCriteria;
import org.jboss.solder.properties.query.PropertyQueries;
import org.jboss.solder.properties.query.PropertyQuery;
import org.jboss.solder.reflection.Reflections;

public class ClassXmlItem extends AbstractXmlItem {

    private HashSet<TypeOccuranceInformation> allowed = new HashSet<TypeOccuranceInformation>();

    public ClassXmlItem(XmlItem parent, Class<?> c, Map<String, String> attributes, String document, int lineno) {
        super(XmlItemType.CLASS, parent, c, null, attributes, document, lineno);
        allowed.add(TypeOccuranceInformation.of(XmlItemType.ANNOTATION, null, null));
        allowed.add(TypeOccuranceInformation.of(XmlItemType.FIELD, null, null));
        allowed.add(TypeOccuranceInformation.of(XmlItemType.METHOD, null, null));
        allowed.add(TypeOccuranceInformation.of(XmlItemType.PARAMETERS, null, null));
        allowed.add(TypeOccuranceInformation.of(XmlItemType.REPLACE, null, null));
        allowed.add(TypeOccuranceInformation.of(XmlItemType.MODIFIES, null, null));
        allowed.add(TypeOccuranceInformation.of(XmlItemType.VALUE, null, null));
        allowed.add(TypeOccuranceInformation.of(XmlItemType.ENTRY, null, null));
    }

    public Set<TypeOccuranceInformation> getAllowedItem() {
        return allowed;
    }

    public Set<PropertyXmlItem> getShorthandFieldValues() {
        Set<PropertyXmlItem> values = new HashSet<PropertyXmlItem>();
        for (Entry<String, String> e : attributes.entrySet()) {
            PropertyQuery<Object> query = PropertyQueries.createQuery(getJavaClass());
            query.addCriteria(new NamedPropertyCriteria(e.getKey()));
            Property<Object> property = query.getFirstWritableResult();
            if (property != null) {
                property.setAccessible();
                values.add(new PropertyXmlItem(this, property, e.getValue(), null, document, lineno));
            } else {
                throw new XmlConfigurationException("Could not resolve field: " + e.getKey(), document, lineno);
            }
        }
        return values;
    }

    public BeanResult<?> createBeanResult(BeanManager manager) {
        boolean override = !getChildrenOfType(ReplacesXmlItem.class).isEmpty();
        boolean extend = !getChildrenOfType(ModifiesXmlItem.class).isEmpty();
        if (override && extend) {
            throw new XmlConfigurationException("A bean may not both <override> and <extend> an existing bean", getDocument(), getLineno());
        }
        BeanResultType beanType = override ? BeanResultType.REPLACES : (extend ? BeanResultType.MODIFIES : BeanResultType.ADD);
        List<BeanResult<?>> inlineBeans = new ArrayList<BeanResult<?>>();
        // get all the field values from the bean
        Set<String> configuredFields = new HashSet<String>();
        List<FieldValueObject> fields = new ArrayList<FieldValueObject>();
        for (PropertyXmlItem xi : getChildrenOfType(PropertyXmlItem.class)) {
            inlineBeans.addAll(xi.getInlineBeans());
            FieldValueObject f = xi.getFieldValue();
            if (f != null) {
                fields.add(f);
                configuredFields.add(xi.getFieldName());
            }
        }

        for (PropertyXmlItem f : getShorthandFieldValues()) {
            if (configuredFields.contains(f.getFieldName())) {
                throw new XmlConfigurationException("Field configured in two places: " + getJavaClass().getName() + "." + f.getFieldName(), getDocument(), getLineno());
            }
            fields.add(f.getFieldValue());
        }

        // if it is an extend we want to read the annotations from the underlying
        // class
        BeanResult<?> result = new BeanResult(getJavaClass(), extend, beanType, fields, inlineBeans, manager);

        List<ParameterXmlItem> constList = new ArrayList<ParameterXmlItem>();

        for (AnnotationXmlItem item : getChildrenOfType(AnnotationXmlItem.class)) {
            Annotation a = AnnotationUtils.createAnnotation(item);
            result.addToClass(a);
        }
        // list of constructor arguments
        List<ParametersXmlItem> constructorParameters = getChildrenOfType(ParametersXmlItem.class);
        if (constructorParameters.size() > 1) {
            throw new XmlConfigurationException("A method may only have a single <parameters> element", getDocument(), getLineno());
        } else if (!constructorParameters.isEmpty()) {
            for (ParameterXmlItem item : constructorParameters.get(0).getChildrenOfType(ParameterXmlItem.class)) {
                constList.add(item);
            }
        }
        for (PropertyXmlItem item : getChildrenOfType(PropertyXmlItem.class)) {
            if (item.getField() != null) {
                for (AnnotationXmlItem fi : item.getChildrenOfType(AnnotationXmlItem.class)) {
                    Annotation a = AnnotationUtils.createAnnotation(fi);
                    result.addToField(item.getField(), a);
                }
            } else if (!item.getChildrenOfType(AnnotationXmlItem.class).isEmpty()) {
                throw new XmlConfigurationException("Property's that do not have an underlying field may not have annotations added to them", item.getDocument(), item.getLineno());
            }
        }
        for (MethodXmlItem item : getChildrenOfType(MethodXmlItem.class)) {
            int paramCount = 0;

            for (AnnotationXmlItem fi : item.getChildrenOfType(AnnotationXmlItem.class)) {
                Annotation a = AnnotationUtils.createAnnotation(fi);
                result.addToMethod(item.getMethod(), a);
            }
            List<ParametersXmlItem> parameters = item.getChildrenOfType(ParametersXmlItem.class);
            if (parameters.size() > 1) {
                throw new XmlConfigurationException("A method may only have a single <parameters> element", item.getDocument(), item.getLineno());
            } else if (!parameters.isEmpty()) {
                for (ParameterXmlItem fi : parameters.get(0).getChildrenOfType(ParameterXmlItem.class)) {
                    int param = paramCount++;
                    for (AnnotationXmlItem pan : fi.getChildrenOfType(AnnotationXmlItem.class)) {
                        Annotation a = AnnotationUtils.createAnnotation(pan);
                        result.addToMethodParameter(item.getMethod(), param, a);
                    }
                }
            }

        }

        if (!constList.isEmpty()) {
            int paramCount = 0;
            Constructor<?> constructor = resolveConstructor(constList);
            // we automatically add inject to the constructor
            result.addToConstructor(constructor, InjectLiteral.INSTANCE);
            for (ParameterXmlItem fi : constList) {
                int param = paramCount++;
                for (AnnotationXmlItem pan : fi.getChildrenOfType(AnnotationXmlItem.class)) {
                    Annotation a = AnnotationUtils.createAnnotation(pan);
                    result.addToConstructorParameter(constructor, param, a);
                }
            }
        }
        return result;
    }

    /**
     * Builds up a bean result for a virtual producer field.
     *
     * @param manager
     * @return
     */
    public BeanResult<?> createVirtualFieldBeanResult(BeanManager manager) {
        boolean override = !getChildrenOfType(ReplacesXmlItem.class).isEmpty();
        boolean extend = !getChildrenOfType(ModifiesXmlItem.class).isEmpty();
        if (override || extend) {
            throw new XmlConfigurationException("A virtual producer field may not containe <override> or <extend> tags", getDocument(), getLineno());
        }
        Field member = org.jboss.solder.config.xml.util.Reflections.getField(VirtualProducerField.class, "field");
        member.setAccessible(true);
        ClassXmlItem vclass = new ClassXmlItem(null, VirtualProducerField.class, Collections.<String, String>emptyMap(), document, lineno);
        PropertyXmlItem field = new PropertyXmlItem(vclass, Properties.createProperty(member), null, getJavaClass(), document, lineno);
        vclass.addChild(field);
        for (XmlItem i : this.getChildren()) {
            field.addChild(i);
        }
        field.resolveChildren(manager);
        BeanResult<?> result = vclass.createBeanResult(manager);
        result.overrideFieldType(member, this.getJavaClass());
        return result;
    }

    private Constructor<?> resolveConstructor(List<ParameterXmlItem> constList) {
        Class<?>[] params = new Class[constList.size()];
        for (int i = 0; i < constList.size(); ++i) {
            params[i] = constList.get(i).getJavaClass();
        }
        Constructor<?> ret = Reflections.findDeclaredConstructor(getJavaClass(), params);
        if (ret == null) {
            throw new XmlConfigurationException("Could not resolve constructor for " + getJavaClass() + " with arguments " + params, getDocument(), getLineno());
        }
        return ret;
    }

}
