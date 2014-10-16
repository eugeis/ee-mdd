package com.siemens.ra.cg.pl.um.impl;

import com.siemens.ra.cg.pl.um.Comment;
import com.siemens.ra.cg.pl.um.Task;
import java.util.Date;

HAAALLLOOOO
TaskMetaAttribute
public abstract class TaskImplBase implements Task {
  private static final long serialVersionUID = 1L;
  
  
  protected Comment comment;
  protected Date created;
  protected Date closed;
  
  public TaskImplBase() {
  }
  
  public TaskImplBase(Comment comment, Date created) {
    this.comment = comment;
    this.created = created;
  }
  
  public TaskImplBase(Comment comment, Date created, Date closed) {
    this.comment = comment;
    this.created = created;
    this.closed = closed;
  }
  
  public Comment getComment() {
    return comment; 
  }
  
  public Date getCreated() {
    return created; 
  }
  
  public Date getClosed() {
    return closed; 
  }

  public void setComment(Comment comment) {
    this.comment = comment; 
  }

  public void setCreated(Date created) {
    this.created = created; 
  }

  public void setClosed(Date closed) {
    this.closed = closed; 
  }
}