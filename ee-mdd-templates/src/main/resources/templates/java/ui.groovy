package templates.java

import ee.mdd.model.ui.View

templates('ui') {

  useMacros('commonMacros', '/common/macros')
  useMacros('macros')

  templates ('viewInterface',
  items: { c -> c.model.findAllRecursiveDown( { View.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {

    template('viewInterface', appendName: true, body: '''<% c.className = c.item.n.cap.viewBase  %> ${macros.generate('viewInterfaceBase', c)}''')
    template('viewInterfaceExtends', appendName: true, body: '''<% c.className = c.item.n.cap.view%> ${macros.generate('viewInterface', c)}''')
  }
}
