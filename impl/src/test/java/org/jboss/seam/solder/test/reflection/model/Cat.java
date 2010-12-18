package org.jboss.seam.solder.test.reflection.model;

public class Cat
{
   private int lives = 9;
   
   public void chewOnPowerCord()
   {
      lives--;
   }
   
   private void diveUnderMovingCar()
   {
      lives--;
   }
   
   void fightWithBigDog()
   {
      lives--;
   }
   
   public void earnLives(int lives)
   {
      if (lives < 0)
      {
         throw new IllegalArgumentException("Must be positive");
      }
      this.lives += lives;
   }
   
   public int getLives()
   {
      return lives;
   }
}
