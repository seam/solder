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

package org.jboss.weld.extensions.test.resourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.asset.ByteArrayAsset;
import org.jboss.weld.extensions.resourceLoader.ResourceProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ResourceLoaderTest
{
   @Deployment
   public static Archive<?> deploy()
   {
      JavaArchive a = ShrinkWrap.create("test.jar", JavaArchive.class);
      a.addPackage(ResourceLoaderTest.class.getPackage());
      a.addPackage(ResourceProvider.class.getPackage());
      a.addManifestResource(new ByteArrayAsset(new byte[0]), "beans.xml");
      return a;
   }

   @Inject
   ResourceClient resourceClient;

   @Inject
   BeanManager beanManager;

   @Test
   public void testLoadsStream() throws Throwable
   {
      InputStream stream = resourceClient.getResourceProvider().loadResourceStream("com/acme/foo1");
      assert stream != null;
      assert stream.available() > 0;
      InputStreamReader reader = new InputStreamReader(stream);
      char[] chars = new char[4];
      reader.read(chars, 0, 4);
      assert new String(chars).equals("foo1");
   }

   @Test
   public void testLoadsURLs() throws Throwable
   {
      URL url = resourceClient.getResourceProvider().loadResource("com/acme/foo1");
      assert url != null;
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
      URL url = resourceClient.getResourceProvider().loadResource("/com/acme/foo1");
      assert url != null;
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
      Bean<ResourceClient> bean = (Bean) beanManager.getBeans(ResourceClient.class).iterator().next();
      CreationalContext<ResourceClient> creationalContext = beanManager.createCreationalContext(bean);
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
