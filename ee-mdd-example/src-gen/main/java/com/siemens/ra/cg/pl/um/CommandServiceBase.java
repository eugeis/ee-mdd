package com.siemens.ra.cg.pl.um;

import com.siemens.ra.cg.pl.um.TaskAgregator;

public interface CommandServiceBase {

  TaskAgregator getTaskAgregator();
  
  void setTaskAgregator(TaskAgregator taskAgregator);
}