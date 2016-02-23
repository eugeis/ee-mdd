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

import static ee.mdd.generator.OutputPurpose.*
import static ee.mdd.generator.OutputType.*
import ee.mdd.model.component.Channel
import ee.mdd.model.component.Component
import ee.mdd.model.component.Config
import ee.mdd.model.component.Container
import ee.mdd.model.component.Entity
import ee.mdd.model.component.EnumType
import ee.mdd.model.component.Facade
import ee.mdd.model.component.Module
import ee.mdd.model.statemachine.Condition
import ee.mdd.model.statemachine.StateMachine
import ee.mdd.model.ui.View

/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */

templates('test', purpose: UNIT_TEST) {

  useMacros('commonMacros', '/common/macros')
  useMacros('macros')

  templates ('modelTest',
  items: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'impl'] ) } ) {
    template('test', appendName: true, body: '''<% if(!item.virtual) { %><% c.className = "${item.n.cap.test}Base"; c.itemInit = "new $item.n.cap.impl()" %>${macros.generate('test', c)}<% } %>''')
    template('testExtends', appendName: true, body: '''<% if(!item.virtual) { c.className = item.n.cap.test %>${macros.generate('testExtends', c)}<% } %>''')
  }
  
  templates('beanTest',
  items: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'ejb'] ) } ) {
    template('beanTest', appendName: true, body: '''<% c.className = item.beanTestBaseName %> ${macros.generate('beanTest', c)}''')
    template('beanTestExtends', appendName: true, body: '''<% if(!item.virtual) { %><% c.className = item.beanTestName %> ${macros.generate('beanTestExtends', c)}<% } %>''')
  }
  
  templates('cacheTest',
  items: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'cache'] ) } ) {
    template('cacheTest', appendName: true, body: '''<% c.className = item.cache.n.cap.testBase %> ${macros.generate('cacheTest', c)}''')
    template('cacheTestExtends', appendName: true, body: '''<% c.className = item.cache.n.cap.test %> ${macros.generate('cacheTestExtends', c)} ''')
    template('cacheOverrideTest', appendName: true, body: '''<% c.className = item.cache.n.cap.overrideTestBase %><% c.override = true %> ${macros.generate('cacheTest', c)}''')
    template('cacheOverrideTestExtends', appendName: true, body: '''<% c.className = item.cache.n.cap.overrideTest %><% c.override = true %> ${macros.generate('cacheTestExtends', c)}''')
    template('deltaCacheTest', appendName: true, body: '''<% c.className = item.deltaCache.n.cap.testBase %> ${macros.generate('deltaCacheTest', c)}''')
    template('deltaCacheTestExtends', appendName: true, body: '''<% c.className = item.deltaCache.n.cap.test %> ${macros.generate('deltaCacheTestExtends', c)}''')
  }
  
  templates('implCacheTest',
  items: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'impl'] ) } ) {
    template('implDeltaCacheTest', appendName: true, body: '''<% if(!item.virtual) { %><% c.className = item.deltaCache.n.cap.implTestBase %>${macros.generate('implDeltaCacheTest', c)}<% } %>''')
    template('implDeltaCacheTestExtends', appendName: true, body: '''<% if(!item.virtual) { %><% c.className = item.deltaCache.n.cap.implTest %>${macros.generate('implDeltaCacheTestExtends', c)}<% } %>''')
  }
    
  templates('moduleCacheTest',
  items: { c -> c.model.findAllRecursiveDown( { Module.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('moduleCacheTest', appendName: true, body: '''<% def cachedContainers = module.containers.findAll {it.controller && it.controller.cache}; if (cachedContainers) { %><% c.className = "${module.capShortName}CacheTestBase" %> ${macros.generate('moduleCacheTest', c)}<% } %>''')
    template('moduleCacheTestExtends', appendName: true, body: '''<% if (module.containers.find { it.controller && it.controller.cache }) { %><% c.className = "${module.capShortName}CacheTest" %> ${macros.generate('moduleCacheTestExtends', c)}<% } %>''')
  }
    
  templates ('bridgeTest',
  items: { c -> c.model.findAllRecursiveDown( { Channel.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'integ' ] ) } ) {

    template('notificationPluginTest', appendName: true, body: '''<% def modules = []; modules.addAll(component.backends.findAll { m -> m.entities }) %><% if(modules) { %><% c.className = c.item.n.cap.notificationPluginTest %> ${macros.generate('notificationPluginTest', c)}<% } %>''')
    template('jmsToCdiTest' , appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.jmsToCdiTest %> ${macros.generate('jmsToCdiTest', c)} <% } %>''')
    template('cdiToJmsTest', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.cdiToJmsTest %> ${macros.generate('cdiToJmsTest', c)} <% } %>''')
    template('eventToCdiTest', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.eventToCdiTest %> ${macros.generate('eventToCdiTest', c)} <% } %>''')
    template('eventToCdiExternalTest', appendName: true, body: '''<% if(module.entities || module.configs) { %><% c.className = c.item.n.cap.eventToCdiExternalTest %> ${macros.generate('eventToCdiExternalTest, c)}<% } %>''')
  }
  
  templates('implContainerTest',
  items: { c -> c.model.findAllRecursiveDown( { Container.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'impl' ] ) } ) {
    template('implContainerTest', appendName: true, body: '''<% c.className = item.n.cap.implTestBase %> ${macros.generate('implContainerTest', c)}''')
    template('implContainerTestExtends', appendName: true, body: '''<% c.className = item.n.cap.implTest %> ${macros.generate('implContainerTestExtends', c)}''')
    template('implContainerFactoryTest', appendName: true, body: '''<% if(!item.virtual) { %><% c.className = item.n.cap.implFactoryTest %> ${macros.generate('implContainerFactoryTest', c)} <% } %>''')
    template('implContainerControllerTest', appendName: true, body: '''<% def controller = item.controller %><% if(controller && controller.base) { %><% c.className = controller.n.cap.BaseTestImpl%>${macros.generate('implContainerControllerTest', c)} <% } %>''')
    template('implContainerControllerTestExtends', appendName: true, body: '''<% def controller = item.controller %><% if(controller && controller.base) { %><% c.className = controller.n.cap.implTest %>${macros.generate('implContainerControllerTestExtends', c)} <% } %>''')
    template('implContainerVersionsTest', appendName: true, body: '''<% c.className = item.n.cap.versionsImplTestBase %> ${macros.generate('implContainerVersionsTest', c)}''')
    template('implContainerVersionsTestExtends', appendName: true, body: '''<% c.className = item.n.cap.versionsImplTest %> ${macros.generate('implContainerVersionsTestExtends', c)}''')
    template('implContainerDeltaTest', appendName: true, body: '''<% c.className = item.n.cap.deltaImplTestBase %> ${macros.generate('implContainerDeltaTest', c)}''')
    template('implContainerDeltaTestExtends', appendName: true, body: '''<% c.className = item.n.cap.deltaImplTest %> ${macros.generate('implContainerDeltaTestExtends', c)}''')
    template('containerControllerDelegateTest', appendName: true, body: '''<% def controller = item.controller %><% if(controller) { %><% c.className = controller.n.cap.delegateTestBase %>${macros.generate('containerControllerDelegateTest', c)}<% } %>''')
    template('containerControllerDelegateTestExtends', appendName: true, body: '''<% def controller = item.controller %><% if(controller) { %><% c.className = controller.n.cap.delegateTest %>${macros.generate('containerControllerDelegateTestExtends', c)}<% } %>''')
  }
  
  templates('containerBuilderTest',
  items: { c -> c.model.findAllRecursiveDown( { Container.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'builder' ] ) } ) {
    template('containerBuilderTest', appendName: true, body: '''<% c.className = "${item.cap}BuilderBase" %> ${macros.generate('containerBuilderTest', c)}''')
    template('containerBuilderTestExtends', appendName: true, body: '''<% c.className = "${item.cap}Builder" %> ${macros.generate('containerBuilderTestExtends', c)}''')
  }
  
  templates('containerIdsTest',
  items: { c -> c.model.findAllRecursiveDown( { Container.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('containerIdsTest', appendName: true, body: '''<% c.className = item.n.cap.idsTest %> ${macros.generate('containerIdsTest', c)}''')
    template('containerIdsTestCase', appendName: true, body: '''<% c.className = item.n.cap.idsTestCase %> ${macros.generate('containerIdsTestCase', c)}''')
  }
  
  templates('containerDiffTest',
  items: { c -> c.model.findAllRecursiveDown( { Container.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('containerDiffTest', appendName: true, body: '''<% c.className = item.n.cap.diffTest %> ${macros.generate('containerDiffTest', c)}''')
    template('containerDiffTestCase', appendName: true, body: '''<% c.className = item.n.cap.diffTestCase %> ${macros.generate('containerDiffTestCase', c)}''')
  }
    
  
  templates('converterTest',
  items: { c -> c.model.findAllRecursiveDown( { Module.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'impl' ] ) } ) {
    template('implConverterTest', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}ConverterTestImpl" %>${macros.generate('implConverterTest', c)}<% } %>''')
    template('converterTest', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}ConverterTest" %> ${macros.generate('converterTest', c)}<% } %>''')
  }
  
  templates('commandsFindersMemoryTest',
  items: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'mem'] ) } ) {
    template('commandsMemoryTest', appendName: true, body: '''<% if(item.commands && !item.virtual) { %><% c.className = item.commands.n.cap.memoryTest %> ${macros.generate('commandsMemoryTest', c)} <% } %>''')
    template('findersMemoryTest', appendName: true, body: '''<% if(item.finders && !item.virtual) { %><% c.className = item.finders.n.cap.memoryTest %> ${macros.generate('findersMemoryTest', c)} <% } %>''')
  }
  
  templates('commandsFindersTest',
  items: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('commandsTest', appendName: true, body: '''<% if (item.commands && !item.virtual) { %><% if(item.commands.base) { c.className = item.commands.n.cap.baseTestImpl } else { c.className = item.commands.n.cap.testImpl } %> ${macros.generate('commandsTest', c)}<% } %>''')
    template('commandsTestExtends', appendName: true, body: '''<% if (item.commands && item.commands.base && !item.virtual) { %><% c.className = item.commands.n.cap.testImpl %> ${macros.generate('commandsTestExtends', c)}<% } %>''')
    template('findersTest', appendName: true, body: '''<% if (item.finders && !item.virtual) { %><% if(item.commands.base) { c.className = item.finders.n.cap.baseTestImpl } else { c.className = item.finders.n.cap.testImpl } %> ${macros.generate('findersTest', c)}<% } %>''')
    template('findersTestExtends', appendName: true, body: ''' <% if (item.finders && item.finders.base && !item.virtual) { %><% c.className = item.finders.n.cap.testImpl %> ${macros.generate('findersTestExtends', c)}<% } %>''')
  }
  
  templates('implCommandsFindersFactoryTest',
  items: { c -> c.model.findAllRecursiveDown( { Module.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'impl'] ) } ) {
    template('implCommandsFactoryTest', appendName: true, body: '''<% def entitiesWithCommands = module.entities.findAll { !it.virtual && it.commands } %><% if(entitiesWithCommands) { %><% c.className = "${module.capShortName}CommandsFactoryImplTest" %>${macros.generate('implCommandsFactoryTest', c)}<% } %> ''')
    template('implFindersFactoryTest', appendName: true, body: '''<% def entitiesWithFinders = module.entities.findAll { !it.virtual && it.finders } %><% if(entitiesWithFinders) { %><% c.className = "${module.capShortName}FindersFactoryImplTest" %>${macros.generate('implFindersFactoryTest', c)}<% } %> ''')
  }
  
  templates('commandsFindersLocalTest',
  items: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'impl'] ) } ) {
    template('commandsLocalTest', appendName: true, body: '''<% if(item.commands && !item.virtual) { %><% c.className = item.commands.n.cap.localTest %>${macros.generate('commandsLocalTest', c)}<% } %>''')
    template('findersLocalTest', appendName: true, body: '''<% if(item.finders && !item.virtual) { %><% c.className = item.finders.n.cap.localTest %>${macros.generate('findersLocalTest', c)}<% } %>''')
  }
    
  templates('initializerMemTest',
  items: { c -> c.model.findAllRecursiveDown( { Module.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'mem' ] ) } ) {
    template('initializerMemTest', appendName: true, body: '''<% if(module.startupInitializer) { %><% c.className = "${module.initializerName}MemTest" %> ${macros.generate('initializerMemTest', c)}<% } %>''')
  }
  
  templates('initializerImplTest',
  items: { c -> c.model.findAllRecursiveDown( { Module.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {
    template('initializerImplTest', appendName: true, body: '''<% if(module.startupInitializer) { %><% c.className = "${module.initializerName}ImplTest" %> ${macros.generate('initializerImplTest', c)}<% } %>''')
  }

  templates ('enumTest',
  items: { c -> c.model.findAllRecursiveDown( { EnumType.isInstance(it) }) },
  context: { c -> def enumType = c.item; c.putAll( [ component: enumType.component, module: enumType.module, enumType: enumType ] ) } ) {

    template('testEnum', appendName: true, body: '''<% c.className = "${item.n.cap.test}Base" %>${macros.generate('testEnum', c)}''')
    template('testEnumExtends', appendName: true, body: '''<% c.className = item.n.cap.test %>${macros.generate('testExtends', c)}''')
  }
  
  templates('stateMachineTests',
  items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {
    
    template('stateMachineControllerBaseTest', appendName: true,  body: '''<% if(!item.entity.virtual) { %><% c.className = item.controller.base ? "${item.controller.cap}BaseTestImpl" : "${item.controller.cap}TestImpl" %> ${macros.generate('stateMachineControllerBaseTest', c)} <% } %>''')
    template('controllerLocalTestInteg', appendName: true, body: '''<% if(!item.entity.virtual) { %><% c.className = c.item.controller.n.cap.localTestInteg %> ${macros.generate('controllerLocalTestInteg', c)} <% } %>''')
    template('controllerMemoryTestInteg', appendName: true, body: '''<% if(!item.entity.virtual) { %><% c.className = c.item.controller.n.cap.memoryTestInteg %> ${macros.generate('controllerMemoryTestInteg', c)} <% } %>''')
    template('controllerTest', appendName: true, body: '''<% if(!item.entity.virtual) { %><% c.className = c.item.controller.n.cap.testImpl %> ${macros.generate('controllerTest', c)} <% } %>''')
  }
    
  templates('stateMachineConditionTests',
  items: { c -> c.model.findAllRecursiveDown( {Condition.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {
    template('conditionHandlerTest', appendName: true, body: '''<% if (!item.body && item.parent.generateDefaultImpl) { %><% c.className = item.n.cap.testBase %> ${macros.generate('conditionHandlerTest', c)} <% } %>''')
    template('conditionHandlerTestExtends', appendName: true, body: '''<% if (!item.body && item.parent.generateDefaultImpl) { %><% c.className = item.n.cap.test %> ${macros.generate('conditionHandlerTestExtends', c)} <% } %>''')
  }
  
  templates('constantsTest',
  items: { c -> c.model.findAllRecursiveDown( {Component.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('constantsTest', appendName: true, body: '''<% c.className = c.item.n.cap.constantsTest %><% c.path = "ee-mdd_example-shared/src-gen/test/java/${c.item.ns.path}/integ/${c.className}.java" %>${macros.generate('constantsTest', c)}''')
  }
  
  templates('presenterTest',
  items: { c -> c.model.findAllRecursiveDown( {View.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('presenterTest', appendName: true, body: '''<% c.className = "${item.presenter.cap}TestBase" %> ${macros.generate('presenterTest', c)}''')
    template('presenterTestExtends', appendName: true, body: '''<% c.className = "${item.presenter.cap}Test" %> ${macros.generate('presenterTestExtends', c)}''')
  }
  
  templates('viewModelTest',
  items: { c -> c.model.findAllRecursiveDown( {View.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('viewModelTest', appendName: true, body: '''<% if (item.model) { %><% c.className = "${item.model.cap}TestBase" %> ${macros.generate('viewModelTest', c)} <% } %>''')
    template('viewModelTestExtends', appendName: true, body: '''<% if (item.model) { %><% c.className = "${item.model.cap}Test" %> ${macros.generate('viewModelTestExtends', c)} <% } %>''')
  }
  
  templates('mediatorTest',
  items: { c -> c.model.findAllRecursiveDown( {View.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('mediatorTest', appendName: true, body: '''<% if (item.withMediator) { %><% c.className = "${item.domainName}MediatorTestBase" %> ${macros.generate('mediatorTest', c)} <% } %>''')
    template('mediatorTestExtends', appendName: true, body: '''<% if (item.withMediator) { %><% c.className = "${item.domainName}MediatorTest" %> ${macros.generate('mediatorTestExtends', c)} <% } %>''')
  }
  
  templates('guidoTest',
  items: { c -> c.model.findAllRecursiveDown( {View.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('viewGuidoTest', appendName: true, body: '''<% c.className = item.n.cap.guidoTestBase %> ${macros.generate('viewGuidoTest', c)}''')
    template('viewGuidoTestExtends', appendName: true, body: '''<% c.className = item.n.cap.guidoTest %> ${macros.generate('guidoTestExtends', c)}''')
    template('dialogGuidoTest', appendName: true, body: '''<% if(item.dialog) { %><% c.className = item.dialog.n.cap.guidoTestBase %> ${macros.generate('dialogGuidoTest', c)}<% } %>''')
    template('dialogGuidoTestExtends', appendName: true, body: '''<% if(item.dialog) { %><% c.className = item.dialog.n.cap.guidoTest %> ${macros.generate('guidoTestExtends', c)}<% } %>''')
  }
  
  templates('serviceDelegateTest',
  items: { c -> c.model.findAllRecursiveDown( {Facade.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('serviceDelegateTest', appendName: true, body: '''<% c.className = item.n.cap.delegateTestBase %> ${macros.generate('serviceDelegateTest', c)}''')
    template('serviceDelegateTestExtends', appendName: true, body: '''<% c.className = item.n.cap.delegateTest %> ${macros.generate('serviceDelegateTestExtends', c)}''')
  }
  
  templates('implControllerTest',
  items: { c -> c.model.findAllRecursiveDown( {Config.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'impl'] ) } ) {
    template('implControllerTest', appendName: true, body: '''<% if(item.controller && item.controller.base) { %><% c.className = item.controller.n.cap.impl %> ${macros.generate('implControllerTest', c)} <% } %>''')
  }
  
  templates('configTest',
  items: { c -> c.model.findAllRecursiveDown( {Config.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('configTest', appendName: true, body: '''<% if(item.base) { %><% c.className = item.n.cap.testBase %><% } else { %><% c.className = item.n.cap.test %><% } %> ${macros.generate('configTest', c)} ''')
    template('configTestExtends', appendName: true, body: '''<% if(item.base) { %><% c.className = item.n.cap.test %> ${macros.generate('configTestExtends', c)} <% } %>''')
  }
  
  templates('containerProducerInternalTest',
  items: { c -> c.model.findAllRecursiveDown( {Module.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('containerProducerInternalTest', appendName: true, body: '''<% if(module.containers) { %><% c.className = "${module.capShortName}ContainerProducerInternal" %> ${macros.generate('containerProducerInternalTest', c)}<% } %> ''')
  }
    
  templates('unitTestHelper',
  items: { c -> c.model.findAllRecursiveDown( {Module.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('unitTestHelper', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}UnitTestHelperBase" %> ${macros.generate('unitTestHelper', c)}<% } %>''')
  }
      
    
  
}
