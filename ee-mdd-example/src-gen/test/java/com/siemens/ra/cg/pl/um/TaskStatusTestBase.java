package com.siemens.ra.cg.pl.um;
 
import static junit.framework.Assert.*;

import org.junit.Test;

public class TaskStatusTestBase {
  
  @Test
  public void testVal() { 
  assertNotNull(TaskStatus.UNKNOWN.getCode());    
  assertNotNull(TaskStatus.OPEN.getCode());    
  assertNotNull(TaskStatus.CLOSED.getCode());    
  }

  @Test
  public void testIsLiteral() { 
  assertTrue(TaskStatus.UNKNOWN.isUnknown()); 
  assertFalse(TaskStatus.UNKNOWN.isOpen());
  assertTrue(TaskStatus.OPEN.isOpen()); 
  assertFalse(TaskStatus.OPEN.isClosed());
  assertTrue(TaskStatus.CLOSED.isClosed()); 
  assertFalse(TaskStatus.CLOSED.isUnknown());
  }
}
 