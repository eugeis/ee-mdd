package ee.mdd.model.statemachine

import ee.mdd.model.component.CompilationUnit

class Action extends CompilationUnit {
  String body
  boolean async = false
  boolean clientCache = false

  StateMachine getStateMachine() {
    parent
  }
}