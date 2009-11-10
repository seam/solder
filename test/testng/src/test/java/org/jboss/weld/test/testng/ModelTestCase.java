package org.jboss.weld.test.testng;

import org.jboss.weld.test.model.Language;
import org.jboss.weld.test.model.LanguageType;
import org.jboss.weld.test.model.User;
import org.jboss.weld.test.model.WelcomeService;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ModelTestCase extends AbstractCDITest
{
   // Will not be injected, no BindingType
   private static String userName = "Aslak";

   private @Language(LanguageType.NO)
   WelcomeService service;

   private User user = new User(userName);

   @Test
   public void shouldBeAbleToInjectInstanceVars() throws Exception
   {
      Assert.assertEquals("Hei " + userName, service.greet(user));
      Assert.assertTrue(user.hasBeenGreetedIn(LanguageType.NO));
      Assert.assertFalse(user.hasBeenGreetedIn(LanguageType.SE));
   }

}
