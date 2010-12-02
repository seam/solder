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
package org.jboss.seam.solder.test.defaultbean;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.jboss.seam.solder.bean.defaultbean.DefaultBean;

/**
 * test that producer methods are read from the installed default bean and not
 * the synthetic delegate
 * 
 * @author stuart
 * 
 */
@ApplicationScoped
@DefaultBean(HardDriveFactory.class)
public class HardDriveFactory
{
   private String size = "small";

   @DefaultBean(HardDrive.class)
   @Produces
   public HardDrive getHardDrive()
   {
      return new HardDrive()
      {
         public String size()
         {
            return size;
         }
      };
   }


   public String getSize()
   {
      return size;
   }

   public void setSize(String size)
   {
      this.size = size;
   }



}
