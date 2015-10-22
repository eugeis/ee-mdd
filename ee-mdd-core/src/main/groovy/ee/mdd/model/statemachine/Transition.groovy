package ee.mdd.model.statemachine

import ee.mdd.model.Element
import ee.mdd.model.component.Component
import ee.mdd.model.realm.RealmGroup

class Transition extends Element {
  String to, redirectEvent
  List<String> actions = []
  List<String> conditions = []
  List<String> notConditions = []
  List<String> groups = []
  State state
  Event event, redirect
  List<Action> actionObjs
  List<Action> allActions
  List<Condition> conditionObjs
  List<Condition> notConditionObjs
  List<RealmGroup> groupObjs
  boolean fireEvent = true
  
  boolean init() {
    def ret = super.init()
    if(!anyEvent) {
      event = stateMachine.resolve(name, Event.class, true)
    }
    if(redirectEvent) {
      redirect = stateMachine.resolve(redirectEvent, Event.class, true)
    }
    conditionObjs = conditions.collect { stateMachine.resolve(it, Condition.class, true) }.findAll { it }
    notConditionObjs = notConditions.collect { stateMachine.resolve(it, Condition.class, true) }.findAll { it }
    actionObjs = actions.collect  { stateMachine.resolve(it, Action.class, true) }.findAll { it }
    
    ret
  }

  State getFromState() {
    parent instanceof State ? parent : null
  }
  
  State getState() {
    if(!to) {
      to = parent.name
    }
    state = stateMachine.resolve(to, State.class, true)
  }
  
  StateMachine getStateMachine() {
    parent.parent
  }
  
  Component getComponent() {
    parent.parent.parent
  }

  String deriveName() {
    'any'
  }

  boolean isAnyEvent() {
    name == 'any'
  }

}