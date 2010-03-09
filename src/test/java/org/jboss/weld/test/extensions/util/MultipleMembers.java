package org.jboss.weld.test.extensions.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MultipleMembers
{
   int intMember();

   long longMember();

   short shortMember();

   float floatMember();

   double doubleMember();

   byte byteMember();

   char charMember();

   boolean booleanMember();

   int[] intArrayMember();
}
