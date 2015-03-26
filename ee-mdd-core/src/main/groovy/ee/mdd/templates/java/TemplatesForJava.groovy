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
import ee.mdd.model.component.Channel
import ee.mdd.model.component.Controller
import ee.mdd.model.component.EnumType





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

        template('ifc', appendName: true, body: '''<% if(c.item.base) { c.className = item.n.cap.base } else { c.className = item.cap } %><% c.serializable = true %>${macros.generate('ifc', c)}''')
        template('ifcExtends', appendName: true, body: '''<% if(c.item.base) { %>${macros.generate('ifcExtends', c)}<% } %>''')
      }

      items('modelApiBasicType',
      query: { c -> c.model.findAllRecursiveDown( { BasicType.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {

        template('ifc', appendName: true, body: '''<% if(c.item.base) { c.className = item.n.cap.base } else { c.className = item.cap } %> ${macros.generate('ifcBasicType', c)}''')
        template('ifcExtends', appendName: true, body: '''<% if(c.item.base) { %>${macros.generate('ifcExtends', c)}<% } %>''')
      }

      items ('modelImplEntity',
      query: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'impl' ] ) } ) {

        template('implEntity', appendName: true, body: '''<% c.virtual = c.item.virtual; c.base = true; c.metas = item.metas; c.serializable = true; c.className = item.n.cap.implBase %>${macros.generate('implEntity', c)}''')
        template('implEntityExtends', appendName: true, body: '''<% if(c.item.base) { %><% c.serializable = true; c.className = item.n.cap.impl %>${macros.generate('implEntityExtends', c)}<% } %>''')

      }

      items('modelEjbEntity',
      query: { c -> c.model.findAllRecursiveDown( {Entity.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'ejb' ] ) } ) {

        template('ejbEntity', appendName: true, body: '''<% if(c.item.base) { c.className = item.n.cap.baseEntity } else { c.className = item.n.cap.entity } %>${macros.generate('ejbEntity', c)}''')
        template('ejbEntityExtends', appendName: true, body: '''<% if(c.item.base) { c.className = item.n.cap.entity %>${macros.generate('ejbEntityExtends', c)}<% } %>''')
      }

      items('modelEjbBasicType',
      query: { c -> c.model.findAllRecursiveDown( {BasicType.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'ejb' ] ) } ) {

        template('ejbBasicType', appendName: true, body: '''<% if(c.item.base) {  c.className = item.n.cap.baseEmbeddable } else { c.className = item.n.cap.embeddable } %>${macros.generate('ejbBasicType', c)}''')
        template('ejbBasicTypeExtends', appendName: true, body: '''<% if(c.item.base) { %><% c.className = item.n.cap.embeddable %> ${macros.generate('ejbBasicTypeExtends', c)} <% } %>''')
      }

      items('facadeEjbService',
      query: { c -> c.model.findAllRecursiveDown( {Service.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'facade' ] ) } ) {

        template('ejbService', appendName: true, body: '''<% c.className = c.item.n.cap.serviceBaseBean %>${macros.generate('ejbService', c)}''')
        template('ejbServiceExtends', appendName: true, body: '''<% if (c.item.base) { %> <% c.className = c.item.n.cap.serviceBean %>${macros.generate('ejbServiceExtends', c)} <% } %>''')
      }

      items('implContainer',
      query: { c -> c.model.findAllRecursiveDown( {Container.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'impl' ] ) } ) {

        template('implContainer', appendName: true, body: '''<% if(!c.item.name.endsWith("Container")) { c.className = c.item.n.cap.containerBaseImpl } else { c.className = c.item.n.cap.baseImpl } %>${macros.generate('implContainer', c)}''')
        template('implContainerExtends', appendName: true, body: '''<% if (c.item.base) { %> <% if(!c.item.name.endsWith("Container")) { c.className = c.item.n.cap.containerImpl } else { c.className = c.item.cap.impl } %><% } %>''')
      }

      items ('modelTest',
      query: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'impl', scope: 'test'] ) } ) {

        template('test', appendName: true, body: '''<% c.virtual = true; c.className = "${item.n.cap.test}Base"; c.itemInit = "new $item.n.cap.impl()" %>${macros.generate('test', c)}''')
        template('testExtends', appendName: true, body: '''<% c.className = item.n.cap.test %>${macros.generate('testExtends', c)}''')
      }

      items ('enum',
      query: { c -> c.model.findAllRecursiveDown( { EnumType.isInstance(it) }) },
      before: { c -> def enumType = c.item; c.putAll( [ component: enumType.component, module: enumType.module, enumType: enumType ] ) } ) {

        template('enum', appendName: true, body: '''${macros.generate('enum', c)}''')
      }

      items ('enumTest',
      query: { c -> c.model.findAllRecursiveDown( { EnumType.isInstance(it) }) },
      before: { c -> def enumType = c.item; c.putAll( [ component: enumType.component, module: enumType.module, enumType: enumType, scope: 'test' ] ) } ) {

        template('testEnum', appendName: true, body: '''<% c.className = "${item.n.cap.test}Base" %>${macros.generate('testEnum', c)}''')
        template('testEnumExtends', appendName: true, body: '''<% c.className = item.n.cap.test %>${macros.generate('testExtends', c)}''')
      }

      //logic
      items ('logicApi',
      query: { c -> c.model.findAllRecursiveDown( { Controller.isInstance(it) || Service.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {

        template('ifc', appendName: true, body: '''<% c.className = "${item.cap}Base" %>${macros.generate('ifc', c)}''')
        template('ifcExtends', appendName: true, body: '''<% if (c.item.base) { %><% c.className = item.cap %> ${macros.generate('ifcExtends', c)}<% } %>''')
      }

      items ('container',
      query: { c -> c.model.findAllRecursiveDown( { Container.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {

        template('ifcContainer', appendName: true, body: '''<% c.className = "${item.cap}Base" %>''')
        template('ifcContainerExtends', appendName: true, body: '''<% if (c.item.base) { %><% c.className = item.cap %> ${macros.generate('ifcContainerExtends', c)}<% } %>''')
      }

      items ('jmsToCdi',
      query: { c -> c.model.findAllRecursiveDown( { Channel.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'integ' ] ) } ) {

        template('jmsToCdi', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.jmsToCdi %> ${macros.generate('jmsToCdi', c)}<% } %>''')
        template('jmsToCdiMdb', appendName: true, body: '''<% def cachedContainers = module.containers.findAll { it.controller.cache }%><% if (cachedContainers || module.configs) { %><% c.className = c.item.n.cap.jmsToCdiMdb %> ${macros.generate('jmsToCdiMdb', c)}<% } %>''')
        template('notificationPlugin', appendName: true, body: '''<% def modules = []; modules.addAll(component.backends.findAll { m -> m.entities }) %><% if(modules) { %><% c.className = component.n.cap.notificationPlugin %> ${macros.generate('notificationPlugin', c)} <% } %> ''')

      }

      items('cdiToJms',
      query: { c -> c.model.findAllRecursiveDown( { Channel.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'integ/ejb' ] ) } ) {

        template('cdiToJms', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.cdiToJms %> ${macros.generate('cdiToJms', c)}<% } %>''')
      }

      items('eventToCdi',
      query: { c -> c.model.findAllRecursiveDown( { Channel.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'integ' ] ) } ) {

        template('eventToCdi', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.eventToCdiBase %> ${macros.generate('eventToCdi', c)}<% } %>''')
        template('eventToCdiExtends', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.eventToCdi %> ${macros.generate('eventToCdiExtends', c)}<% } %>''')
        template('eventToCdiExternal', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.eventToCdiExternalBase %> ${macros.generate('eventToCdiExternal', c)}<% } %>''')
        template('eventToCdiExternalExtends', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.eventToCdiExternal %> ${macros.generate('eventToCdiExternalExtends', c)}<% } %>''')
      }

    }
  }
}
