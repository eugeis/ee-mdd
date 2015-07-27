package ee.mdd.model.statemachine

import ee.mdd.model.component.CompilationUnit

class Condition extends CompilationUnit {
  boolean cachedInContext = true
  boolean toShared = true
  String body
}