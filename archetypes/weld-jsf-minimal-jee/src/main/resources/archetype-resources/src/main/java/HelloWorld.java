package ${package};

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
@RequestScoped
@Named
public class HelloWorld implements Serializable
{
   public HelloWorld()
   {
      System.out.println(this.getClass().getSimpleName() + " was constructed");
   }

   private final String text = "Hello World!";

   public String getText()
   {
      return text;
   }

   private static final long serialVersionUID = 1L;
}