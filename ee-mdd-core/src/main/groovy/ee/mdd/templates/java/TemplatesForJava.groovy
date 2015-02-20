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
package ee.mdd.templates.java

import ee.mdd.builder.GeneratorBuilder
import ee.mdd.generator.Generator
import ee.mdd.model.component.BasicType
import ee.mdd.model.component.Container
import ee.mdd.model.component.Controller
import ee.mdd.model.component.Entity
import ee.mdd.model.component.EnumType
import ee.mdd.model.component.Service





/**
 *
 * @author Eugen Eisler
 */
class TemplatesForJava {

  static Generator build() {
    def model = new GeneratorBuilder().generator('javaEe') {
      //model
      items ('modelApi',
      query: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {

        template('ifc', body: '''<% if(c.item.base) { c.className = item.n.cap.base } else { c.className = item.cap } %><% c.serializable = true %>${macros.generate('ifc', c)}''')
        template('ifcExtends', body: '''<% if(c.item.base) { %>${macros.generate('ifcExtends', c)}<% } %>''')
      }

      items('modelApiBasicType',
      query: { c -> c.model.findAllRecursiveDown( { BasicType.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {

        template('ifc', body: '''<% if(c.item.base) { c.className = item.n.cap.base } else { c.className = item.cap } %> ${macros.generate('ifcBasicType', c)}''')
        template('ifcExtends', body: '''<% if(c.item.base) { %>${macros.generate('ifcExtends', c)}<% } %>''')
      }

      items ('modelImplEntity',
      query: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'impl' ] ) } ) {

        template('implEntity', body: '''<% c.virtual = c.item.virtual; c.base = true; c.metas = item.metas; c.serializable = true; c.className = item.n.cap.implBase %>${macros.generate('implEntity', c)}''')
        template('implEntityExtends', body: '''<% if(c.item.base) { %><% c.serializable = true; c.className = item.n.cap.impl %>${macros.generate('implEntityExtends', c)}<% } %>''')

      }

      items('modelEjbEntity',
      query: { c -> c.model.findAllRecursiveDown( {Entity.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'ejb' ] ) } ) {

        template('ejbEntity', body: '''<% if(c.item.base) { c.className = item.n.cap.baseEntity } else { c.className = item.n.cap.entity } %>${macros.generate('ejbEntity', c)}''')
        template('ejbEntityExtends', body: '''<% if(c.item.base) { c.className = item.n.cap.entity %>${macros.generate('ejbEntityExtends', c)}<% } %>''')
      }

      items('modelEjbBasicType',
      query: { c -> c.model.findAllRecursiveDown( {BasicType.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'ejb' ] ) } ) {

        template('ejbBasicType', body: '''<% if(c.item.base) {  c.className = item.n.cap.baseEmbeddable } else { c.className = item.n.cap.embeddable } %>${macros.generate('ejbBasicType', c)}''')
        template('ejbBasicTypeExtends', body: '''<% if(c.item.base) { %><% c.className = item.n.cap.embeddable %> ${macros.generate('ejbBasicTypeExtends', c)} <% } %>''')
      }

      items('facadeEjbService',
      query: { c -> c.model.findAllRecursiveDown( {Service.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'facade' ] ) } ) {

        template('ejbService', body: '''<% c.className = c.item.n.cap.serviceBaseBean %>${macros.generate('ejbService', c)}''')
        template('ejbServiceExtends', body: '''<% if (c.item.base) { %> <% c.className = c.item.n.cap.serviceBean %>${macros.generate('ejbServiceExtends', c)} <% } %>''')
      }

      items('implContainer',
      query: { c -> c.model.findAllRecursiveDown( {Container.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'impl' ] ) } ) {

        template('implContainer', body: '''<% if(!c.item.name.endsWith("Container")) { c.className = c.item.n.cap.containerBaseImpl } else { c.className = c.item.n.cap.baseImpl } %>${macros.generate('implContainer', c)}''')
        template('implContainerExtends', body: '''<% if (c.item.base) { %> <% if(!c.item.name.endsWith("Container")) { c.className = c.item.n.cap.containerImpl } else { c.className = c.item.cap.impl } %><% } %>''')
      }

      items ('modelTest',
      query: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'impl', scope: 'test'] ) } ) {

        template('test', body: '''<% c.virtual = true; c.className = "${item.n.cap.test}Base"; c.itemInit = "new $item.n.cap.impl()" %>${macros.generate('test', c)}''')
        template('testExtends', body: '''<% c.className = item.n.cap.test %>${macros.generate('testExtends', c)}''')
      }

      items ('enum',
      query: { c -> c.model.findAllRecursiveDown( { EnumType.isInstance(it) }) },
      before: { c -> def enumType = c.item; c.putAll( [ component: enumType.component, module: enumType.module, enumType: enumType ] ) } ) {

        template('enum', body: '''${macros.generate('enum', c)}''')
      }

      items ('enumTest',
      query: { c -> c.model.findAllRecursiveDown( { EnumType.isInstance(it) }) },
      before: { c -> def enumType = c.item; c.putAll( [ component: enumType.component, module: enumType.module, enumType: enumType, scope: 'test' ] ) } ) {

        template('testEnum', body: '''<% c.className = "${item.n.cap.test}Base" %>${macros.generate('testEnum', c)}''')
        template('testEnumExtends', body: '''<% c.className = item.n.cap.test %>${macros.generate('testExtends', c)}''')
      }

      //logic
      items ('logicApi',
      query: { c -> c.model.findAllRecursiveDown( { Controller.isInstance(it) || Service.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {

        template('ifc', body: '''<% c.className = "${item.cap}Base" %>${macros.generate('ifc', c)}''')
        template('ifcExtends', body: '''<% if (c.item.base) { %><% c.className = item.cap %> ${macros.generate('ifcExtends', c)}<% } %>''')
      }
    }
  }
}