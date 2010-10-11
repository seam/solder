/**
 * <p>
 * A number enhancements to the CDI programming model which are under trial and may be included in later releases 
 * of <em>Contexts and Dependency Injection</em>.
 * </p>
 * 
 * <p>
 * Included are:
 * 
 * <table>
 *    <tr>
 *       <td><code>&#64;{@link org.jboss.weld.extensions.core.Veto}</code></td>
 *       <td>Prevents a class from being installed as a bean</td>
 *    </tr>
 *    <tr>
 *       <td><code>&#64;{@link org.jboss.weld.extensions.core.Requires}</code></td>
 *       <td>Prevents a class from being installed as a bean unless class dependencies are satisfied</td>
 *    </tr>
 *    <tr>
 *       <td><code>&#64;{@link org.jboss.weld.extensions.core.Exact}</code></td>
 *       <td>Specify an implementation of an injection point type</td>
 *    </tr>
 *    <tr>
 *       <td><code>&#64;{@link org.jboss.weld.extensions.core.Client}</code></td>
 *       <td>Qualifier identifying a bean as belonging to the current client</td>
 *    </tr>
 *    <tr>
 *       <td></td>
 *       <td>Named packages</td>
 *    </tr>
 * </table>
 * 
 * @see org.jboss.weld.extensions.core.Veto
 * @see org.jboss.weld.extensions.core.Requires
 * @see org.jboss.weld.extensions.core.Exact
 * @see org.jboss.weld.extensions.core.Client
 * @see javax.inject.Named
 */
package org.jboss.weld.extensions.core;

