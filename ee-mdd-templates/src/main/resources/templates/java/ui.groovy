package templates.java

import static ee.mdd.generator.OutputPurpose.*
import static ee.mdd.generator.OutputType.*
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

    template('presenter', appendName: true, body: '''<% c.className = item.n.cap.presenterBase-"View" %> ${macros.generate('presenterBase', c)}''')
    template('presenterExtends', appendName: true, body: '''<% c.className = item.n.cap.presenter-"View" %> ${macros.generate('presenter', c)}''')
    template('presenterEventForwarder', appendName: true, body: '''<% if (item.presenter.withMediator) { %><% c.className = item.n.cap.presenterEventsBase-"View" %> ${macros.generate('presenterEventForwarderBase', c)}<% } %>''')
    template('presenterEventForwarderExtends', appendName: true, body: '''<% if (item.presenter.withMediator) { %><% c.className = item.n.cap.presenterEvents-"View" %> ${macros.generate('presenterEventForwarder', c)}<% } %>''')
  }

  templates ('mediator',
  items: { c -> c.model.findAllRecursiveDown( {View.isInstance(it) }) },
  context: { c -> c.putAll( [component: c.item.component, module: c.item.module] ) } ) {

    template('mediator', appendName: true, body: '''<% if (item.view.withMediator) { %><% c.className = item.n.cap.mediatorBase-"View" %> ${macros.generate('mediatorBase', c)}<% } %>''')
    template('mediatorExtends', appendName: true, body: '''<% if (item.view.withMediator) { %><% c.className = item.n.cap.mediator-"View" %> ${macros.generate('mediator', c)}<% } %>''')
  }

  templates ('guido',
  items: { c -> c.model.findAllRecursiveDown( {View.isInstance(it) }) },
  context: { c -> c.putAll( [component: c.item.component, module: c.item.module] ) } ) {

    template('dialogGuido', appendName: true, body: '''<% if (item.dialog) { %><% c.className = item.dialog.n.cap.guidoBase-"View" %> ${macros.generate('dialogGuido', c)}<% } %>''')
    template('dialogGuidoExtends', appendName: true, body: '''<% if (item.dialog) { %><% c.className = item.dialog.n.cap.guido-"View" %> ${macros.generate('dialogGuidoExtends', c)}<% } %>''')
    template('viewGuido', appendName: true, body: '''<% c.className = item.n.cap.guidoBase %> ${macros.generate('viewGuido', c)} ''')
    template('dialogDriver', appendName: true, body: '''<% if(item.dialog) { %><% c.className = item.dialog.n.cap.driverBase %> ${macros.generate('dialogDriver', c)} <% } %>''')
    template('dialogDriverExtends', appendName: true, body: '''<% if(item.dialog) { %><% c.className = item.dialog.n.cap.driver %> ${macros.generate('dialogDriverExtends', c)} <% } %>''')
    template('viewDriverGuido', appendName: true, body: '''<% c.className = item.n.cap.driverBase %> ${macros.generate('viewDriverGuido', c)} ''')
    template('viewDriverGuidoExtends', appendName: true, body: '''<% c.className = item.n.cap.driver %> ${macros.generate('viewDriverGuidoExtends', c)} ''')
  }
  
  templates('fx',
  items: { c -> c.model.findAllRecursiveDown( {View.isInstance(it) }) },
  context: { c -> c.putAll( [component: c.item.component, module: c.item.module] ) } ) {
    template('fxDialog', appendName: true, body: '''<% if (item.dialog) { %><% c.className = item.n.cap.fxBase %> ${macros.generate('fxDialog', c)} <% } %>''')
    template('fxDialogExtends', appendName: true, body: '''<% if (item.dialog) { %><% c.className = item.n.cap.fx %> ${macros.generate('fxDialogExtends', c)} <% } %>''')
  }
    
}
