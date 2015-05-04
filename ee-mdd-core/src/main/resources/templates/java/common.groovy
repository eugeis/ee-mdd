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

import ee.mdd.model.component.BasicType
import ee.mdd.model.component.Channel
import ee.mdd.model.component.Container
import ee.mdd.model.component.Controller
import ee.mdd.model.component.Entity
import ee.mdd.model.component.EnumType
import ee.mdd.model.component.Service

/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */

templates ('common') {

  //model
  templates ('modelApi',
  items: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {

    template('ifcEntity', appendName: true, body: '''<% if(c.item.base) { c.className = item.n.cap.base } else { c.className = item.cap } %><% c.serializable = true %>${macros.generate('ifcEntity', c)}''')
    template('ifcEntityExtends', appendName: true, body: '''<% if(c.item.base) { %>${macros.generate('ifcExtends', c)}<% } %>''')
  }

  templates ('modelApiBasicType',
  items: { c -> c.model.findAllRecursiveDown( { BasicType.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {

    template('ifcBasicType', appendName: true, body: '''<% if(c.item.base) { c.className = item.n.cap.base } else { c.className = item.cap } %> ${macros.generate('ifcBasicType', c)}''')
    template('ifcBasicTypeExtends', appendName: true, body: '''<% if(c.item.base) { %>${macros.generate('ifcExtends', c)}<% } %>''')
  }

  templates ('modelImplEntity',
  items: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'impl' ] ) } ) {

    template('implEntity', appendName: true, body: '''<% c.virtual = c.item.virtual; c.metas = item.metas; c.serializable = true; if(c.item.base) { c.className = item.n.cap.baseImpl } else { c.className = item.n.cap.impl } %>${macros.generate('implEntity', c)}''')
    template('implEntityExtends', appendName: true, body: '''<% if(c.item.base) { %><% c.serializable = true; c.className = item.n.cap.impl %>${macros.generate('implEntityExtends', c)}<% } %>''')
  }

  templates ('modelEjbEntity',
  items: { c -> c.model.findAllRecursiveDown( {Entity.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'ejb' ] ) } ) {

    template('ejbEntity', appendName: true, body: '''<% if(c.item.base) { c.className = item.n.cap.baseEntity } else { c.className = item.n.cap.entity } %>${macros.generate('ejbEntity', c)}''')
    template('ejbEntityExtends', appendName: true, body: '''<% if(c.item.base) { c.className = item.n.cap.entity %>${macros.generate('ejbEntityExtends', c)}<% } %>''')
  }

  templates ('modelEjbBasicType',
  items: { c -> c.model.findAllRecursiveDown( {BasicType.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'ejb' ] ) } ) {

    template('ejbBasicType', appendName: true, body: '''<% if(c.item.base) {  c.className = item.n.cap.baseEmbeddable } else { c.className = item.n.cap.embeddable } %>${macros.generate('ejbBasicType', c)}''')
    template('ejbBasicTypeExtends', appendName: true, body: '''<% if(c.item.base) { %><% c.className = item.n.cap.embeddable %> ${macros.generate('ejbBasicTypeExtends', c)} <% } %>''')
  }

  templates ('facadeEjbService',
  items: { c -> c.model.findAllRecursiveDown( {Service.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'facade' ] ) } ) {

    template('ejbService', appendName: true, body: '''<% c.className = c.item.n.cap.baseBean %>${macros.generate('ejbService', c)}''')
    template('ejbServiceExtends', appendName: true, body: '''<% if (c.item.base) { %> <% c.className = c.item.n.cap.bean %>${macros.generate('ejbServiceExtends', c)} <% } %>''')
  }

  templates ('implContainer',
  items: { c -> c.model.findAllRecursiveDown( {Container.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'impl' ] ) } ) {

    template('implContainer', appendName: true, body: '''<% c.className = c.item.n.cap.baseImpl %>${macros.generate('implContainer', c)}''')
    template('implContainerExtends', appendName: true, body: '''<% if (c.item.base) { %> c.className = c.item.n.cap.impl %>${macros.generate('implContainerExtends', c)}<% } %>''')
    template('implContainerDeltaExtends', appendName: true, body: '''<% c.className = item.n.cap.deltaImpl %> ${macros.generate('implContainerDeltaExtends', c)}''')
  }

  templates ('modelTest',
  items: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'impl', scope: 'test'] ) } ) {

    template('test', appendName: true, body: '''<% c.virtual = true; c.className = "${item.n.cap.test}Base"; c.itemInit = "new $item.n.cap.impl()" %>${macros.generate('test', c)}''')
    template('testExtends', appendName: true, body: '''<% c.className = item.n.cap.test %>${macros.generate('testExtends', c)}''')
  }

  templates ('enum',
  items: { c -> c.model.findAllRecursiveDown( { EnumType.isInstance(it) }) },
  context: { c -> def enumType = c.item; c.putAll( [ component: enumType.component, module: enumType.module, enumType: enumType ] ) } ) {

    template('enum', appendName: true, body: '''${macros.generate('enum', c)}''')
  }

  templates ('enumTest',
  items: { c -> c.model.findAllRecursiveDown( { EnumType.isInstance(it) }) },
  context: { c -> def enumType = c.item; c.putAll( [ component: enumType.component, module: enumType.module, enumType: enumType, scope: 'test' ] ) } ) {

    template('testEnum', appendName: true, body: '''<% c.className = "${item.n.cap.test}Base" %>${macros.generate('testEnum', c)}''')
    template('testEnumExtends', appendName: true, body: '''<% c.className = item.n.cap.test %>${macros.generate('testExtends', c)}''')
  }

  //logic
  templates ('service',
  items: { c -> c.model.findAllRecursiveDown( { Service.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {

    template('ifcService', appendName: true, body: '''<% c.className = item.n.cap.base %>${macros.generate('ifcService', c)}''')
    template('ifcServiceExtends', appendName: true, body: '''<% if (c.item.base) { %><% c.className = item.cap %> ${macros.generate('ifcServiceExtends', c)}<% } %>''')
  }

  templates ('controller',
  items: { c -> c.model.findAllRecursiveDown( { Controller.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {

    template('ifcController', appendName: true, body: '''<% c.className = "${item.cap}Base" %>${macros.generate('ifcController', c)}''')
    template('ifcControllerExtends', appendName: true, body: '''<% if (c.item.base) { %><% c.className = item.cap %> ${macros.generate('ifcControllerExtends', c)}<% } %>''')
  }


  templates ('container',
  items: { c -> c.model.findAllRecursiveDown( { Container.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'model'] ) } ) {

    template('ifcContainer', appendName: true, body: '''<% c.className = c.item.n.cap.base %> ${macros.generate('ifcContainer', c)}''')
    template('ifcContainerExtends', appendName: true, body: '''<% if (c.item.base) { %><% c.className = item.cap %> ${macros.generate('ifcContainerExtends', c)}<% } %>''')
    template('ifcContainerDelta', appendName: true, body: '''<% c.className = item.n.cap.deltaBase %> ${macros.generate('ifcContainerDelta', c)}''')
    template('ifcContainerDeltaExtends', appendName: true, body: '''<% c.className = item.n.cap.delta %> ${macros.generate('ifcContainerDeltaExtends', c)}''')
    template('containerRemoves', appendName: true, body: '''<% c.className = item.n.cap.removesBase %> ${macros.generate('containerRemoves', c)}''')
    template('containerRemovesExtends', appendName: true, body: '''<% c.className = item.n.cap.removes %> ${macros.generate('containerRemovesExtends', c)}''')
    template('implContainerDelta', appendName: true, body: '''<% c.className = item.n.cap.deltaImpl %> ${macros.generate('implContainerDelta', c)}''')
  }

  templates ('jmsToCdi',
  items: { c -> c.model.findAllRecursiveDown( { Channel.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'integ' ] ) } ) {

    template('jmsToCdi', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.jmsToCdi %> ${macros.generate('jmsToCdi', c)}<% } %>''')
    template('jmsToCdiMdb', appendName: true, body: '''<% def cachedContainers = module.containers.findAll { it.controller.cache }%><% if (cachedContainers || module.configs) { %><% c.className = c.item.n.cap.jmsToCdiMdb %> ${macros.generate('jmsToCdiMdb', c)}<% } %>''')
    template('notificationPlugin', appendName: true, body: '''<% def modules = []; modules.addAll(component.backends.findAll { m -> m.entities }) %><% if(modules) { %><% c.className = component.n.cap.notificationPlugin %> ${macros.generate('notificationPlugin', c)} <% } %> ''')
  }

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


  templates ('bridgeTests',
  items: { c -> c.model.findAllRecursiveDown( { Channel.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'integ' ] ) } ) {
    template('notificationPluginTest', appendName: true, body: '''<% def modules = []; modules.addAll(component.backends.findAll { m -> m.entities }) %><% if(modules) { %><% c.className = c.item.n.cap.notificationPluginTest %> ${macros.generate('notificationPluginTest', c)}<% } %>''')
    template('jmsToCdiTest' , appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.jmsToCdiTest %> ${macros.generate('jmsToCdiTest', c)} <% } %>''')
    template('cdiToJmsTest', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.cdiToJmsTest %> ${macros.generate('cdiToJmsTest', c)} <% } %>''')
    template('eventToCdiTest', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.eventToCdiTest %> ${macros.generate('eventToCdiTest', c)} <% } %>''')
  }

  //      templates ('constants',
  //      items: { c -> c.model.findAllRecursiveDown( { Component.isInstance(it) }) },
  //      context: { c -> c.putAll( [ component: c.item] ) } ) {
  //
  //        template('constants', appendName: true, body: '''<% c.className = c.item.n.cap.constantsBase%> ${macros.generate('constants', c)}''')
  //        template('constantsExtends', appendName: true, body: '''<% c.className = c.item.n.cap.constants%> ${macros.generate('constantsExtends', c)}''')
  //        template('Ml', appendName: true, body: '''<% c.className = c.item.n.cap.ml %> ${macros.generate('constantsMl', c)}''')
  //      }
}
