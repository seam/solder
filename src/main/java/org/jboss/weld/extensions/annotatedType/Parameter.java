package org.jboss.weld.extensions.annotatedType;

import java.lang.reflect.Member;

public class Parameter
{

   private final Member declaringMember;
   private final int position;

   Parameter(Member declaringMember, int position)
   {
      this.declaringMember = declaringMember;
      this.position = position;
   }

   public Member getDeclaringMember()
   {
      return declaringMember;
   }

   public int getPosition()
   {
      return position;
   }

   @Override
   public int hashCode()
   {
      int hash = 1;
      hash = hash * 31 + declaringMember.hashCode();
      hash = hash * 31 + Integer.valueOf(position).hashCode();
      return hash;
   }
   
   @Override
   public boolean equals(Object obj)
   {
      if (obj instanceof Parameter)
      {
         Parameter that = (Parameter) obj;
         return this.getDeclaringMember().equals(that.getDeclaringMember()) && this.getPosition() == that.getPosition(); 
      }
      else
      {
         return false;
      }
         
   }

}
