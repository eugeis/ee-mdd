package ee.mdd.model.statemachine

import ee.mdd.model.component.Pojo
import ee.mdd.model.component.Prop

class Context extends Pojo {

  String deriveName() {
    "${module().capShortName}Context"
  }

  boolean init() {
    boolean ret = super.init()

    //    if(!types) {
    //      types = []
    //    }
    //
    //    types << 'static com.siemens.ra.cg.pl.common.base.util.TimeUtils.*'
    //    types << 'com.siemens.ra.cg.pl.common.base.model.SessionPrincipal'
    //    types << 'java.util.ArrayList'

    add(new Prop(name: 'sessionPrincipal', type: 'SessionPrincipal'))
    add(new Prop(name: 'event', type: parent.names.event))
    add(new Prop(name: 'redirectEvent', type: parent.names.event))
    add(new Prop(name: 'transitions', type: "$parent.names.transitionExecutionResult<?>", multi: true, defaultValue: 'new ArrayList<>()'))
    add(new Prop(name: 'currentTransition', type: "$parent.names.transitionExecutionResult<?>"))

    if(parent.isTimeoutEnabled()) {
      add(new Prop(name: 'timeout', type: 'Date'))
      add(new Prop(name: 'newTimeout', type: 'Date'))
    }

    add(new Prop(name: parent.entity.names.instance, type: parent.entity.names.clazz))
    add(new Prop(name: 'state', type: parent.stateProp.type))
    add(new Prop(name: 'newState', type: parent.stateProp.type))

    ret
  }
}