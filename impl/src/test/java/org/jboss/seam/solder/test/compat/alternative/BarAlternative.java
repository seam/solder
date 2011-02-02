package org.jboss.seam.solder.test.compat.alternative;

import javax.enterprise.inject.Alternative;

@Alternative
public class BarAlternative extends Bar
{
   @Override
   public String ping()
   {
      return super.ping() + "Alternative";
   }
}
