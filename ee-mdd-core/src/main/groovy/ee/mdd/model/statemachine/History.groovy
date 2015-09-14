package ee.mdd.model.statemachine

import ee.mdd.model.component.Commands
import ee.mdd.model.component.Entity
import ee.mdd.model.component.LogicUnit
import ee.mdd.model.component.Module
import ee.mdd.model.component.Prop

class History extends LogicUnit {
  String entityRef, oldStateProp, newStateProp, actorProp, actionProp, dateProp, reasonProp, stateMachineEntityHistoryEntriesProp
  Entity _entity
  Prop _oldState, _newState, _actor, _action, _dateOfOccurrence, _reason, _stateMachineEntityHistoryEntriesProp

  String deriveName() {
    "${parent.cap}Manager"
  }

  StateMachine getStateMachine() {
    parent
  }
  
  Module getModule() {
    parent.module
  }
  
  Entity getEntity() {
    if(!_entity) {
      assert entityRef, "entityRef is not defined in '$this'"
      _entity = module.resolve(entityRef, Entity.class, true)
    }; _entity
  }
  Commands getManager() {
    entity.manager
  }
  Prop getOldState() {
    _oldState = findProp(oldStateProp, _oldState)
  }
  Prop getNewState() {
    _newState = findProp(newStateProp, _newState)
  }
  Prop getActor() {
    _actor = findProp(actorProp, _actor)
  }
  Prop getAction() {
    _action = findProp(actionProp, _action)
  }
  Prop getDateOfOccurrence() {
    _dateOfOccurrence = findProp(dateProp, _dateOfOccurrence)
  }
  Prop getReason() {
    _reason = findProp(reasonProp, _reason)
  }
  Prop getStateMachineEntityHistoryEntries() {
    if(!_stateMachineEntityHistoryEntriesProp) {
      _stateMachineEntityHistoryEntriesProp = stateMachine.entity.resolve(stateMachineEntityHistoryEntriesProp)
    }; _stateMachineEntityHistoryEntriesProp
  }

  protected Prop findProp(String propKey, Prop prop) {
    if (prop) {
      return prop
    }
    if (!propKey) {
      return null
    }
    entity.resolveProp(propKey)
  }
}