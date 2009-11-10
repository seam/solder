package org.jboss.weld.test.model;

import javax.enterprise.event.Event;
import javax.inject.Inject;

@Language(LanguageType.NO)
public class NorwegianWelcomeService implements WelcomeService
{
   @Inject
   @Greeted
   @Language(LanguageType.NO)
   private Event<User> greetedUserEvent;

   public String greet(User user)
   {
      greetedUserEvent.fire(user);
      return "Hei " + user.getFirstName();
   }

}
