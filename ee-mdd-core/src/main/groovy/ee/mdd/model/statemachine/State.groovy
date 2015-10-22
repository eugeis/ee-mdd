package ee.mdd.model.statemachine

import ee.mdd.model.component.Component
import ee.mdd.model.component.LogicUnit
import ee.mdd.model.component.Module

class State extends LogicUnit {
  List<String> toBeNotified = []
  List<Transition> transitions = []
  List<String> entryActions = []
  List<String> exitActions = []
  Integer timeoutInMillis = null
  List<EventTransitions> eventTransitions = []
  List<Action> actions
  List<Condition> conditions
  List<Action> entryActionObjs
  List<Action> exitActionObjs

  boolean init() {
    boolean ret = super.init()

    entryActionObjs = entryActions.collect { stateMachine.resolve(it, Action.class, true) }.findAll{ it }
    exitActionObjs = exitActions.collect { stateMachine.resolve(it, Action.class, true) }.findAll{ it }

    ret
  }
  
  Component getComponent() {
    parent.component
  }

  Module getModule() {
    parent.module
  }
  
  StateMachine getStateMachine() {
    parent
  }
  
  List<EventTransitions> getEventTransitions() {
    eventTransitions = transitions.findAll { !it.anyEvent }.groupBy { it.event }.collect { event, trs ->
      if(!event) { println "Event is null for transition $trs.name" }
      new EventTransitions(event: event, transitions: trs) }
  }
  
  List<Action> getActions() {
    actions = transitions.collectMany  { it.actionObjs }
    actions.addAll(exitActionObjs)
    actions.addAll(transitions.findAll { it.state.entryActionObjs }.collectMany { it.state.entryActionObjs })
    actions = actions.toSet() as List
    actions = actions.sort(false) { it.name }
  }
  
  List<Condition> getConditions() {
    conditions = transitions.collectMany { it.conditionObjs }
    conditions.addAll(transitions.collectMany { it.notConditionObjs })
    conditions = conditions.toSet() as List
    conditions = conditions.sort(false) { it.name }
  }

  def add(Transition item) {
    if(!transitions) {
      transitions = []
    }; transitions << super.add(item)
  }
  
  void setTimeout(String timeout) {
    timeoutInMillis = timeout.duration(timeout)
  }

  boolean isTimeoutEnabled() { stateMachine.timeoutEnabled && timeoutInMillis != null }
  
  void buildChildren() {
    super.buildChildren()

    transitions.findAll { it.anyEvent }.reverse().each { tr -> eventTransitions.each { etrs -> etrs.transitions.add(0, tr) } }

  }
  
}
