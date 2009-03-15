/**
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.webbeans.environment.se.test;

import javax.inject.AnnotationLiteral;
import javax.inject.manager.Manager;

import org.jboss.webbeans.environment.se.StartMain;
import org.jboss.webbeans.environment.se.events.Shutdown;
import org.jboss.webbeans.environment.se.test.beans.MainTestBean;
import org.jboss.webbeans.environment.se.test.beans.ParametersTestBean;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Peter Royle
 */
public class StartMainTest {

    public static String[] ARGS = new String[] { "arg1", "arg2", "arg 3"};


    /**
     * Test of main method, of class StartMain. Checks that the beans
     * found in the org.jboss.webbeans.environment.se.beans package are
     * initialised as expected.
     */
    @Test
    public void testMain()
    {
        String[] args = ARGS ;
        Manager manager = new StartMain(args).main();

        MainTestBean mainTestBean = manager.getInstanceByType( MainTestBean.class );
        Assert.assertNotNull( mainTestBean );

        ParametersTestBean paramsBean = mainTestBean.getParametersTestBean();
        Assert.assertNotNull( paramsBean );
        Assert.assertNotNull( paramsBean.getParam1() );
        Assert.assertEquals( ARGS[0], paramsBean.getParam1() );
        Assert.assertNotNull( paramsBean.getParam2() );
        Assert.assertEquals( ARGS[1], paramsBean.getParam2() );
        Assert.assertNotNull( paramsBean.getParam3() );
        Assert.assertEquals( ARGS[2], paramsBean.getParam3() );

        manager.fireEvent( manager, new AnnotationLiteral<Shutdown>() {} );
        boolean contextNotActive = false;
        try
        {
           assert manager.getInstanceByType(MainTestBean.class) == null;
        }
        catch (Exception e) 
        {
           contextNotActive = true;
        }
        assert contextNotActive;
    }

}
