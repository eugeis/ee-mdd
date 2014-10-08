package com.siemens.ra.cg.pl.um;

public enum TaskStatus {
  UNKNOWN(-1),
  OPEN(1),
  CLOSED(2);
  
  protected int code; 

  private TaskStatus(int code) {
      this.code = code;
    }
  
  public int getCode() {
    return code; 
  }
  
  public boolean isUnknown() {
    return this == UNKNOWN; 
  }
  
  public boolean isOpen() {
    return this == OPEN; 
  }
  
  public boolean isClosed() {
    return this == CLOSED; 
  }
}