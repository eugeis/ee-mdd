import ee.mdd.model.component.BasicType
import ee.mdd.model.component.Entity


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

templates('jpa') {

  templates ('basicType',
  items: { c -> c.model.findAllRecursiveDown( {BasicType.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'ejb' ] ) } ) {

    template('basicTypeBase', appendName: true, body: '''<% if(c.item.base) {  c.className = item.n.cap.baseEmbeddable } else { c.className = item.n.cap.embeddable } %>${macros.generate('basicTypeBaseBean', c)}''')
    template('basicTypeBean', appendName: true, body: '''<% if(c.item.base) { %><% c.className = item.n.cap.embeddable %> ${macros.generate('basicTypeBean', c)} <% } %>''')
  }

  templates ('entity',
  items: { c -> c.model.findAllRecursiveDown( {Entity.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'ejb' ] ) } ) {

    template('entityBaseBean', appendName: true, body: '''<% if(c.item.base) { c.className = item.n.cap.baseEntity } else { c.className = item.n.cap.entity } %>${macros.generate('entityBaseBean', c)}''')
    template('entityBean', appendName: true, body: '''<% if(c.item.base) { c.className = item.n.cap.entity %>${macros.generate('entityBean', c)}<% } %>''')
  }
}
