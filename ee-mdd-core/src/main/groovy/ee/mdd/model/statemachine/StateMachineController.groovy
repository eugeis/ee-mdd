package ee.mdd.model.statemachine

import ee.mdd.model.component.Controller
import ee.mdd.model.component.Entity
import ee.mdd.model.component.Operation

class StateMachineController extends Controller {
  StateMachine getStateMachine() {
    parent
  }

  Entity getEntity() {
    stateMachine.entity
  }

// Resolution of property ret (type) does not work here
  
//  boolean init() {
//    boolean ret = super.init()
//
//    def op = new Operation(name: 'process', ret: entity.n.cap.entity, parent:this)
//    op.add(new Param(name: 'event', type: stateMachine.n.cap.event))
//    add(op, true)
//    add(new Operation(name: 'findStateMetaModel', ret: stateMachine.names.metaModel, parent:this), true)
//
//    ret
//  }
}