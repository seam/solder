/**
 * <p>
 * Allows a library to expose sets of beans which may be configured multiple times in an application. When exposing 
 * these services via CDI, it would be time consuming and error prone to force the end developer to provide producers 
 * for all the different classes required. Generic beans provides a solution, allowing a framework author to
 * provide a set of related beans, one for each single configuration point defined by the end developer. 
 * The configuration points specifies the qualifiers which are inherited by all beans in the set.
 * </p>
 * 
 * @see org.jboss.seam.solder.bean.generic.Generic
 * @see org.jboss.seam.solder.bean.generic.GenericConfiguration
 * @see org.jboss.seam.solder.bean.generic.GenericType
 * 
 * @author Pete Muir
 * @author Stuart Douglas
 */

package org.jboss.seam.solder.bean.generic;

