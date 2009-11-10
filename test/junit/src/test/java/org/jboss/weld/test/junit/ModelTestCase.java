package org.jboss.weld.test.junit;

import junit.framework.Assert;

import org.jboss.weld.test.model.Language;
import org.jboss.weld.test.model.LanguageType;
import org.jboss.weld.test.model.User;
import org.jboss.weld.test.model.WelcomeService;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CDIRunner.class)
public class ModelTestCase
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

   @Test
   public void shouldBeAbleToInjectParameters(
         @Language(LanguageType.SE) WelcomeService welcomeService)
         throws Exception
   {
      Assert.assertEquals("Hej " + userName, welcomeService.greet(user));
      Assert.assertTrue(user.hasBeenGreetedIn(LanguageType.SE));
      Assert.assertFalse(user.hasBeenGreetedIn(LanguageType.NO));
   }
}
