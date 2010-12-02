package org.jboss.seam.solder.test.util.collections;

import org.jboss.seam.solder.util.collections.WrappedListIterator;
import org.junit.Test;

/**
 * tests for the presenece of a specific compiler bug
 * 
 * @author stuart
 * 
 */
public class CompilerBugTest
{
   @Test
   public void testcompilerBug()
   {
      WrappedListIterator.class.getTypeParameters();
   }
}
