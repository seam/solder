package org.slf4j.impl;

import org.slf4j.helpers.MarkerIgnoringBase;

/**
 * A very limited test logger which records the last info message logged
 */
public class TestLogger extends MarkerIgnoringBase
{

   private String lastMessage;
   
   private static final long serialVersionUID = 8313525025872406710L;
   
   
   
   public String getLastMessage()
   {
      return lastMessage;
   }
   
   public void reset()
   {
      this.lastMessage = null;
   }

   public void debug(String msg)
   {
      // TODO Auto-generated method stub
      
   }

   public void debug(String format, Object arg)
   {
      // TODO Auto-generated method stub
      
   }

   public void debug(String format, Object[] argArray)
   {
      // TODO Auto-generated method stub
      
   }

   public void debug(String msg, Throwable t)
   {
      // TODO Auto-generated method stub
      
   }

   public void debug(String format, Object arg1, Object arg2)
   {
      // TODO Auto-generated method stub
      
   }

   public void error(String msg)
   {
      // TODO Auto-generated method stub
      
   }

   public void error(String format, Object arg)
   {
      // TODO Auto-generated method stub
      
   }

   public void error(String format, Object[] argArray)
   {
      // TODO Auto-generated method stub
      
   }

   public void error(String msg, Throwable t)
   {
      // TODO Auto-generated method stub
      
   }

   public void error(String format, Object arg1, Object arg2)
   {
      // TODO Auto-generated method stub
      
   }

   public void info(String msg)
   {
      lastMessage = msg;
   }

   public void info(String format, Object arg)
   {
      // TODO Auto-generated method stub
      
   }

   public void info(String format, Object[] argArray)
   {
      // TODO Auto-generated method stub
      
   }

   public void info(String msg, Throwable t)
   {
      // TODO Auto-generated method stub
      
   }

   public void info(String format, Object arg1, Object arg2)
   {
      // TODO Auto-generated method stub
      
   }

   public boolean isDebugEnabled()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isErrorEnabled()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isInfoEnabled()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isTraceEnabled()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isWarnEnabled()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public void trace(String msg)
   {
      // TODO Auto-generated method stub
      
   }

   public void trace(String format, Object arg)
   {
      // TODO Auto-generated method stub
      
   }

   public void trace(String format, Object[] argArray)
   {
      // TODO Auto-generated method stub
      
   }

   public void trace(String msg, Throwable t)
   {
      // TODO Auto-generated method stub
      
   }

   public void trace(String format, Object arg1, Object arg2)
   {
      // TODO Auto-generated method stub
      
   }

   public void warn(String msg)
   {
      // TODO Auto-generated method stub
      
   }

   public void warn(String format, Object arg)
   {
      // TODO Auto-generated method stub
      
   }

   public void warn(String format, Object[] argArray)
   {
      // TODO Auto-generated method stub
      
   }

   public void warn(String msg, Throwable t)
   {
      // TODO Auto-generated method stub
      
   }

   public void warn(String format, Object arg1, Object arg2)
   {
      // TODO Auto-generated method stub
      
   }

}
