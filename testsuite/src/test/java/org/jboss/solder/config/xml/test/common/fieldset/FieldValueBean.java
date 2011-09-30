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
package org.jboss.solder.config.xml.test.common.fieldset;

import java.math.BigDecimal;

import org.jboss.solder.config.xml.test.common.method.QualifierEnum;

public class FieldValueBean {

    public void init() {
        assert ivalue != 20;
    }

    private int ivalue = 20;

    public String stringValue;

    public QualifierEnum enumValue;

    public boolean bvalue;

    public float fvalue = 1;

    public double dvalue = 1;

    private BigDecimal bigDecimalValue;

    public BigDecimal readBigDecimalValue() {
        return bigDecimalValue;
    }

    public short svalue;

    public long lvalue;

    public String elValue;

    public String elInnerTextValue;

    int noFieldValue;

    public void setIvalue(int value) {
        this.ivalue = value + 1;
    }

    public int getIvalue() {
        return ivalue;
    }

    public int getNoField() {
        return noFieldValue;
    }

    public void setNoField(int value) {
        noFieldValue = value;
    }

}
