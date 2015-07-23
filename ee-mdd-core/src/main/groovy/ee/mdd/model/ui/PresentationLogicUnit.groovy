package ee.mdd.model.ui

import ee.mdd.model.component.LogicUnit

class PresentationLogicUnit extends LogicUnit {
  List<Listener> handlers = []
  List<Listener> observers = []

  boolean init() {
    super.init()
    true
  }

  View getView() {
    parent
  }
  def addHandlerFor(Listener item) {
    handlers << item
  }
  def addObserverFor(Listener item) {
    observers << item
  }
}