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
    template('viewModelExtends', appendName: true, body: '''<% if (item.viewModel) { %><% c.className = c.item.cap  %> ${macros.generate('viewModel', c)}<% } %>''')
  }
}
