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
package org.jboss.seam.solder.test.bean.generic.method;

import java.util.HashMap;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import junit.framework.Assert;
import org.jboss.seam.solder.bean.generic.Generic;
import org.jboss.seam.solder.bean.generic.GenericConfiguration;

/**
 * A generic bean for the config annotation Message
 *
 * @author pmuir
 */

@GenericConfiguration(Service.class)
public class Garply {

    static boolean disposerCalled = false;
    static boolean mapDisposerCalled = false;

    @Inject
    @Generic
    private Waldo waldo;

    @Produces
    public HashMap<String, String> getMap() {
        return new HashMap<String, String>();
    }

    @Produces
    @WaldoName
    public String getWaldoName() {
        return waldo.getName();
    }

    @Produces
    @Formatted
    public String getFormattedWaldoName(@Generic Waldo waldo) {
        return "[" + waldo.getName() + "]";
    }

    public void dispose(@Disposes @WaldoName String waldoName, @Generic Waldo waldo) {
        disposerCalled = true;
        Assert.assertEquals(waldo.getName(), waldoName);
    }

    public void dispose(@Disposes HashMap<String, String> map) {
        mapDisposerCalled = true;
    }

    public Waldo getWaldo() {
        return waldo;
    }

    public static boolean isDisposerCalled() {
        return disposerCalled;
    }

    public static boolean isMapDisposerCalled() {
        return mapDisposerCalled;
    }

}
