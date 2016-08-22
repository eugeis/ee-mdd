import ee.mdd.model.component.BasicType
import ee.mdd.model.component.Channel
import ee.mdd.model.component.Commands
import ee.mdd.model.component.Component
import ee.mdd.model.component.Config
import ee.mdd.model.component.Container
import ee.mdd.model.component.Controller
import ee.mdd.model.component.Entity
import ee.mdd.model.component.EnumType
import ee.mdd.model.component.Facade
import ee.mdd.model.component.Finders
import ee.mdd.model.component.InterfType
import ee.mdd.model.component.Module
import ee.mdd.model.component.Pojo


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

  useMacros('commonMacros', '/common/macros')
  useMacros('macros')

  templates('build',
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Module) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {
    template('build', appendName: true, body: '''<% if (component) { %><% c.path = "${component.artifact}-${module.key}/build.gradle" %>${macros.generate('build', c)}<% } %>''')
  }

  templates ('modelApi',
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Entity) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {
    template('ifcEntity', appendName: true, body: '''<% if(c.item.base) { c.className = item.n.cap.base } else { c.className = item.cap } %><% c.serializable = true %>${macros.generate('ifcEntity', c)}''')
    template('ifcEntityExtends', appendName: true, body: '''<% if(c.item.base) { %>${macros.generate('ifcExtends', c)}<% } %>''')
  }

  templates('ifcFactory',
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Module) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('ifcModelFactory', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}ModelFactory" %> ${macros.generate('ifcModelFactory', c)} <% } %>''')
  }

  templates ('modelApiBasicType',
  items: { c -> c.model.findAllDown(ee.mdd.model.component.BasicType) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {
    template('ifcBasicType', appendName: true, body: '''<% if(c.item.base) { c.className = item.n.cap.base } else { c.className = item.cap } %> ${macros.generate('ifcBasicType', c)}''')
    template('ifcBasicTypeExtends', appendName: true, body: '''<% if(c.item.base) { %>${macros.generate('ifcExtends', c)}<% } %>''')
  }

  templates('basicTypeFactory',
  init: { c -> c.model.findAllDown({ BasicType.isInstance(it) }).each { it.n.cap.addAll(['factoryBase', 'factory'], 'builder') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.BasicType) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('basicTypeFactory', appendName: true, body: '''<% if (!item.virtual) { %><% c.className = item.n.cap.factoryBase %><% c.baseClass = 'AbstractFactory' %> ${macros.generate('factory', c)}<% } %>''' )
    template('basicTypeFactoryExtends', appendName: true, body: '''<% if (!item.virtual) { %><% c.className = item.n.cap.factory %> ${macros.generate('factoryExtends', c)}<% } %>''')
  }

  templates('implBasicTypeFactory',
  init: { c -> c.model.findAllDown({ BasicType.isInstance(it) }).each { it.n.cap.addAll(['embeddableFactory'], 'impl') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.BasicType) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('implBasicTypeFactory', appendName: true, body: '''<% if (!item.virtual) { %><% c.className = item.n.cap.embeddableFactory %> ${macros.generate('implFactory', c)}<% } %>''')
  }

  templates ('modelImplEntity',
  init: { c -> c.model.findAllDown({ Entity.isInstance(it) }).each { it.n.cap.addAll(['baseImpl', 'impl'], 'impl') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Entity) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('implEntity', appendName: true, body: '''<% c.metas = item.metas; c.serializable = true; if(c.item.base) { c.className = item.n.cap.baseImpl } else { c.className = item.n.cap.impl } %>${macros.generate('implEntity', c)}''')
    template('implEntityExtends', appendName: true, body: '''<% if(c.item.base) { %><% c.serializable = true; c.className = item.n.cap.impl %>${macros.generate('implEntityExtends', c)}<% } %>''')
  }

  templates('interfsBase',
  items: { c -> c.model.findAllDown(ee.mdd.model.component.InterfType) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {
    template('interfs', appendName: true, body: '''<% if(item.base) { %><% c.className = item.n.cap.base %><% } else { %><% c.className = item.cap %><% } %> ${macros.generate('interfs', c)} ''')
  }


  templates('cache',
  init: { c -> c.model.findAllDown({ Entity.isInstance(it) }).each { it.n.cap.addAll(['cache', 'cacheBase', 'deltaCache', 'deltaCacheBase'], 'cache') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Entity) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module]) } ) {
    template('ifcCache', appendName: true, body: '''<% c.className = item.n.cap.cacheBase %> ${macros.generate('ifcCache', c)}''')
    template('ifcCacheExtends', appendName: true, body: '''<% c.className = item.n.cap.cache %> ${macros.generate('ifcCacheExtends', c)}''')
    template('ifcDeltaCache', appendName: true, body: '''<% c.className = item.n.cap.deltaCacheBase %> ${macros.generate('ifcDeltaCache', c)}''')
    template('ifcDeltaCacheExtends', appendName: true, body: '''<% c.className = item.n.cap.deltaCache %> ${macros.generate('ifcDeltaCacheExtends', c)}''')
  }

  templates('implCache',
  init: { c -> c.model.findAllDown({ Entity.isInstance(it) }).each { it.n.cap.addAll(['cacheBaseImpl', 'cacheImpl', 'deltaCacheBaseImpl', 'deltaCacheImpl', 'cacheOverrideBase', 'cacheOverride'], 'cache') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Entity) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module]) } ) {
    template('implCache', appendName: true, body: '''<% c.className = item.n.cap.cacheBaseImpl %> ${macros.generate('implCache', c)}''')
    template('implCacheExtends', appendName: true, body: '''<% c.className = item.n.cap.cacheImpl %> ${macros.generate('implCacheExtends', c)}''')
    template('implDeltaCache', appendName: true, body: '''<% c.className = item.n.cap.deltaCacheBaseImpl %> ${macros.generate('implDeltaCache', c)}''')
    template('implDeltaCacheExtends', appendName: true, body: '''<% c.className = item.n.cap.deltaCacheImpl %> ${macros.generate('implDeltaCacheExtends', c)}''')
    template('cacheOverride', appendName: true, body: '''<% c.className = item.n.cap.cacheOverrideBase %><% c.override = true %> ${macros.generate('implCache', c)}''')
    template('cacheOverrideExtends', appendName: true, body: '''<% c.className = item.n.cap.cacheOverride %><% c.override = true %> ${macros.generate('implCacheExtends', c)}''')
  }

  templates('entityEvent',
  init: { c -> c.model.findAllDown({ Entity.isInstance(it) }).each { it.n.cap.addAll(['event'], 'event') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Entity) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('entityEvent', appendName: true, body: '''<% if(!item.virtual) { %><% c.className = item.n.cap.event %> ${macros.generate('event', c)}<% } %>''')
  }

  templates('entityBuilder',
  init: { c -> c.model.findAllDown({ Entity.isInstance(it) }).each { it.n.cap.addAll(['builderBase', 'builder', 'factoryBase', 'factory'], 'builder') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Entity) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('entityBuilder', appendName: true, body: '''<% if(!item.virtual) { %><% c.className = item.n.cap.builderBase %> ${macros.generate('entityBuilder', c)}<% } %>''')
    template('entityBuilderExtends', appendName: true, body: '''<% if(!item.virtual) { %><% c.className = item.n.cap.builder %> ${macros.generate('entityBuilderExtends', c)}<% } %>''')
    template('entityFactory', appendName: true, body: '''<% if (!item.virtual) { %><% c.className = item.n.cap.factoryBase %><% c.baseClass = 'AbstractEntityFactory' %> ${macros.generate('factory', c)}<% } %>''' )
    template('entityFactoryExtends', appendName: true, body: '''<% if (!item.virtual) { %><% c.className = item.n.cap.factory %> ${macros.generate('factoryExtends', c)}<% } %>''')
  }

  templates('implEntityBuilder',
  init: { c -> c.model.findAllDown({ Entity.isInstance(it) }).each { it.n.cap.addAll(['implBuilderBase', 'implBuilder', 'implFactory'], 'impl') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Entity) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('implEntityBuilder', appendName: true, body: '''<% if(!item.virtual) { %><% c.className = item.n.cap.implBuilderBase %>${macros.generate('implEntityBuilder', c)}<% } %>''')
    template('implEntityBuilderExtends', appendName: true, body: '''<% if(!item.virtual) { %><% c.className = item.n.cap.implBuilder %>${macros.generate('implEntityBuilderExtends', c)}<% } %>''')
    template('implEntityFactory', appendName: true, body: '''<% if(!item.virtual) { %><% c.className = item.n.cap.implFactory %>${macros.generate('implFactory', c)}<% } %>''')
  }

  templates('entityEventReceiver',
  init: { c -> c.model.findAllDown({ Entity.isInstance(it) }).each { it.n.cap.addAll(['eventReceiver'], 'receiver') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Entity) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('entityEventReceiver', appendName: true, body: '''<% if(!item.virtual) { %><% c.className = item.n.cap.eventReceiver %> ${macros.generate('eventReceiver', c)}<% } %>''')
  }

  templates ('enum',
  init: { c -> c.model.findAllDown({ EnumType.isInstance(it) }).each { it.n.cap.addAll([''], 'model') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.EnumType) },
  context: { c -> def enumType = c.item; c.putAll( [ component: enumType.component, module: enumType.module, enumType: enumType ] ) } ) {
    template('enum', appendName: true, body: '''<% c.className = item.n.cap[''] %>${macros.generate('enum', c)}''')
  }

  templates('pojo',
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Pojo) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('pojo', appendName: true, body: '''<% if(item.base) { %><% c.className = item.n.cap.base %><% } else { %><% c.className = item.cap %><% } %> ${macros.generate('pojo', c)}''')
    template('pojoExtends', appendName: true, body: '''<% if(item.base) { %><% c.className = item.cap %>${macros.generate('pojoExtends', c)}<% } %>''')
  }

  templates('pojoEvent',
  init: { c -> c.model.findAllDown({ Pojo.isInstance(it) }).each { it.n.cap.addAll(['event'], 'event') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Pojo) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('pojoEvent', appendName: true, body: '''<% if(!item.virtual) { %><% c.className = item.n.cap.event %> ${macros.generate('event', c)}<% } %>''')
  }

  templates ('service',
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Facade) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {
    template('ifcService', appendName: true, body: '''<%  if(c.item.base) { c.className = item.n.cap.base } else { c.className = item.cap }%>${macros.generate('ifcService', c)}''')
    template('ifcServiceExtends', appendName: true, body: '''<% if (c.item.base) { %><% c.src = true %><% c.className = item.cap %> ${macros.generate('ifcServiceExtends', c)}<% } %>''')
    template('serviceEmpty', appendName: true, body: '''<% c.className = item.n.cap.empty %> ${macros.generate('serviceEmpty', c)}''')
  }

  templates('serviceProvider',
  init: { c -> c.model.findAllDown({ Facade.isInstance(it) }).each { it.n.cap.addAll(['provider'], 'provider') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Facade) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('serviceProvider', appendName: true, body: '''<% c.className = item.n.cap.provider %> ${macros.generate('serviceProvider', c)}''')
  }

  templates ('container',
  init: { c -> c.model.findAllDown({ Container.isInstance(it) }).each { it.n.cap.addAll(['base', '', 'deltaBase', 'delta', 'idsBase', 'ids', 'versionsBase', 'versions', 'diffBase', 'diff'], 'model') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Container) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('ifcContainer', appendName: true, body: '''<% if(c.item.base) { %><% c.className = item.n.cap.base %><% } else { %><% c.className = item.cap %><% } %> ${macros.generate('ifcContainer', c)}''')
    template('ifcContainerExtends', appendName: true, body: '''<% if (c.item.base) { %><% c.className = item.n.cap[''] %> ${macros.generate('ifcContainerExtends', c)}<% } %>''')
    template('ifcContainerDelta', appendName: true, body: '''<% c.className = item.n.cap.deltaBase %> ${macros.generate('ifcContainerDelta', c)}''')
    template('ifcContainerDeltaExtends', appendName: true, body: '''<% c.className = item.n.cap.delta %> ${macros.generate('ifcContainerDeltaExtends', c)}''')
    template('containerIds', appendName: true, body: '''<% c.className = item.n.cap.idsBase %> ${macros.generate('containerIds', c)}''')
    template('containerIdsExtends', appendName: true, body: '''<% c.className = item.n.cap.ids %> ${macros.generate('containerIdsExtends', c)}''')
    template('containerVersions', appendName: true, body: '''<% if(c.item.base) { c.className = item.n.cap.versionsBase } else { c.className = item.n.cap.versions } %> ${macros.generate('containerVersions', c)}''' )
    template('containerVersionsExtends', appendName: true, body: '''<% if(c.item.base) { %><% c.className = item.n.cap.versions %> ${macros.generate('containerVersionsExtends', c)} <% } %>''' )
    template('containerDiff', appendName: true, body: '''<% c.className = item.n.cap.diffBase %> ${macros.generate('containerDiff', c)}''')
    template('containerDiffExtends', appendName: true, body: '''<% c.className = item.n.cap.diff %> ${macros.generate('containerDiffExtends', c)}''')
  }

  templates ('implContainer',
  init: { c -> c.model.findAllDown({ Container.isInstance(it) }).each { it.n.cap.addAll(['baseImpl', 'containerImpl', 'impl', 'deltaBaseImpl', 'versionsBaseImpl', 'versionsImpl'], 'impl') } },
  items: { c -> c.model.findAllDown( {Container.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('implContainer', appendName: true, body: '''<% if(c.item.base) { %><% c.className = c.item.n.cap.baseImpl %><% } else { %><% c.className = item.n.cap.impl %><% } %>${macros.generate('implContainer', c)}''')
    template('implContainerExtends', appendName: true, body: '''<% if (c.item.base) { %><% if(!c.item.name.endsWith("Container")) { c.className = c.item.n.cap.containerImpl } else { c.className = c.item.n.cap.impl } %>${macros.generate('implContainerExtends', c)}<% } %>''')
    template('implContainerDelta', appendName: true, body: '''<% c.className = item.n.cap.deltaBaseImpl %> ${macros.generate('implContainerDelta', c)}''')
    template('implContainerDeltaExtends', appendName: true, body: '''<% c.className = item.n.cap.deltaImpl %> ${macros.generate('implContainerDeltaExtends', c)}''')
    template('implContainerVersions', appendName: true, body: '''<% if(c.item.base) { %><% c.className = item.n.cap.versionsBaseImpl %><% } else { %><% c.className = item.n.cap.versionsImpl %><% } %>${macros.generate('implContainerVersions', c)}''')
    template('implContainerVersionsExtends', appendName: true, body: '''<% if(c.item.base) { %><% c.className = item.n.cap.versionsImpl %> ${macros.generate('implContainerVersionsExtends', c)}<% } %>''')
  }

  templates('containerFactory',
  init: { c -> c.model.findAllDown({ Container.isInstance(it) }).each { it.n.cap.addAll(['baseImpl', 'containerImpl', 'impl', 'deltaBaseImpl', 'versionsBaseImpl', 'versionsImpl'], 'builder') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Container) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('containerFactory', appendName: true, body: '''<% if (!item.virtual) { %><% c.className = item.n.cap.factoryBase %> ${macros.generate('containerFactory', c)}<% } %>''' )
    template('containerFactoryExtends', appendName: true, body: '''<% if (!item.virtual) { %><% c.className = item.n.cap.factory %> ${macros.generate('factoryExtends', c)}<% } %>''')
  }

  templates('implContainerFactory',
  init: { c -> c.model.findAllDown({ Container.isInstance(it) }).each { it.n.cap.addAll(['baseImpl'], 'implFactory') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Container) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('implContainerFactory', appendName: true, body: '''<% if (!item.virtual) { %><% c.className = item.n.cap.implFactory %> ${macros.generate('implContainerFactory', c)}<% } %>''' )
  }

  templates('containerFactoryBean',
  init: { c -> c.model.findAllDown({ Container.isInstance(it) }).each { it.n.cap.addAll(['beanFactory'], 'ejb') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Container) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('containerFactoryBean', appendName: true, body: '''<% if (!item.virtual) { %><% c.className = item.n.cap.beanFactory %> ${macros.generate('containerFactoryBean', c)}<% } %>''' )
  }


  templates('containerEvent',
  init: { c -> c.model.findAllDown({ Container.isInstance(it) }).each { it.n.cap.addAll(['event'], 'event') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Container) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('containerEvent', appendName: true, body: '''<% if(!item.virtual) { %><% c.className = item.n.cap.event %> ${macros.generate('containerEvent', c)}<% } %>''')
  }

  templates('containerEventReceiver',
  init: { c -> c.model.findAllDown({ Container.isInstance(it) }).each { it.n.cap.addAll(['eventReceiver'], 'receiver') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Container) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('containerEventReceiver', appendName: true, body: '''<% c.className = item.n.cap.eventReceiver %> ${macros.generate('eventReceiver', c)}''')
  }

  templates ('controller',
  items: { c -> c.model.findAllDown( { Controller.isInstance(it) && !Container.isInstance(it.parent) && !Config.isInstance(it.parent) && !Finders.isInstance(it) && !Commands.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {
    template('ifcController', appendName: true, body: '''<% if(c.item.base) { c.className = item.n.cap.base } else { c.className = item.cap } %>${macros.generate('ifcController', c)}''')
    template('ifcControllerExtends', appendName: true, body: '''<% if (c.item.base) { %><% c.className = item.cap %> ${macros.generate('ifcControllerExtends', c)}<% } %>''')
  }

  templates('implController',
  init: { c -> c.model.findAllDown({ Container.isInstance(it) }).each { it.n.cap.addAll(['eventReceiver'], 'impl') } },
  items: { c -> c.model.findAllDown( {Controller.isInstance(it) && !Finders.isInstance(it) && !Commands.isInstance(it) && !Config.isInstance(it.parent) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('implController', appendName: true, body: '''<% if(item.base) { %><% c.className = item.n.cap.baseImpl %><% } else { %><% c.className = item.n.cap.impl %><% } %> ${macros.generate('implController', c)}''')
    template('implControllerExtends', appendName: true, body: '''<% if(item.base) { %><% c.className = item.n.cap.impl %>${macros.generate('implControllerExtends', c)}<% } %>''')
  }

  templates('implContainerController',
  init: { c -> c.model.findAllDown({ Container.isInstance(it) }).each { it.n.cap.addAll(['baseImpl', 'impl'], 'impl') } },
  items: { c -> c.model.findAllDown( {Container.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('implContainerController', appendName: true, body: '''<% def controller = item.controller %><% if(controller) { %><% if(controller.base) { %><% c.className = controller.n.cap.baseImpl %><% } else { %><% c.className = item.controller.n.cap.impl %><% } %> ${macros.generate('implContainerController', c)}<% } %>''')
    template('implContainerControllerExtends', appendName: true, body: '''<% def controller = item.controller %><% if (controller && controller.base) { %><% c.className = controller.n.cap.impl %>${macros.generate('implContainerControllerExtends', c)}<% } %>''')
  }

  templates('containerController',
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Container) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {
    template('ifcContainerController', appendName: true, body: '''<% def controller = item.controller %><% if(controller && controller.base) { %><% c.className = controller.n.cap.base %> ${macros.generate('ifcContainerController', c)}<% } %>''')
    template('ifcContainerControllerExtends', appendName: true, body: '''<% def controller = item.controller %><% if (controller && controller.base) { %><% c.className = controller.cap %> ${macros.generate('ifcContainerControllerExtends', c)}<% } %>''')
  }

  templates('config',
  init: { c -> c.model.findAllDown({ Config.isInstance(it) }).each { it.n.cap.addAll(['', 'base'], 'integ') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Config) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {
    template('config', appendName: true, body: '''<% if(c.item.base) { c.className = item.n.cap.base } else { c.className = item.n.cap[''] } %>${macros.generate('config', c)}''')
    template('configExtends', appendName: true, body: '''<% if (c.item.base) { %><% c.className = item.n.cap[''] %> ${macros.generate('configExtends', c)}<% } %>''')
  }

  templates('configEvent',
  init: { c -> c.model.findAllDown({ Config.isInstance(it) }).each { it.n.cap.addAll(['event'], 'integ.event') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Config) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {
    template('configEvent', appendName: true, body: '''<% if(!item.virtual) { %><% c.className = item.n.cap.event %> ${macros.generate('event', c)}<% } %>''')
  }
  
  templates('configController',
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Config) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {
    template('ifcConfigController', appendName: true, body: '''<% def controller = item.controller %><% if(controller && controller.base) { %><% c.className = controller.n.cap.base %> ${macros.generate('ifcConfigController', c)}<% } %>''')
    template('ifcConfigControllerExtends', appendName: true, body: '''<% def controller = item.controller %><% if (controller && controller.base) { %><% c.className = controller.cap %> ${macros.generate('ifcConfigControllerExtends', c)}<% } %>''')
  }

  templates('implConfigController',
  init: { c -> c.model.findAllDown({ Config.isInstance(it) }).each { it.n.cap.addAll(['impl', 'baseImpl'], 'impl') } },
  items: { c -> c.model.findAllDown( {Config.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('implConfigControllerExtends', appendName: true, body: '''<% def controller = item.controller %><% if (controller && controller.base) { %><% c.className = controller.n.cap.impl %> ${macros.generate('implConfigControllerExtends', c)}<% } %>''')
    template('implConfigController', appendName: true, body: '''<% def controller = item.controller %><% if(controller) { %><% c.className = controller.n.cap.baseImpl %> ${macros.generate('implConfigController', c)}<% } %>''')
  }

  templates('configEventReceiver',
  init: { c -> c.model.findAllDown({ Config.isInstance(it) }).each { it.n.cap.addAll(['eventReceiver'], 'receiver') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Config) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('configEventReceiver', appendName: true, body: '''<% c.className = item.n.cap.eventReceiver %> ${macros.generate('eventReceiver', c)}''')
  }

  templates ('jmsToCdi',
  init: { c -> c.model.findAllDown({ Channel.isInstance(it) }).each { it.n.cap.addAll(['jmsToCdi', 'jmsToCdiMdb', 'notificationPlugin'], 'integ') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Channel) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('jmsToCdi', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.jmsToCdi %> ${macros.generate('jmsToCdi', c)}<% } %>''')
    template('jmsToCdiMdb', appendName: true, body: '''<% def cachedContainers = module.containers.findAll { it.controller.cache }%><% if (cachedContainers || module.configs) { %><% c.className = c.item.n.cap.jmsToCdiMdb %> ${macros.generate('jmsToCdiMdb', c)}<% } %>''')
    template('notificationPlugin', appendName: true, body: '''<% def modules = []; modules.addAll(component.backends.findAll { m -> m.entities }) %><% if(modules) { %><% c.className = component.n.cap.notificationPlugin %> ${macros.generate('notificationPlugin', c)} <% } %> ''')
  }

  templates ('commandsFinders',
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Entity) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {
    template('ifcFinders', appendName: true, body: '''<% if (item.finders && !item.virtual) { %><% c.className = item.n.cap.findersBase %>${macros.generate('ifcFinders', c)}<% } %>''')
    template('ifcCommands', appendName: true, body: '''<% if (item.commands && !item.virtual) { %><% c.className = item.n.cap.commandsBase %>${macros.generate('ifcCommands', c)}<% } %>''')
    template('ifcFindersExtends', appendName: true, body: '''<% if (item.finders && !item.virtual && item.finders.base) { %><% c.className = item.n.cap.finders %>${macros.generate('ifcFindersExtends', c)}<% } %>''' )
    template('ifcCommandsExtends', appendName: true, body: '''<% if (item.commands && !item.virtual && item.commands.base) { %><% c.className = item.n.cap.commands %>${macros.generate('ifcCommandsExtends', c)}<% } %>''' )
  }

  templates ('constants',
  init: { c -> c.model.findAllDown({ Component.isInstance(it) }).each { it.n.cap.addAll(['constantsBase', 'constants',  '', 'MlBase', 'Ml'], 'integ') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Component) },
  context: { c -> c.putAll( [ component: c.item, module: c.item.module] ) } ) {
    template('constants', appendName: true, body: '''<% c.className = c.item.n.cap.constantsBase%>${macros.generate('constants', c)}''')
    template('qualifier', appendName: true, body: '''<% c.className = c.item.n.cap[''] %>${macros.generate('qualifier', c)}''')
    template('constantsExtends', appendName: true, body: '''<% c.className = c.item.n.cap.constants%> ${macros.generate('constantsExtends', c)}''')
    template('Ml', appendName: true, body: '''<% c.className = c.item.n.cap.mlBase %>${macros.generate('constantsMl', c)}''')
    template('MlExtends', appendName: true, body: '''<% c.className = item.n.cap.ml %>${macros.generate('constantsMlExtends', c)}''')
    template('constantsRealm', appendName: true, body: '''<% c.className = "${item.capShortName}RealmConstants" %> ${macros.generate('constantsRealm', c)}''')
  }

  templates('xml',
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Module) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item] ) } ) {
    template('xmlConverter', appendName: true, body: '''<% if(item.entities) { %><% c.className = "${item.capShortName}XmlConverterBase" %> ${macros.generate('xmlConverter', c)} <% } %>''')
    template('xmlConverterExtends', appendName: true, body: '''<% if(item.entities) { %><% c.className = "${item.capShortName}XmlConverter" %> ${macros.generate('xmlConverterExtends', c)} <% } %>''')
  }

  templates('xmlContainerImport',
  init: { c -> c.model.findAllDown({ Container.isInstance(it) }).each { it.n.cap.addAll(['importDataMdb'], 'ejb') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Container) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('containerImportDataMdb', appendName: true, body: '''<% if(item.controller.asyncImport) { %><% c.className = item.n.cap.importDataMdb %> ${macros.generate('containerImportDataMdb', c)} <% } %>''')
  }

  templates('xmlContainer',
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Container) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('containerXmlConverter', appendName: true, body: '''<% c.className = c.item.n.cap.xmlConverterBase %> ${macros.generate('containerXmlConverter', c)}''')
    template('containerXmlConverterExtends', appendName: true, body: '''<% c.className = c.item.n.cap.xmlConverter %> ${macros.generate('containerXmlConverterExtends', c)}''')
    template('implContainerXmlConverter', appendName: true, body: '''<% c.className = c.item.n.cap.xmlConverterBaseImpl %> ${macros.generate('implContainerXmlConverter', c)}''')
    template('implContainerXmlConverterExtends', appendName: true, body: '''<% c.className = c.item.n.cap.xmlConverterImpl %> ${macros.generate('implContainerXmlConverterExtends', c)}''')
    template('xmlController', appendName: true, body: '''<% if(item.xmlController) { %><% c.className = c.item.xmlController.n.cap.base %> ${macros.generate('xmlController', c)}<% } %>''')
    template('xmlControllerExtends', appendName: true, body: '''<% if(item.xmlController) { %><% c.className = c.item.xmlController.cap %> ${macros.generate('xmlControllerExtends', c)}<% } %>''')
    template('implXmlController', appendName: true, body: '''<% if(item.xmlController) { %><% c.className = c.item.xmlController.n.cap.baseImpl %> ${macros.generate('implXmlController', c)}<% } %>''')
    template('implXmlControllerExtends', appendName: true, body: '''<% if(item.xmlController) { %><% c.className = c.item.xmlController.n.cap.Impl %> ${macros.generate('implXmlControllerExtends', c)}<% } %>''')
  }

  templates('converter',
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Module) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('converter', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}ConverterBase" %> ${macros.generate('converter', c)}<% } %>''')
    template('converterExends', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}Converter" %> ${macros.generate('converterExtends', c)} <% } %>''')
  }


  templates('initializer',
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Module) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('ifcInitializer', appendName: true, body: '''<% if(module.startupInitializer) { %><% c.className = "${module.initializerName}" %> ${macros.generate('ifcInitializer', c)} <% } %>''')
    template('initializer', appendName: true, body: '''<% if(module.startupInitializer) { %><% c.className = "${module.initializerName}Base" %> ${macros.generate('initializer', c)} <% } %>''')
    template('implInitializer', appendName: true, body: '''<% if(module.startupInitializer) { %><% c.className = "${module.initializerName}Impl" %> ${macros.generate('implInitializer', c)} <% } %>''')
  }

  templates('intializerComponent',
  init: { c -> c.model.findAllDown({ Component.isInstance(it) }).each { it.n.cap.addAll(['initializerImpl', 'initializerBase', 'initializerWakeup'], 'integ') } },
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Component) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('implInitializerComponent', appendName:true,  body: '''<% if(item.modules.find { it.name.equals('backend') }) { %><% c.className = component.n.cap.initializerImpl %> ${macros.generate('implInitializerComponent', c)}<% } %>''')
    template('initializerComponent', appendName: true, body: '''<% if(item.modules.find { it.name.equals('backend') }) { %><% c.className = component.n.cap.initializerBase %> ${macros.generate('initializerComponent', c)}<% } %>''')
    template('initializerWakeup', appendName: true, body: '''<% if(item.modules.find { it.name.equals('backend') }) { %><% c.className = component.n.cap.initializerWakeup %> ${macros.generate('initializerWakeup', c)}<% } %>''')
  }

  templates('initializerMem',
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Module) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('initializerMem', appendName: true, body: '''<% if(module.startupInitializer) { %><% c.className = "${module.initializerName}Mem" %> ${macros.generate('initializerMem', c)} <% } %>''')
  }

  templates('moduleCache',
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Module) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('moduleCache', appendName: true, body: '''<% if(module.containers.find { it.controller && it.controller.cache } && module.name.equals('backend') ) { %><% c.className = "${module.capShortName}CacheBase" %> ${macros.generate('moduleCache', c)}<% } %>''')
    template('moduleCacheExtends', appendName: true, body: '''<% if(module.containers.find { it.controller && it.controller.cache } && module.name.equals('backend') ) { %><% c.className = "${module.capShortName}Cache" %> ${macros.generate('moduleCacheExtends', c)}<% } %>''')
    template('cacheSynchronizerPeriodic', appendName: true, body: '''<% if(module.containers.find { it.controller && it.controller.cache }) { %><% c.className = "${module.capShortName}CacheSynchronizerPeriodic" %> ${macros.generate('cacheSynchronizerPeriodic', c)} <% } %> ''')
  }

  templates('builderFactory',
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Module) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('builderFactory', appendName: true, body: '''<% if(module.entities) { %><% c.className = module.n.cap.builderFactoryBase %> ${macros.generate('builderFactory', c)} <% } %>''')
    template('builderFactoryExtends', appendName: true, body: '''<% if(module.entities) { %><% c.className = module.n.cap.builderFactory %> ${macros.generate('builderFactoryExtends', c)} <% } %>''')
  }

  templates('producerClient',
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Module) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('producerClient', appendName: true, body: '''<% if(module.services) { %><% c.className = "${module.capShortName}ProducerClient" %> ${macros.generate('producerClient', c)} <% } %>''')
    template('producerTestClient', appendName: true, body: '''<% if(module.services) { %><% c.className = "${module.capShortName}ProducerTestClient" %> ${macros.generate('producerTestClient', c)} <% } %> ''' )
    template('producerEjbClient', appendName: true, body: '''<% if(module.services) { %><% c.className = "${module.capShortName}ProducerEjbClient" %> ${macros.generate('producerEjbClient', c)} <% } %> ''' )
  }

  templates('containerProducerInternal',
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Module) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('containerProducerInternal', appendName: true, body: '''<% if(module.containers) { %><% c.className = "${module.capShortName}ContainerProducerInternal" %> ${macros.generate('containerProducerInternal', c)} <% } %>''')
  }

  templates('implFactory',
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Module) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('implDataFactory', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}DataFactoryImpl" %> ${macros.generate('implDataFactory', c)}<% } %>''')
    template('implModelFactory', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}ModelFactoryImpl" %> ${macros.generate('implModelFactory', c)}<% } %>''')
  }

  templates('factory',
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Module) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('dataFactory', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}DataFactoryBase" %> ${macros.generate('dataFactory', c)}<% } %>''')
    template('modelFactory', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}ModelFactoryBase" %> ${macros.generate('modelFactory', c)}<% } %>''')
  }

  templates('commandsFindersFactoryMem',

  items: { c -> c.model.findAllDown(ee.mdd.model.component.Module) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('commandsFactoryMem', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}CommandsFactoryMemoryBase" %> ${macros.generate('commandsFactoryMem', c)} <% } %>''')
    template('findersFactoryMem', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}FindersFactoryMemoryBase" %> ${macros.generate('findersFactoryMem', c)} <% } %>''')
    template('commandsFactoryMemExtends', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}CommandsFactoryMemory" %> ${macros.generate('commandsFactoryMemExtends', c)} <% } %>''')
    template('findersFactoryMemExtends', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}FindersFactoryMemory" %> ${macros.generate('findersFactoryMemExtends', c)} <% } %>''')
  }

  templates('commandsFindersMem',
  items: { c -> c.model.findAllDown(ee.mdd.model.component.Entity) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('commandsMem', appendName: true, body: '''<% if(item.commands && !item.virtual) { if(item.commands.base) { %><% c.className = item.commands.n.cap.baseMem %><% } else { %><% c.className = item.commands.n.cap.mem %><% } %> ${macros.generate('commandsMem', c)} <% } %>''')
    template('findersMem', appendName: true, body: '''<% if(item.finders && !item.virtual) { if(item.finders.base) { %><% c.className = item.finders.n.cap.baseMem %><% } else { %><% c.className = item.finders.n.cap.mem %><% } %> ${macros.generate('findersMem', c)} <% } %>''')
    template('commandsMemExtends', appendName: true, body: '''<% if(item.commands && item.commands.base && !item.virtual) { %><% c.className = item.commands.n.cap.mem %> ${macros.generate('commandsMemExtends', c)} <% } %>''')
    template('findersMemExtends', appendName: true, body: '''<% if(item.finders && item.finders.base && !item.virtual) { %><% c.className = item.finders.n.cap.mem %> ${macros.generate('findersMemExtends', c)} <% } %>''')
  }
}
