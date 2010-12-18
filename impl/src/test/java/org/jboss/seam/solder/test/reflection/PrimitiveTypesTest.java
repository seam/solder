package org.jboss.seam.solder.test.reflection;

import java.sql.Date;

import junit.framework.Assert;

import org.jboss.seam.solder.reflection.PrimitiveTypes;
import org.junit.Test;

/**
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
public class PrimitiveTypesTest
{
   @Test
   public void should_box_primitive_type()
   {
      Assert.assertNotSame(Double.class, Double.TYPE);
      Assert.assertSame(Double.class, PrimitiveTypes.box(Double.TYPE));
      Assert.assertSame(Double.class, PrimitiveTypes.box(Double.class));
   }
   
   @Test
   public void should_unbox_wrapper_type()
   {
      Assert.assertNotSame(Double.TYPE, Double.class);
      Assert.assertSame(Double.TYPE, PrimitiveTypes.unbox(Double.class));
      Assert.assertSame(Double.TYPE, PrimitiveTypes.unbox(Double.TYPE));
   }
   
   @Test
   public void should_recognize_wrapper_type()
   {
      Assert.assertTrue(PrimitiveTypes.isWrapperType(Double.class));
      Assert.assertFalse(PrimitiveTypes.isWrapperType(Double.TYPE));
   }
   
   @Test
   public void should_not_affect_other_type()
   {
      Assert.assertSame(Date.class, PrimitiveTypes.unbox(Date.class));
      Assert.assertSame(Date.class, PrimitiveTypes.box(Date.class));
   }
}
