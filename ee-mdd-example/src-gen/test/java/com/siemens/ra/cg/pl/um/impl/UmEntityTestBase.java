package com.siemens.ra.cg.pl.um.impl;

import org.junit.Before;
import org.junit.Test;

public abstract class UmEntityTestBase {
  protected UmEntityImpl item;
  
  @Before
  public void beforeUmEntityTestBase() {
    item = new UmEntityImpl();
  }
  
  @Test
  public void testProperties() {
    
  }
}