package org.jboss.weld.test.model;

import java.util.ArrayList;
import java.util.List;

public class User
{
   private String firstName;

   private List<LanguageType> greetedInLanguage = new ArrayList<LanguageType>();

   public User(String firstName)
   {
      this.firstName = firstName;
   }

   public String getFirstName()
   {
      return firstName;
   }

   @Override
   public String toString()
   {
      return firstName;
   }

   public void wasGreetedIn(LanguageType language)
   {
      greetedInLanguage.add(language);
   }

   public boolean hasBeenGreetedIn(LanguageType language)
   {
      return greetedInLanguage.contains(language);
   }
}
