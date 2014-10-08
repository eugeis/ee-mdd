package com.siemens.ra.cg.pl.um.impl;

import com.siemens.ra.cg.pl.um.Comment;
import java.util.Date;

public class TaskImpl extends TaskImplBase {
  private static final long serialVersionUID = 1L;
   

  public TaskImpl() {
    super();
  }

  public TaskImpl(Comment comment, Date created) {
    super(comment, created);
  }

  public TaskImpl(Comment comment, Date created, Date closed) {
    super(comment, created, closed);
  }
  
}