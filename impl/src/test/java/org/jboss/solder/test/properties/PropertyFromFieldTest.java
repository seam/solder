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
package org.jboss.solder.test.properties;

import java.lang.reflect.Field;

import org.jboss.solder.properties.FieldProperty;
import org.jboss.solder.properties.Properties;
import org.junit.Test;

/**
 * Verify that only valid properties are permitted, as per the JavaBean specification.
 *
 * @author Vivian Steller
 */
public class PropertyFromFieldTest {
	
	@Test
	public void testAccessingPrimitiveTypedFieldProperty() throws Exception
	{
		final Field field = ClassToIntrospect.class.getField("primitiveProperty");
		
		FieldProperty<Object> propertyUT = Properties.createProperty(field);
		propertyUT.getValue(new ClassToIntrospect());
	}
	
}
