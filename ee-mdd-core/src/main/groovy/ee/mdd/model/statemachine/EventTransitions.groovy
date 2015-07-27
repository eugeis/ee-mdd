package ee.mdd.model.statemachine


class EventTransitions {
  Event event
  List<Transition> transitions
  Transition getTransition() {
    transitions[0]
  }

  /** do we need if,.., else switch for transition(s) of the event? */
  boolean isConditions() {
    transitions && (transitions.size() > 1 || transition.conditionObjs)
  }
}