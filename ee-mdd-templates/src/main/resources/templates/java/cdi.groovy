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


import ee.mdd.model.component.Channel
import ee.mdd.model.component.Module

import static ee.mdd.generator.OutputType.INTEG

/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */

templates('cdi') {

  useMacros('commonMacros', '/common/macros')
  useMacros('macros')

  templates ('cdiToJms', type: INTEG,
  init: { c -> c.model.findAllDown({ Channel.isInstance(it) }).each { it.n.cap.addAll(['cdiToJms'], 'integ.ejb') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Channel) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {

    template('cdiToJms', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.cdiToJms %> ${macros.generate('cdiToJms', c)}<% } %>''')
  }

  templates ('eventToCdi', type: INTEG,
  init: { c -> c.model.findAllDown({ Channel.isInstance(it) }).each { it.n.cap.addAll(['eventToCdiBase', 'eventToCdi', 'eventToCdiExternalBase', 'eventToCdiExternal'], 'integ') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Channel) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {

    template('eventToCdi', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.eventToCdiBase %> ${macros.generate('eventToCdi', c)}<% } %>''')
    template('eventToCdiExtends', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.eventToCdi %> ${macros.generate('eventToCdiExtends', c)}<% } %>''')
    template('eventToCdiExternal', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.eventToCdiExternalBase %> ${macros.generate('eventToCdiExternal', c)}<% } %>''')
    template('eventToCdiExternalExtends', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.eventToCdiExternal %> ${macros.generate('eventToCdiExternalExtends', c)}<% } %>''')
  }
  
  templates('cdiToAal', type: INTEG,
  init: { c -> c.model.findAllDown({ Module.isInstance(it) }).each { it.n.cap.addAll(['cdiToAal'], 'ejb') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Module) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('cdiToAal', appendName: true, body: '''<% def aalEntities = module.entities.findAll {it.aal && !it.virtual}; def aalContainers = module.containers.findAll{it.aal} %><% if(aalEntities || aalContainers) { %><% c.className = module.n.cap.cdiToAal %> ${macros.generate('cdiToAal', c)} <% } %>''')
  }
    
}
