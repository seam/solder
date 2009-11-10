package org.jboss.weld.test.model;

import javax.enterprise.event.Observes;

public class GreetingsObserver
{

   public void onUserGreetedInNorwegian(
         @Observes @Greeted @Language(LanguageType.NO) User user)
   {
      user.wasGreetedIn(LanguageType.NO);
   }

   public void onUserGreetedInSwedish(
         @Observes @Greeted @Language(LanguageType.SE) User user)
   {
      user.wasGreetedIn(LanguageType.SE);
   }

}
