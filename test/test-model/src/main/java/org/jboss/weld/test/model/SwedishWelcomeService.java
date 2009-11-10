package org.jboss.weld.test.model;

import javax.enterprise.event.Event;
import javax.inject.Inject;

@Language(LanguageType.SE)
public class SwedishWelcomeService implements WelcomeService
{
   @Inject
   @Greeted
   @Language(LanguageType.SE)
   private Event<User> greetedUserEvent;

   public String greet(User user)
   {
      greetedUserEvent.fire(user);
      return "Hej " + user.getFirstName();
   }

}
