package org.jboss.weld.test.extensions.beanid;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.util.AnnotationLiteral;

import org.jboss.testharness.impl.packaging.Artifact;
import org.jboss.testharness.impl.packaging.Classes;
import org.jboss.weld.extensions.beanid.AnnotatedTypeIdentifier;
import org.jboss.weld.test.AbstractWeldTest;
import org.testng.annotations.Test;
@Artifact
@Classes(packages = { "org.jboss.weld.extensions.beanid" })
public class IdentifiableBeansTest extends AbstractWeldTest
{
   @Test
   public void testBeanIdentifiers()
   {
      //TODO: This needs to be split up into lots of little tests
      IdentifiableBean bean = getReference(IdentifiableBean.class);
      AnnotatedTypeIdentifier identifier  = getReference(AnnotatedTypeIdentifier.class,new AnnotationLiteral<Default>()
      {
      });
      AnnotatedType<?> type = identifier.getAnnotatedType(bean);
      assert type.getJavaClass() == IdentifiableBean.class;
      assert type.isAnnotationPresent(IdentifiableInterceptorBinding.class);
   }
}
