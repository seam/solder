package org.jboss.seam.solder.test.compat;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

public class BeerCollector
{
   @Inject
   private BeanManager beanManager;
   
   @Inject @Any
   private Instance<Beer> beerRefs;
   
   public Integer getNumDiscovered()
   {
      int cnt = 0;
      for (@SuppressWarnings("unused") Beer b : beerRefs)
      {
         cnt++;
      }
      return cnt;
   }
   
   public boolean isNamedBeerVisible(String name)
   {
      return beanManager.getBeans(name).size() == 1;
   }
}
