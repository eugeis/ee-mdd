package com.siemens.ra.cg.pl.um;

import com.siemens.ra.cg.pl.um.Comment;
import java.io.Serializable;
import java.util.Date;

public interface TaskBase extends Serializable {

  Comment getComment();

  Date getCreated();

  Date getClosed();
  
  void setComment(Comment comment);
  
  void setCreated(Date created);
  
  void setClosed(Date closed);
}