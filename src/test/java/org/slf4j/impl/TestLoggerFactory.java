package org.slf4j.impl;

import org.slf4j.ILoggerFactory;

public class TestLoggerFactory implements ILoggerFactory
{

   public final static TestLoggerFactory INSTANCE = new TestLoggerFactory();

   private final TestLogger logger = new TestLogger();
   
   public TestLogger getLogger(String name)
   {
      return logger;
   }
}
