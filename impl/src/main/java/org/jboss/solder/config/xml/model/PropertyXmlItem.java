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
package org.jboss.solder.config.xml.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.solder.config.xml.core.BeanResult;
import org.jboss.solder.config.xml.fieldset.ArrayFieldSet;
import org.jboss.solder.config.xml.fieldset.CollectionFieldSet;
import org.jboss.solder.config.xml.fieldset.ConstantFieldValue;
import org.jboss.solder.config.xml.fieldset.ELFieldValue;
import org.jboss.solder.config.xml.fieldset.FieldValue;
import org.jboss.solder.config.xml.fieldset.FieldValueObject;
import org.jboss.solder.config.xml.fieldset.MapFieldSet;
import org.jboss.solder.config.xml.fieldset.SimpleFieldValue;
import org.jboss.solder.config.xml.util.TypeOccuranceInformation;
import org.jboss.solder.config.xml.util.XmlConfigurationException;
import org.jboss.solder.properties.Property;

public class PropertyXmlItem extends AbstractXmlItem {
    private final Property<Object> property;
    private final HashSet<TypeOccuranceInformation> allowed = new HashSet<TypeOccuranceInformation>();
    private final Class<?> fieldType;
    private final List<BeanResult<?>> inlineBeans = new ArrayList<BeanResult<?>>();

    private FieldValueObject fieldValue;

    public PropertyXmlItem(XmlItem parent, Property<Object> property, String innerText, String document, int lineno) {
        this(parent, property, innerText, null, document, lineno);
    }

    public PropertyXmlItem(XmlItem parent, Property<Object> property, String innerText, Class<?> overridenFieldType, String document, int lineno) {
        super(XmlItemType.FIELD, parent, parent.getJavaClass(), innerText, null, document, lineno);
        this.property = property;
        if (overridenFieldType == null) {
            this.fieldType = property.getJavaClass();
        } else {
            this.fieldType = overridenFieldType;
        }
        if (innerText != null && innerText.length() > 0) {
            FieldValue fv;
            if (innerText.matches("^#\\{.*\\}$")) {
                fv = new ELFieldValue(innerText);
            } else {
                fv = new ConstantFieldValue(innerText);
            }
            fieldValue = new SimpleFieldValue(parent.getJavaClass(), property, fv, fieldType);
        }
        allowed.add(new TypeOccuranceInformation(XmlItemType.VALUE, null, null));
        allowed.add(new TypeOccuranceInformation(XmlItemType.ANNOTATION, null, null));
        allowed.add(new TypeOccuranceInformation(XmlItemType.ENTRY, null, null));
    }

    public FieldValueObject getFieldValue() {
        return fieldValue;
    }

    @Override
    public boolean resolveChildren(BeanManager manager) {
        List<EntryXmlItem> mapEntries = new ArrayList<EntryXmlItem>();
        List<ValueXmlItem> valueEntries = new ArrayList<ValueXmlItem>();
        if (fieldValue == null) {
            for (XmlItem i : children) {
                if (i.getType() == XmlItemType.VALUE) {
                    valueEntries.add((ValueXmlItem) i);
                } else if (i.getType() == XmlItemType.ENTRY) {
                    mapEntries.add((EntryXmlItem) i);
                }

            }
        }
        if (!mapEntries.isEmpty() || !valueEntries.isEmpty()) {
            if (Map.class.isAssignableFrom(getFieldType())) {
                if (!valueEntries.isEmpty()) {
                    throw new XmlConfigurationException("Map fields cannot have <value> elements as children,only <entry> elements Field:" + getDeclaringClass().getName() + '.' + getFieldName(), getDocument(), getLineno());
                }
                if (!mapEntries.isEmpty()) {
                    for (EntryXmlItem entry : mapEntries) {
                        // resolve inline beans if nessesary
                        Set<BeanResult<?>> beans = entry.getBeanResults(manager);
                        inlineBeans.addAll(beans);

                    }
                    fieldValue = new MapFieldSet(property, mapEntries);
                }
            } else if (Collection.class.isAssignableFrom(getFieldType()) || getFieldType().isArray()) {
                if (!mapEntries.isEmpty()) {
                    throw new XmlConfigurationException("Collection fields must be set using <value> not <entry> Field:" + getDeclaringClass().getName() + '.' + getFieldName(), getDocument(), getLineno());
                }
                if (!valueEntries.isEmpty()) {
                    for (ValueXmlItem value : valueEntries) {
                        // resolve inline beans if nessesary
                        BeanResult<?> result = value.getBeanResult(manager);
                        if (result != null) {
                            inlineBeans.add(result);
                        }
                    }
                    if (getFieldType().isArray()) {
                        fieldValue = new ArrayFieldSet(property, valueEntries);
                    } else {
                        fieldValue = new CollectionFieldSet(property, valueEntries);
                    }
                }
            } else {
                if (!mapEntries.isEmpty()) {
                    throw new XmlConfigurationException("Only Map fields can be set using <entry> Field:" + getDeclaringClass().getName() + '.' + getFieldName(), getDocument(), getLineno());
                }
                if (valueEntries.size() != 1) {
                    throw new XmlConfigurationException("Non collection fields can only have a single <value> element Field:" + getDeclaringClass().getName() + '.' + getFieldName(), getDocument(), getLineno());
                }
                ValueXmlItem value = valueEntries.get(0);
                BeanResult<?> result = value.getBeanResult(manager);
                fieldValue = new SimpleFieldValue(parent.getJavaClass(), property, value.getValue(), fieldType);
                if (result != null) {
                    inlineBeans.add(result);
                }
            }
        }
        return true;
    }

    /**
     * Returns the field that corresponds to the property, or null if it does not
     * exist
     *
     * @return
     */
    public Field getField() {
        if (property.getMember() instanceof Field) {
            return (Field) property.getMember();
        }
        return org.jboss.solder.config.xml.util.Reflections.getField(parent.getJavaClass(), property.getName());
    }

    public Set<TypeOccuranceInformation> getAllowedItem() {
        return allowed;
    }

    public Collection<? extends BeanResult<?>> getInlineBeans() {
        return inlineBeans;
    }

    public Class<?> getDeclaringClass() {
        return property.getDeclaringClass();
    }

    public String getFieldName() {
        return property.getName();
    }

    public Class<?> getFieldType() {
        return fieldType;
    }

    public Property<?> getProperty() {
        return property;
    }

}
