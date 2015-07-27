package ee.mdd.model.statemachine

import ee.mdd.model.component.LogicUnit

class State extends LogicUnit {
  List<String> toBeNotified = []
  List<Transition> transitions = []
  List<String> entryActions = []
  List<String> exitActions = []
  Integer timeoutInMillis = null
  List<EventTransitions> eventTransitions
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

  StateMachine getStateMachine() {
    parent
  }

  def add(Transition item) {
    super.add(item); transitions << item
  }

  void buildChildren() {
    super.buildChildren()

    eventTransitions = transitions.findAll { !it.anyEvent }.groupBy { it.event }.collect { event, trs ->
      if(!event) {
        println "Event is null for transition $trs.name"
      }
      new EventTransitions(event: event, transitions: trs)
    }

    transitions.findAll { it.anyEvent }.reverse().each { tr ->
      eventTransitions.each { etrs ->
        etrs.transitions.add(0, tr)
      }
    }

    //do not add entry actions in the all action lists, because they are needed in the prev. state processor only
    //but add entry action of all next states instead
    actions = transitions.collectMany { it.actionObjs }
    conditions = transitions.collectMany { it.conditionObjs }
    conditions.addAll(transitions.collectMany { it.notConditionObjs })
    conditions = conditions.toSet() as List
    conditions = conditions.sort(false) { it.name }

    actions.addAll(exitActionObjs)
    actions.addAll(transitions.findAll { it.state.entryActionObjs }.collectMany { it.state.entryActionObjs })
    actions = actions.toSet() as List
    actions = actions.sort(false) { it.name }
  }

  void setTimeout(String timeout) {
    timeoutInMillis = timeout.duration()
  }

  boolean isTimeoutEnabled() { stateMachine.timeoutEnabled && timeoutInMillis != null }
}