import ee.mdd.model.component.Channel
import ee.mdd.model.component.Entity
import ee.mdd.model.component.EnumType

/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */
 
templates('test') {
   
  templates ('modelTest',
  items: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'impl', scope: 'test'] ) } ) {
   
    template('test', appendName: true, body: '''<% c.virtual = true; c.className = "${item.n.cap.test}Base"; c.itemInit = "new $item.n.cap.impl()" %>${macros.generate('test', c)}''')
    template('testExtends', appendName: true, body: '''<% c.className = item.n.cap.test %>${macros.generate('testExtends', c)}''')
  }
     
  templates ('bridgeTests',
  items: { c -> c.model.findAllRecursiveDown( { Channel.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'integ' ] ) } ) {
     
    template('notificationPluginTest', appendName: true, body: '''<% def modules = []; modules.addAll(component.backends.findAll { m -> m.entities }) %><% if(modules) { %><% c.className = c.item.n.cap.notificationPluginTest %> ${macros.generate('notificationPluginTest', c)}<% } %>''')
    template('jmsToCdiTest' , appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.jmsToCdiTest %> ${macros.generate('jmsToCdiTest', c)} <% } %>''')
    template('cdiToJmsTest', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.cdiToJmsTest %> ${macros.generate('cdiToJmsTest', c)} <% } %>''')
    template('eventToCdiTest', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.eventToCdiTest %> ${macros.generate('eventToCdiTest', c)} <% } %>''')
  }
  
  templates ('enumTest',
  items: { c -> c.model.findAllRecursiveDown( { EnumType.isInstance(it) }) },
  context: { c -> def enumType = c.item; c.putAll( [ component: enumType.component, module: enumType.module, enumType: enumType, scope: 'test' ] ) } ) {
  
    template('testEnum', appendName: true, body: '''<% c.className = "${item.n.cap.test}Base" %>${macros.generate('testEnum', c)}''')
    template('testEnumExtends', appendName: true, body: '''<% c.className = item.n.cap.test %>${macros.generate('testExtends', c)}''')
  }
  
}