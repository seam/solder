/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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

package org.jboss.weld.extensions.test.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import org.jboss.testharness.impl.packaging.Artifact;
import org.jboss.testharness.impl.packaging.Classes;
import org.jboss.weld.test.AbstractWeldTest;
import org.testng.annotations.Test;

@Artifact
@Classes(packages = {"org.jboss.weld.extensions.resources.spi", "org.jboss.weld.extensions.resources"})
public class ResourceLoaderTest extends AbstractWeldTest
{

   @Test
   public void testLoadsStream() throws Throwable
   {
      InputStream stream = getReference(ResourceClient.class).getResourceProvider().loadResourceStream("com/acme/foo1");
      assert stream.available() > 0;
      InputStreamReader reader = new InputStreamReader(stream);
      char[] chars = new char[4];
      reader.read(chars, 0, 4);
      assert new String(chars).equals("foo1");
   }
   
   @Test
   public void testLoadsURLs() throws Throwable
   {
      URL url = getReference(ResourceClient.class).getResourceProvider().loadResource("com/acme/foo1");
      InputStream stream = url.openStream();
      assert stream.available() > 0;
      InputStreamReader reader = new InputStreamReader(stream);
      char[] chars = new char[4];
      reader.read(chars, 0, 4);
      assert new String(chars).equals("foo1");
      assert url.getFile().endsWith("/com/acme/foo1");
   }
   
   @Test
   public void testInitialSlashIgnored() throws Throwable
   {
      URL url = getReference(ResourceClient.class).getResourceProvider().loadResource("/com/acme/foo1");
      InputStream stream = url.openStream();
      assert stream.available() > 0;
      InputStreamReader reader = new InputStreamReader(stream);
      char[] chars = new char[4];
      reader.read(chars, 0, 4);
      assert new String(chars).equals("foo1");
      assert url.getFile().endsWith("com/acme/foo1");
   }
   
   @Test
   public void testStreamsAreCleanedUp() throws Throwable
   {
      Bean<ResourceClient> bean = getBean(ResourceClient.class);
      CreationalContext<ResourceClient> creationalContext = getCurrentManager().createCreationalContext(bean);
      ResourceClient client = bean.create(creationalContext);
      InputStream stream = client.getResourceProvider().loadResourceStream("/com/acme/foo1");
      assert stream.available() > 0;
      InputStreamReader reader = new InputStreamReader(stream);
      char[] chars = new char[4];
      reader.read(chars, 0, 4);
      assert new String(chars).equals("foo1");
      bean.destroy(client, creationalContext);
      try 
      {
         stream.available();
         assert false;
      }
      catch (IOException e) 
      {
         // Expected
      }
   }
   
}
