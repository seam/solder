package org.jboss.weld.extensions.test.properties.query;

import org.jboss.weld.extensions.properties.Property;
import org.jboss.weld.extensions.properties.query.NamedPropertyCriteria;
import org.jboss.weld.extensions.properties.query.PropertyQueries;
import org.jboss.weld.extensions.properties.query.PropertyQuery;
import org.jboss.weld.extensions.properties.query.TypedPropertyCriteria;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Validate the property query mechanism.
 * 
 * @author Dan Allen
 */
public class PropertyQueryTest
{
   /**
    * Querying for a single result with a criteria that matches multiple
    * properties should throw an exception. 
    * 
    * @see PropertyQuery#getSingleResult()
    */
   @Test(expected = RuntimeException.class)
   public void testNonUniqueSingleResultThrowsException()
   {
      PropertyQuery<String> q = PropertyQueries.<String>createQuery(Person.class);
      q.addCriteria(new TypedPropertyCriteria(String.class));
      q.getSingleResult();
   }
   
   /**
    * Querying for a single result with a criteria that does not match
    * any properties should throw an exception.
    * 
    * @see PropertyQuery#getSingleResult()
    */
   @Test(expected = RuntimeException.class)
   public void testEmptySingleResultThrowsException()
   {
      PropertyQuery<String> q = PropertyQueries.<String>createQuery(Person.class);
      q.addCriteria(new TypedPropertyCriteria(Integer.class));
      q.getSingleResult();
   }
   
   /**
    * Querying for a single result with a criterai that matches exactly one
    * property should return the property.
    * 
    * @see PropertyQuery#getSingleResult()
    */
   @Test
   public void testSingleResult()
   {
      PropertyQuery<String> q = PropertyQueries.<String>createQuery(Person.class);
      q.addCriteria(new NamedPropertyCriteria("name"));
      Property<String> p = q.getSingleResult();
      assertNotNull(p);
      Person o = new Person();
      o.setName("Trap");
      assertEquals("Trap", p.getValue(o));
   }
}
