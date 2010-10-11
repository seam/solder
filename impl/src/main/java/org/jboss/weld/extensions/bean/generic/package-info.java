/**
 * Many common services and API's require the use of more than just one class. When exposing these services 
 * via CDI, it would be time consuming and error prone to force the end developer to provide producers for
 * all the different classes required. Generic beans provides a solution, allowing a framework author to
 * provide a set of related beans, one for each single configuration point defined by the end developer. 
 * The configuration points specifies the qualifiers which are inherited by all beans in the set.
 * 
 * @see org.jboss.weld.extensions.bean.generic.Generic
 * @see org.jboss.weld.extensions.bean.generic.GenericConfiguration
 * @see org.jboss.weld.extensions.bean.generic.GenericType
 * 
 * @author Pete Muir
 * @author Stuart Douglas
 */

package org.jboss.weld.extensions.bean.generic;

