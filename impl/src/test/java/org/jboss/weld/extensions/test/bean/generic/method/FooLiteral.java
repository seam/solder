package org.jboss.weld.extensions.test.bean.generic.method;

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
