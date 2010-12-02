package org.jboss.seam.solder.test.bean.generic.field;

import javax.enterprise.util.AnnotationLiteral;

public class FooLiteral extends AnnotationLiteral<Foo> implements Foo
{
   
   private final int value;
   
   public FooLiteral(int value)
   {
      this.value = value;
   }

   public int value()
   {
      return value;
   }

}
