/**
 * <p>
 * Provides an application wide EL value and method expression resolution facility as well as a improved API for
 * evaluating EL expressions aimed at ease of use.
 * </p> 
 * 
 * <p>
 * To use the improved API, inject the Expressions bean, and call one of it's <code>evaluate</code> methods:
 * </p>
 * 
 * <pre>
 *    &#64;Inject Expressions expressions;
 *    
 *    ...
 *    
 *    Address address = expressions.evaluateValueExpression("#{person.address}");
 *    
 *    ...
 *    
 *    expressions.evaluateMethodExpression("#{userManager.savePerson}");
 * </pre>
 * 
 * <p>
 * By default Seam Solder will only resolve beans from CDI, and provides no function or variable mapping.
 * </p>
 * 
 * <p>
 * If you integrating Seam Solder into an environment that provides a source of beans for EL resolution, you can 
 * register an {@link javax.el.ELResolver} by creating a bean of type {@link javax.el.ELResolver} with the qualifier 
 * &#64;{@link org.jboss.seam.solder.el.Resolver}. 
 * </p>
 * 
 * <p>
 * If you integrating Seam Solder into an environment that provides a function or variable mapper, you can also 
 * provide an alternative {@link javax.el.FunctionMapper} or {@link javax.el.VariableMapper}. Simply create a bean 
 * exposing your alternative implementation with the qualifier &#64;{@link org.jboss.seam.solder.el.Mapper}.
 * </p>
 *
 * @see org.jboss.seam.solder.el.Expressions
 * @see org.jboss.seam.solder.el.Resolver
 * @see org.jboss.seam.solder.el.Mapper 
 */
package org.jboss.seam.solder.el;

