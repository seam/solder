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
package org.jboss.solder.bean.generic;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Uniquely identifies a generic bean set
 *
 * @author Stuart Douglas
 */
public class GenericIdentifier {

    private final Set<Annotation> qualifiers;
    private final Annotation configuration;

    @Override
    public String toString() {
        return "GenericIdentifier [configuration=" + configuration + ", qualifiers=" + qualifiers + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((configuration == null) ? 0 : configuration.hashCode());
        result = prime * result + ((qualifiers == null) ? 0 : qualifiers.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GenericIdentifier other = (GenericIdentifier) obj;
        if (configuration == null) {
            if (other.configuration != null)
                return false;
        } else if (!configuration.equals(other.configuration))
            return false;
        if (qualifiers == null) {
            if (other.qualifiers != null)
                return false;
        } else if (!qualifiers.equals(other.qualifiers))
            return false;
        return true;
    }

    public GenericIdentifier(Set<Annotation> qualifiers, Annotation configuration) {
        this.qualifiers = qualifiers;
        this.configuration = configuration;
    }

    public Set<Annotation> getQualifiers() {
        return qualifiers;
    }

    public Class<? extends Annotation> getAnnotationType() {
        return configuration.annotationType();
    }

    public Annotation getConfiguration() {
        return configuration;
    }

}
