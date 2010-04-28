/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.weld.extensions.test.beanlookup;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.util.AnnotationLiteral;

import org.jboss.testharness.impl.packaging.Artifact;
import org.jboss.testharness.impl.packaging.Classes;
import org.jboss.weld.extensions.bean.lookup.AnnotatedTypeIdentifier;
import org.jboss.weld.test.AbstractWeldTest;
import org.testng.annotations.Test;
@Artifact
@Classes(packages = { "org.jboss.weld.extensions.beanlookup" })
public class IdentifiableBeansTest extends AbstractWeldTest
{
   @Test
   public void testBeanIdentifiers()
   {
      IdentifiableBean bean = getReference(IdentifiableBean.class);
      AnnotatedTypeIdentifier identifier  = getReference(AnnotatedTypeIdentifier.class,new AnnotationLiteral<Default>()
      {
      });
      AnnotatedType<?> type = identifier.getAnnotatedType(bean);
      assert type.getJavaClass() == IdentifiableBean.class;
      assert type.isAnnotationPresent(IdentifiableInterceptorBinding.class);
   }
}
