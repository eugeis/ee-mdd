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

  State getFromState() {
    parent instanceof State ? parent : null
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

  void buildMe() {
    super.buildMe()
    if(!anyEvent) {
      event = stateMachine.resolve(name, Event.class, true)
    }
    if(redirectEvent) {
      redirect = stateMachine.resolve(redirectEvent, Event.class, true)
    }
    if(!to) {
      to = parent.name
    }
    state = stateMachine.resolve(to, State.class, true)
    conditionObjs = conditions.collect { stateMachine.resolve(it, Condition.class, true) }.findAll { it }
    notConditionObjs = notConditions.collect { stateMachine.resolve(it, Condition.class, true) }.findAll { it }
    actionObjs = actions.collect { stateMachine.resolve(it, Action.class, true) }.findAll{ it }
    groupObjs = groups.collect { component.realm.resolve(it, RealmGroup.class, false) }.findAll{ it }

    allActions = []
    allActions.addAll(fromState.exitActionObjs)
    for(action in actionObjs) {
      if(!allActions.contains(action)) {
        allActions << action
      }
    }
    for(action in state.entryActionObjs) {
      if(!allActions.contains(action)) {
        allActions << action
      }
    }
  }
}