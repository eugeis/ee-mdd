import ee.mdd.model.component.BasicType
import ee.mdd.model.component.Channel
import ee.mdd.model.component.Component
import ee.mdd.model.component.Container
import ee.mdd.model.component.Controller
import ee.mdd.model.component.Entity
import ee.mdd.model.component.EnumType
import ee.mdd.model.component.Facade




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

    template('implEntity', appendName: true, body: '''<% c.metas = item.metas; c.serializable = true; if(c.item.base) { c.className = item.n.cap.baseImpl } else { c.className = item.n.cap.impl } %>${macros.generate('implEntity', c)}''')
    template('implEntityExtends', appendName: true, body: '''<% if(c.item.base) { %><% c.serializable = true; c.className = item.n.cap.impl %>${macros.generate('implEntityExtends', c)}<% } %>''')
  }

  templates ('enum',
  items: { c -> c.model.findAllRecursiveDown( { EnumType.isInstance(it) }) },
  context: { c -> def enumType = c.item; c.putAll( [ component: enumType.component, module: enumType.module, enumType: enumType ] ) } ) {

    template('enum', appendName: true, body: '''${macros.generate('enum', c)}''')
  }

  //logic
  templates ('service',
  items: { c -> c.model.findAllRecursiveDown( { Facade.isInstance(it) }) },
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
    template('implContainerDelta', appendName: true, body: '''<% c.className = item.n.cap.deltaBaseImpl %> ${macros.generate('implContainerDelta', c)}''')
  }

  templates ('implContainer',
  items: { c -> c.model.findAllRecursiveDown( {Container.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'impl' ] ) } ) {

    template('implContainer', appendName: true, body: '''<% c.className = c.item.n.cap.baseImpl %>${macros.generate('implContainer', c)}''')
    template('implContainerExtends', appendName: true, body: '''<% if (c.item.base) { %> c.className = c.item.n.cap.impl %>${macros.generate('implContainerExtends', c)}<% } %>''')
    template('implContainerDeltaExtends', appendName: true, body: '''<% c.className = item.n.cap.deltaImpl %> ${macros.generate('implContainerDeltaExtends', c)}''')
  }

  templates ('jmsToCdi',
  items: { c -> c.model.findAllRecursiveDown( { Channel.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'integ' ] ) } ) {

    template('jmsToCdi', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.jmsToCdi %> ${macros.generate('jmsToCdi', c)}<% } %>''')
    template('jmsToCdiMdb', appendName: true, body: '''<% def cachedContainers = module.containers.findAll { it.controller.cache }%><% if (cachedContainers || module.configs) { %><% c.className = c.item.n.cap.jmsToCdiMdb %> ${macros.generate('jmsToCdiMdb', c)}<% } %>''')
    template('notificationPlugin', appendName: true, body: '''<% def modules = []; modules.addAll(component.backends.findAll { m -> m.entities }) %><% if(modules) { %><% c.className = component.n.cap.notificationPlugin %> ${macros.generate('notificationPlugin', c)} <% } %> ''')
  }

  templates ('commandsFinders',
  items: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {

    template('ifcFinders', appendName: true, body: '''<% if (item.finders && !item.virtual) { %><% c.className = item.n.cap.finders %>${macros.generate('ifcFinders', c)}<% } %>''')
    template('ifcCommands', appendName: true, body: '''<% if (item.commands && !item.virtual) { %><% c.className = item.n.cap.commands %>${macros.generate('ifcCommands', c)}<% } %>''')

  }

  templates ('constants',
  items: { c -> c.model.findAllRecursiveDown( { Component.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item, module: c.item.module, subPkg: 'integ'] ) } ) {
    // Custom paths for Component templates
    template('constants', appendName: true, body: '''<% c.className = c.item.n.cap.constantsBase%><% c.path = "ee-mdd_example-shared/src-gen/main/java/${c.item.ns.path}/integ/${c.className}.java" %>${macros.generate('constants', c)}''')
    //    template('constantsExtends', appendName: true, body: '''<% c.className = c.item.n.cap.constants%> ${macros.generate('constantsExtends', c)}''')
    template('Ml', appendName: true, body: '''<% c.className = "${item.name}MlBase" %><% c.path = "ee-mdd_example-shared/src-gen/main/java/${c.item.ns.path}/integ/${c.className}.java" %> ${macros.generate('constantsMl', c)}''')
    template('MlExtends', appendName: true, body: '''<% c.className = "${item.name}Ml" %><% c.path = "ee-mdd_example-shared/src/main/java/${c.item.ns.path}/integ/${c.className}.java" %> ${macros.generate('constantsMlExtends', c)}''')
  }
}
