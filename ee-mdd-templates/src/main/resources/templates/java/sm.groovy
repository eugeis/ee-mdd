package templates.java

import ee.mdd.model.statemachine.StateMachine



templates('sm') {

  useMacros('commonMacros', '/common/macros')
  useMacros('macros')

    templates('event',
    items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {
    
      template('stateEvent', appendName: true, body: '''<% c.className = "${item.key.capitalize()}StateEvent" %> ${macros.generate('stateEvent', c)}''')
      template('moduleEventType', appendName: true, body: '''<% c.className = "${item.key.capitalize()}EventType" %> ${macros.generate('eventType',c)}''')
  
    }
    

}
