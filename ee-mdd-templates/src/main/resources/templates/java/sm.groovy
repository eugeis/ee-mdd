package templates.java

import ee.mdd.model.statemachine.StateMachine



templates('sm') {

  useMacros('commonMacros', '/common/macros')
  useMacros('macros')
  
    templates('controller',
    items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {
      template('controller', appendName: true, body: '''<% if(item.controller.base) { %><% c.className = "${item.capShortName}Controller" %> ${macros.generate('stateMachineController', c)}<% } %>''')
    
    }
      
      
    templates('event',
    items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {
    
      template('event', appendName: true, body: '''<% c.className = "${item.key.capitalize()}StateEvent" %> ${macros.generate('event', c)}''')
      template('implEvent', appendName: true, body: '''<% c.className = "${item.capShortName}StateEventImpl" %> ${macros.generate('implEvent', c)}''')
      template('eventType', appendName: true, body: '''<% c.className = "${item.key.capitalize()}StateEventType" %> ${macros.generate('eventType',c)}''')
      template('eventFactory', appendName: true, body: '''<% c.className = "${item.key.capitalize()}EventFactory" %> ${macros.generate('eventFactory',c)}''')
      template('implEventFactory', appendName: true, body: '''<% c.className = "${item.capShortName}EventFactoryBaseImpl" %> ${macros.generate('implEventFactory', c)}''')
      template('implEventFactoryExtends', appendName: true, body: '''<% c.className = "${item.capShortName}EventFactoryBaseImpl" %> ${macros.generate('implEventFactoryExtends', c)}''')
    }
    
    templates('typeEnums',
    items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {
      template('actionType', appendName: true, body: '''<% c.className = "${item.capShortName}StateActionType" %> ${macros.generate('actionType', c)}''')
      template('conditionType', appendName: true, body: '''<% c.className = "${item.capShortName}StateConditionType" %> ${macros.generate('conditionType', c)}''')
    }
}
