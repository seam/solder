/**
 * 
 * <p>
 * Allows a library to provide a default implmentation of a bean, which is used unless overridden by an application. 
 * Although this may sound identical to an alternative, alternatives have some restrictions that may make them 
 * undesirable. Primarily, alternatives require an entry in every <code>beans.xml</code> file in an application.
 * </p>
 * 
 * <p>
 * Developers consuming the extension will have to open up the any jar file which references the default bean, and edit 
 * the <code>beans.xml</code> file within, in order to override the service. This is where default beans come in.
 * </p>
 * 
 * <p>
 * Default beans allow you to create a default bean with a specified type and set of qualifiers. If no other bean is
 * installed that has the same type and qualifiers, then the default bean will be installed.
 * </p>
 * 
 * @see org.jboss.seam.solder.bean.defaultbean.DefaultBean
 */
package org.jboss.seam.solder.bean.defaultbean;

