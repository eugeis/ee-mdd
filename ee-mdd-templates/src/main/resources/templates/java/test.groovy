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
import ee.mdd.model.component.Container
import ee.mdd.model.component.Entity
import ee.mdd.model.component.EnumType
import ee.mdd.model.component.Module
import ee.mdd.model.statemachine.Condition
import ee.mdd.model.statemachine.StateMachine

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
  
  templates('moduleCacheTest',
  items: { c -> c.model.findAllRecursiveDown( { Module.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
    template('moduleCacheTest', appendName: true, body: '''<% def cachedContainers = module.containers.findAll {it.controller && it.controller.cache}; if (cachedContainers) { %><% c.className = "${module.capShortName}CacheTestBase" %> ${macros.generate('moduleCacheTest', c)}<% } %>''')
    template('moduleCacheTestExtends', appendName: true, body: '''<% if (module.containers.find { it.controller && it.controller.cache }) { %><% c.className = "${module.capShortName}CacheTest" %> ${macros.generate('moduleCacheTestExtends', c)}<% } %>''')
  }
  
  templates ('bridgeTests',
  items: { c -> c.model.findAllRecursiveDown( { Channel.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'integ' ] ) } ) {

    template('notificationPluginTest', appendName: true, body: '''<% def modules = []; modules.addAll(component.backends.findAll { m -> m.entities }) %><% if(modules) { %><% c.className = c.item.n.cap.notificationPluginTest %> ${macros.generate('notificationPluginTest', c)}<% } %>''')
    template('jmsToCdiTest' , appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.jmsToCdiTest %> ${macros.generate('jmsToCdiTest', c)} <% } %>''')
    template('cdiToJmsTest', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.cdiToJmsTest %> ${macros.generate('cdiToJmsTest', c)} <% } %>''')
    template('eventToCdiTest', appendName: true, body: '''<% if (module.entities || module.configs) { %><% c.className = c.item.n.cap.eventToCdiTest %> ${macros.generate('eventToCdiTest', c)} <% } %>''')
    template('eventToCdiExternalTest', appendName: true, body: '''<% if(module.entities || module.configs) { %><% c.className = c.item.n.cap.eventToCdiExternalTest %> ${macros.generate('eventToCdiExternalTest, c)}<% } %>''')
  }
  
  templates('containerTests',
  items: { c -> c.model.findAllRecursiveDown( { Container.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'impl' ] ) } ) {
    template('implContainerFactoryTest', appendName: true, body: '''<% if(!item.virtual) { %><% c.className = item.n.cap.implFactoryTest %> ${macros.generate('implContainerFactoryTest', c)} <% } %>''')
    template('implContainerControllerTest', appendName: true, body: '''<% def controller = item.controller %><% if(controller && controller.base) { %><% c.className = controller.n.cap.BaseTestImpl%>${macros.generate('implContainerControllerTest', c)} <% } %>''')
    template('implContainerControllerTestExtends', appendName: true, body: '''<% def controller = item.controller %><% if(controller && controller.base) { %><% c.className = controller.n.cap.implTest %>${macros.generate('implContainerControllerTestExtends', c)} <% } %>''')
    template('implContainerVersionsTest', appendName: true, body: '''<% c.className = item.n.cap.versionsImplTestBase %> ${macros.generate('implContainerVersionsTest', c)}''')
    template('implContainerVersionsTestExtends', appendName: true, body: '''<% c.className = item.n.cap.versionsImplTest %> ${macros.generate('implContainerVersionsTestExtends', c)}''')
    template('containerBuilderTest', appendName: true, body: '''<% c.className = "${item.cap}BuilderBase" %> ${macros.generate('containerBuilderTest', c)}''')
    template('containerBuilderTestExtends', appendName: true, body: '''<% c.className = "${item.cap}Builder" %> ${macros.generate('containerBuilderTestExtends', c)}''')
    template('containerControllerDelegateTest', appendName: true, body: '''<% def controller = item.controller %><% if(controller) { %><% c.className = controller.n.cap.delegateTestBase %>${macros.generate('containerControllerDelegateTest', c)}<% } %>''')
    template('containerControllerDelegateTestExtends', appendName: true, body: '''<% def controller = item.controller %><% if(controller) { %><% c.className = controller.n.cap.delegateTest %>${macros.generate('containerControllerDelegateTestExtends', c)}<% } %>''')
  }
  
  templates('converterTest',
  items: { c -> c.model.findAllRecursiveDown( { Module.isInstance(it) }) },
  context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'impl' ] ) } ) {
    template('implConverterTest', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}ConverterTestImpl" %>${macros.generate('implConverterTest', c)}<% } %>''')
    template('converterTest', appendName: true, body: '''<% if(module.entities) { %><% c.className = "${module.capShortName}ConverterTest" %> ${macros.generate('converterTest', c)}<% } %>''')
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
    template('constantsTest', appendName: true, body: '''<% c.className = c.item.n.cap.constantsTest%><% c.path = "ee-mdd_example-shared/src-gen/test/java/${c.item.ns.path}/integ/${c.className}.java" %>${macros.generate('constantsTest', c)}''')
  }
  
}
