package org.jboss.solder.config.xml.test.common.simple;

import static org.jboss.solder.config.xml.test.common.util.Deployments.baseDeployment;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.runner.RunWith;


/**
 * This test verifies that a no-namespace root element does not break the deployment.
 *
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 * @see https://issues.jboss.org/browse/SEAMXML-45
 */
@RunWith(Arquillian.class)
public class EmptyRootNamespaceTest extends SimpleBeanTest {
    @Deployment(name = "EmptyRootNamespaceTest")
    public static Archive<?> deployment() {
        return baseDeployment(EmptyRootNamespaceTest.class, "empty-root-namespace-beans.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addClasses(SimpleBeanTest.class, 
                    Bean1.class, Bean2.class, Bean3.class, 
                    ExtendedBean.class, ExtendedQualifier1.class, ExtendedQualifier2.class,
                    OverriddenBean.class, ScopeOverrideBean.class);
    }
}
