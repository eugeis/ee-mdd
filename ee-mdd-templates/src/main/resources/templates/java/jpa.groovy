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
import ee.mdd.model.component.Component
import ee.mdd.model.component.Entity
import ee.mdd.model.component.Module

import static ee.mdd.generator.OutputType.SHARED

/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */

templates('jpa') {

  useMacros('commonMacros', '/common/macros')
  useMacros('macros')

  templates ('basicType', type: SHARED,
  items: { c -> c.model.findAllDown(BasicType) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'ejb' ] ) } ) {

    template('basicTypeBase', appendName: true, body: '''<% if(c.item.base) {  c.className = item.n.cap.baseEmbeddable } else { c.className = item.n.cap.embeddable } %>${macros.generate('basicTypeBaseBean', c)}''')
    template('basicTypeBean', appendName: true, body: '''<% if(c.item.base) { %><% c.className = item.n.cap.embeddable %> ${macros.generate('basicTypeBean', c)} <% } %>''')
    template('basicTypeFactoryBean', appendName: true, body: '''<% if(!item.virtual) { %><% c.className = item.n.cap.embeddableFactory; c.bean = 'Embeddable' %> ${macros.generate('factoryBean', c)} <% } %>''')
  }

  templates ('entity', type: SHARED,
  init: { c -> c.model.findAllDown({ Entity.isInstance(it) }).each { it.n.cap.addAll(['baseEntity', 'entity',  'entityFactory', 'entityBuilderBase', 'entityBuilder'], 'ejb') } },
  items: { c -> c.model.findAllDown(Entity) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {

    template('entityBaseBean', appendName: true, body: '''<% if(c.item.base) { c.className = item.n.cap.baseEntity } else { c.className = item.n.cap.entity } %>${macros.generate('entityBaseBean', c)}''')
    template('entityBean', appendName: true, body: '''<% if(c.item.base) { c.className = item.n.cap.entity %>${macros.generate('entityBean', c)}<% } %>''')
    template('entityFactoryBean', appendName: true, body: '''<% if(!item.virtual) { %><% c.className = item.n.cap.entityFactory; c.bean = 'Entity' %> ${macros.generate('factoryBean', c)}<% } %>''')
    template('entityBeanBuilder', appendName: true, body: '''<% if(!item.virtual) { %><% c.className = item.n.cap.entityBuilderBase %> ${macros.generate('entityBeanBuilder', c)}<% } %>''')
    template('entityBeanBuilderExtends', appendName: true, body: '''<% if(!item.virtual) { %><% c.className = item.n.cap.entityBuilder %> ${macros.generate('entityBeanBuilderExtends', c)}<% } %>''')
  }

  templates('implCommandsFinders', type: SHARED,
  items: { c -> c.model.findAllDown(Entity) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'ejb' ] ) } ) {
    template('implCommands', appendName: true, body: '''<% if(item.commands && !item.virtual) { %><% if(item.commands.base) { %><% c.className = item.commands.n.cap.baseImpl %><% } else { %><% c.className = item.commands.n.cap.impl %><% } %> ${macros.generate('implCommands', c)}<% } %>''')
    template('implCommandsExtends', appendName: true, body: '''<% if(item.commands && !item.virtual && item.commands.base) { %><% c.className = item.commands.n.cap.impl %> ${macros.generate('implCommandsExtends', c)} <% } %>''')
    template('implFinders', appendName: true, body: '''<% if(item.finders && !item.virtual) { %><% if(item.finders.base) { %><% c.className = item.finders.n.cap.baseImpl %><% } else { %><% c.className = item.finders.n.cap.impl %><% } %> ${macros.generate('implFinders', c)}<% } %>''')
    template('implFindersExtends', appendName: true, body: '''<% if(item.finders && !item.virtual && item.finders.base) { %><% c.className = item.finders.n.cap.impl %> ${macros.generate('implFindersExtends', c)} <% } %>''')
  }

  templates('factoryCommandsFinders', type: SHARED,
  items: { c -> c.model.findAllDown(Module) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('commandsFactory', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}CommandsFactoryBase" %> ${macros.generate('commandsFactory', c)} <% } %>''')
    template('findersFactory', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}FindersFactoryBase" %> ${macros.generate('findersFactory', c)} <% } %>''')
    template('commandsFactoryExtends', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}CommandsFactory" %> ${macros.generate('commandsFactoryExtends', c)} <% } %>''')
    template('findersFactoryExtends', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}FindersFactory" %> ${macros.generate('findersFactoryExtends', c)} <% } %>''')
    template('commandsFactoryLocal', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}CommandsFactoryLocalBase" %> ${macros.generate('commandsFactoryLocal', c)} <% } %>''')
    template('findersFactoryLocal', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}FindersFactoryLocalBase" %> ${macros.generate('findersFactoryLocal', c)} <% } %>''')
    template('commandsFactoryLocalExtends', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}CommandsFactoryLocal" %> ${macros.generate('commandsFactoryLocalExtends', c)} <% } %>''')
    template('findersFactoryLocalExtends', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}FindersFactoryLocal" %> ${macros.generate('findersFactoryLocalExtends', c)} <% } %>''')
    template('implCommandsFactory', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}CommandsFactoryBaseImpl" %> ${macros.generate('implCommandsFactory', c)} <% } %>''')
    template('implFindersFactory', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}FindersFactoryBaseImpl" %> ${macros.generate('implFindersFactory', c)} <% } %>''')
    template('implCommandsFactoryExtends', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}CommandsFactoryImpl" %> ${macros.generate('implCommandsFactoryExtends', c)} <% } %>''')
    template('implFindersFactoryExtends', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}FindersFactoryImpl" %> ${macros.generate('implFindersFactoryExtends', c)} <% } %>''')
  }


  templates('jpaProducer', type: SHARED,
  items: { c -> c.model.findAllDown(Component) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('producerLocal', appendName: true, body: '''<% c.className = "${component.capShortName}ProducerLocal" %>${macros.generate('producerLocal', c)}''')
    template('producerServer', appendName: true, body: '''<% c.className = "${component.capShortName}ProducerServer" %><% c.path = "ee-mdd_example-backend/src-gen/main/java/${c.item.ns.path}/integ/ejb/${c.className}.java" %> ${macros.generate('producerServer', c)}''')
    template('producer', appendName: true, body: '''<% c.className = "${component.capShortName}Producer" %><% c.path = "ee-mdd_example-backend/src-gen/main/java/${c.item.ns.path}/integ/ejb/${c.className}.java" %> ${macros.generate('producer', c)}''')
  }

  templates('jpaSchemaGenerator', type: SHARED,
  items: { c -> c.model.findAllDown(Module) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('jpaSchemaGenerator', appendName: true, body: '''<% if (module.entities) { %><% c.className = "${module.capShortName}SchemaGenerator" %> ${macros.generate('jpaSchemaGenerator', c)}<% } %>''')
  }
}
