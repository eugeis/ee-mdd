package ee.mdd.model.statemachine

import ee.mdd.model.component.CompilationUnit

class Event extends CompilationUnit {
  boolean alternative = false
  
  StateMachine getStateMachine() {
    parent
  }
}