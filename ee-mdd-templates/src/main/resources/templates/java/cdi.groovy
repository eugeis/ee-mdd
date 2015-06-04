import ee.mdd.model.component.Channel

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

templates('cdi') {

  templates ('cdiToJms',
  items: { c -> c.model.findAllRecursiveDown( { Channel.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'integ/ejb' ] ) } ) {

    template('cdiToJms', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.cdiToJms %> ${macros.generate('cdiToJms', c)}<% } %>''')
  }

  templates ('eventToCdi',
  items: { c -> c.model.findAllRecursiveDown( { Channel.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'integ' ] ) } ) {

    template('eventToCdi', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.eventToCdiBase %> ${macros.generate('eventToCdi', c)}<% } %>''')
    template('eventToCdiExtends', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.eventToCdi %> ${macros.generate('eventToCdiExtends', c)}<% } %>''')
    template('eventToCdiExternal', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.eventToCdiExternalBase %> ${macros.generate('eventToCdiExternal', c)}<% } %>''')
    template('eventToCdiExternalExtends', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.eventToCdiExternal %> ${macros.generate('eventToCdiExternalExtends', c)}<% } %>''')
  }
}
