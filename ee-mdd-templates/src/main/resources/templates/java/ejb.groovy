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

import static ee.mdd.generator.OutputType.*
import static ee.mdd.generator.OutputPurpose.*

import ee.mdd.model.component.Facade

/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */

templates('ejb') {

  useMacros('commonMacros', '/common/macros')
  useMacros('macros')

  templates ('facadeEjbService', type: LOGIC,
  items: { c -> c.model.findAllRecursiveDown( { Facade.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'facade' ] ) } ) {

    template('serviceBaseBean', appendName: true, body: '''<% if (c.item.base) { c.className = c.item.n.cap.baseBean  } else { c.className = c.item.n.cap.bean } %>${macros.generate('serviceBaseBean', c)}''')
    template('serviceBean', appendName: true, body: '''<% if (c.item.base) { %><% c.className = c.item.n.cap.bean %>${macros.generate('serviceBean', c)} <% } %>''')
  }
}
