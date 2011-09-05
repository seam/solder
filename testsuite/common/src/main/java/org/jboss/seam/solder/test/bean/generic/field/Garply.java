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
package org.jboss.seam.solder.test.bean.generic.field;


import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AnnotatedMember;
import javax.inject.Inject;

import org.jboss.seam.solder.bean.generic.Generic;
import org.jboss.seam.solder.bean.generic.GenericConfiguration;

/**
 * A generic bean for the config annotation Message
 *
 * @author pmuir
 */

@GenericConfiguration(Service.class)
public class Garply {

    @Inject
    @Generic
    private Waldo waldo;

    @Inject
    @Generic
    private AnnotatedMember<?> annotatedMember;


    @Produces
    @WaldoName
    public String getWaldoName() {
        return waldo.getName();
    }


    public Waldo getWaldo() {
        return waldo;
    }

    public AnnotatedMember<?> getAnnotatedMember() {
        return annotatedMember;
    }


}
