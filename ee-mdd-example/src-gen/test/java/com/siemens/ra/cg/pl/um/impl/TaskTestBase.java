package com.siemens.ra.cg.pl.um.impl;

import static junit.framework.Assert.*;

import com.siemens.ra.cg.pl.um.Comment;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;

public abstract class TaskTestBase {
  protected TaskImpl item;
  
  @Before
  public void beforeTaskTestBase() {
    item = new TaskImpl();
  }
  
  @Test
  public void testProperties() { 
    Comment comment = new CommentImpl();
    Date created = new Date();
    Date closed = new Date();
    item.setComment(comment);
    item.setCreated(created);
    item.setClosed(closed);
    
    assertEquals(comment, item.getComment());
    assertEquals(created, item.getCreated());
    assertEquals(closed, item.getClosed());
  }

  @Test
  public void testConstructor() { 
     TaskImpl instance = new TaskImpl();
  }

  @Test
  public void testConstructorCommentAndCreated() { 
     Comment comment = new CommentImpl();
     Date created = new Date();
     TaskImpl instance = new TaskImpl(comment, created);
     assertSame(comment, instance.getComment());
     assertSame(created, instance.getCreated());
  }

  @Test
  public void testConstructorCommentAndCreatedAndClosed() { 
     Comment comment = new CommentImpl();
     Date created = new Date();
     Date closed = new Date();
     TaskImpl instance = new TaskImpl(comment, created, closed);
     assertSame(comment, instance.getComment());
     assertSame(created, instance.getCreated());
     assertSame(closed, instance.getClosed());
  }
}