package templates.java

import ee.mdd.model.ui.View

templates('ui') {

  useMacros('commonMacros', '/common/macros')
  useMacros('macros')

  templates ('viewInterface',
  items: { c -> c.model.findAllRecursiveDown( { View.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {

    template('viewInterface', appendName: true, body: '''<% c.className = c.item.n.cap.base %> ${macros.generate('viewInterfaceBase', c)}''')
    template('viewInterfaceExtends', appendName: true, body: '''<% c.className = c.item.cap %> ${macros.generate('viewInterface', c)}''')
  }

  templates ('viewModel',
  items: { c -> c.model.findAllRecursiveDown( { View.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {

    template('viewModel', appendName: true, body: '''<% if (item.model) { %><% c.className = item.model.n.cap.base %> ${macros.generate('viewModelBase', c)}<% } %>''')
    template('viewModelExtends', appendName: true, body: '''<% if (item.model) { %><% c.className = item.model.cap  %> ${macros.generate('viewModel', c)}<% } %>''')
  }
  
  templates ('modelEventForwarder',
  items: { c -> c.model.findAllRecursiveDown( {View.isInstance(it) }) },
  context: { c -> c.putAll( [component: c.item.component, module: c.item.module] ) } ) {  
  
    template('modelEventForwarder', appendName: true, body: '''<% if (item.model) { %><% c.className = item.model.n.cap.eventsBase%> ${macros.generate('modelEventForwarderBase', c)}<% } %>''')
    template('modelEventForwarderExtends', appendName: true, body: '''<% if (item.model) { %><% c.className = item.model.n.cap.events%> ${macros.generate('modelEventForwarder', c)}<% } %>''')
  }
  
  templates ('presenter',
  items: { c -> c.model.findAllRecursiveDown( {View.isInstance(it) }) },
  context: { c -> c.putAll( [component: c.item.component, module: c.item.module] ) } ) {
    
    template('presenter', appendName: true, body: '''<% c.className = item.model.n.cap.presenterBase %> ${macros.generate('presenterBase', c)}''')
    //template('presenterExtends', appendName: true, body: '''<% c.className = item.model.n.cap.presenter%> ${macros.generate('presenter', c)}''')
  }
}
