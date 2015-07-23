package templates.java

import ee.mdd.model.ui.View

templates('ui') {

  templates ('viewInterface',
  items: { c -> c.model.findAllRecursiveDown( { View.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {

    template('viewInterface', appendName: true, body: '''<% c.className = c.item.n.cap.viewBase  %> ${macros.generate('viewInterfacsseBase', c)}''')
    template('viewInterfaceExtends', appendName: true, body: '''<% c.className = c.item.n.cap.view%> ${macros.generate('viewInterfacsse', c)}''')
  }
}
