package ee.mdd.model.statemachine

import ee.mdd.model.component.Controller
import ee.mdd.model.component.Entity
import ee.mdd.model.component.Operation
import ee.mdd.model.component.Param

class StateMachineController extends Controller {
  StateMachine getStateMachine() {
    parent
  }

  Entity getEntity() {
    stateMachine.entity
  }


  boolean init() {
    boolean ret = super.init()

    def op = new Operation(name: 'process', nameExternal: "process${stateMachine.capShortName}StateEvent", returnType: entity.names.bean, returnTypeExternal: entity.names.clazz, parent:this)
    op.add(new Param(name: 'event', type: stateMachine.names.event))
    add(op, true)
    add(new Operation(name: 'findStateMetaModel', nameExternal: "find${stateMachine.capShortName}StateMetaModel", returnType: stateMachine.names.metaModel, parent:this), true)

    ret
  }
}