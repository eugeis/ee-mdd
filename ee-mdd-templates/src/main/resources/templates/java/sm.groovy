package templates.java

import ee.mdd.model.statemachine.StateMachine



templates('sm') {

  useMacros('commonMacros', '/common/macros')
  useMacros('macros')

    templates('moduleEvent',
    items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    
      template('moduleEvent', appendName: true, body: '''<% c.className = "${item.key}StateEvent" %> ${macros.generate('moduleEvent', c)}''')
  
    }
    

}
