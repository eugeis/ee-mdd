package templates.java

import ee.mdd.model.statemachine.StateMachine







templates('sm') {

  useMacros('commonMacros', '/common/macros')
  useMacros('macros')
  
    templates('metas',
    items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {
      template('metaModel', appendName: true, body: '''<% c.className = "${item.capShortName}StateMetaModel" %> ${macros.generate('metaModel', c)}''')
      template('metaState', appendName: true, body: '''<% c.className = "${item.capShortName}MetaState" %> ${macros.generate('metaState', c)}''')
    }
    
    templates('typeEnums',
    items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {
      template('actionType', appendName: true, body: '''<% c.className = "${item.capShortName}StateActionType" %> ${macros.generate('actionType', c)}''')
      template('conditionType', appendName: true, body: '''<% c.className = "${item.capShortName}StateConditionType" %> ${macros.generate('conditionType', c)}''')
    }
      
    templates('controller',
    items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {
      template('controller', appendName: true, body: '''<% c.className = "${item.capShortName}ControllerBase" %> ${macros.generate('stateMachineController', c)}''')
      template('controllerExtends', appendName: true, body: '''<% c.className = "${item.capShortName}Controller" %> ${macros.generate('stateMachineControllerExtends', c)}''')
      template('implStateMachineController', appendName: true, body: '''<% c.className = "${item.capShortName}ControllerBaseImpl" %> ${macros.generate('implStateMachineController', c)}''')
      template('implStateMachineControllerExtends', appendName: true, body: '''<% c.className = "${item.capShortName}ControllerImpl" %> ${macros.generate('implStateMachineControllerExtends', c)}''')
    }
      
      
    templates('event',
    items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {
    
      template('event', appendName: true, body: '''<% c.className = "${item.key.capitalize()}StateEvent" %> ${macros.generate('event', c)}''')
      template('implEvent', appendName: true, body: '''<% c.className = "${item.capShortName}StateEventImpl" %> ${macros.generate('implEvent', c)}''')
      template('eventType', appendName: true, body: '''<% c.className = "${item.key.capitalize()}StateEventType" %> ${macros.generate('eventType', c)}''')
      template('eventFactory', appendName: true, body: '''<% c.className = "${item.key.capitalize()}EventFactory" %> ${macros.generate('eventFactory', c)}''')
      template('implEventFactory', appendName: true, body: '''<% c.className = "${item.capShortName}EventFactoryBaseImpl" %> ${macros.generate('implEventFactory', c)}''')
      template('implEventFactoryExtends', appendName: true, body: '''<% c.className = "${item.capShortName}EventFactoryBaseImpl" %> ${macros.generate('implEventFactoryExtends', c)}''')
    }
    
    templates('stateEventProcessor',
    items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {
    
      template('stateEventProcessor', appendName: true, body: '''<% c.className = "${item.capShortName}StateEventProcessor" %> ${macros.generate('stateEventProcessor', c)}''')
      template('implStateEventProcessor', appendName: true, body: '''<% c.className = "${item.capShortName}StateEventProcessorBaseImpl" %> ${macros.generate('implStateEventProcessor', c)}''')
      template('implStateEventProcessorExtends', appendName: true, body: '''<% c.className = "${item.capShortName}StateEventProcessorImpl" %> ${macros.generate('implStateEventProcessorExtends', c)}''')
    }
    
    templates('context',
    items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {
    
      template('context', appendName: true, body: '''<% c.className = "${item.capShortName}ContextBase" %> ${macros.generate('context', c)}''')
      template('contextExtends', appendName: true, body: '''<% c.className = "${item.capShortName}Context" %> ${macros.generate('contextExtends', c)}''')
    }
    
    templates('contextManager',
    items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {
    
      template('contextManager', appendName: true, body: '''<% c.className = "${item.capShortName}ContextManagerBase" %> ${macros.generate('contextManager', c)}''')
      template('contextManagerExtends', appendName: true, body: '''<% c.className = "${item.capShortName}ContextManager" %> ${macros.generate('contextManagerExtends', c)}''')
      template('implContextManager', appendName: true, body: '''<% c.className = "${item.capShortName}ContextManagerBaseImpl" %> ${macros.generate('implContextManager', c)}''')
      template('implContextManagerExtends', appendName: true, body: '''<% c.className = "${item.capShortName}ContextManagerImpl" %> ${macros.generate('implContextManagerExtends', c)}''')
    }
    
    templates('actionExecutor',
    items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {
    
      template('actionExecutor', appendName: true, body:'''<% c.className = "${item.capShortName}ActionExecutor" %> ${macros.generate('actionExecutor', c)}''')
    }
}
