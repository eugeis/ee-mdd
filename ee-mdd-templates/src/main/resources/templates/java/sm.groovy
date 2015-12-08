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

import ee.mdd.model.statemachine.Action
import ee.mdd.model.statemachine.Condition
import ee.mdd.model.statemachine.Event
import ee.mdd.model.statemachine.State
import ee.mdd.model.statemachine.StateMachine

/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */

templates('sm') {

  useMacros('commonMacros', '/common/macros')
  useMacros('macros')

    templates('metas', type: API,
    items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {
      template('metaModel', appendName: true, body: '''<% c.className = "${item.capShortName}StateMetaModel" %> ${macros.generate('metaModel', c)}''')
      template('metaState', appendName: true, body: '''<% c.className = "${item.capShortName}MetaState" %> ${macros.generate('metaState', c)}''')
    }

    templates('typeEnums', type: API,
    items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {
      template('actionType', appendName: true, body: '''<% c.className = "${item.capShortName}StateActionType" %> ${macros.generate('actionType', c)}''')
      template('conditionType', appendName: true, body: '''<% c.className = "${item.capShortName}StateConditionType" %> ${macros.generate('conditionType', c)}''')
    }

    templates('controller', type: LOGIC,
    items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {
      template('controller', appendName: true, body: '''<% c.className = "${item.capShortName}ControllerBase" %> ${macros.generate('stateMachineController', c)}''')
      template('controllerExtends', appendName: true, body: '''<% c.className = "${item.capShortName}Controller" %> ${macros.generate('stateMachineControllerExtends', c)}''')
      template('implStateMachineController', appendName: true, body: '''<% c.className = "${item.capShortName}ControllerBaseImpl" %> ${macros.generate('implStateMachineController', c)}''')
      template('implStateMachineControllerExtends', appendName: true, body: '''<% c.className = "${item.capShortName}ControllerImpl" %> ${macros.generate('implStateMachineControllerExtends', c)}''')
      template('controllerBootstrapBase', appendName: true, body: '''<% c.className = "${item.controller.cap}BootstrapBase" %> ${macros.generate('controllerBootstrapBase', c)}''')
      template('controllerFactoryBase', appendName: true, body: '''<% c.className = "${item.controller.cap}FactoryBase" %> ${macros.generate('controllerFactoryBase', c)}''')
    }

    templates('stateMachineEvents', type: API,
    items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {

      template('event', appendName: true, body: '''<% c.className = "${item.key.capitalize()}StateEvent" %> ${macros.generate('eventStateMachine', c)}''')
      template('implEventStateMachine', appendName: true, body: '''<% c.className = "${item.capShortName}StateEventImpl" %> ${macros.generate('implEventStateMachine', c)}''')
      template('eventType', appendName: true, body: '''<% c.className = "${item.key.capitalize()}StateEventType" %> ${macros.generate('eventType', c)}''')
      template('eventFactory', appendName: true, body: '''<% c.className = "${item.key.capitalize()}EventFactory" %> ${macros.generate('eventFactory', c)}''')
      template('implEventFactory', appendName: true, body: '''<% c.className = "${item.capShortName}EventFactoryBaseImpl" %> ${macros.generate('implEventFactory', c)}''')
      template('implEventFactoryExtends', appendName: true, body: '''<% c.className = "${item.capShortName}EventFactoryImpl" %> ${macros.generate('implEventFactoryExtends', c)}''')
    }

    templates('stateEventProcessor', type: LOGIC,
    items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {

      template('stateEventProcessor', appendName: true, body: '''<% c.className = "${item.capShortName}StateEventProcessor" %> ${macros.generate('stateEventProcessor', c)}''')
      template('implStateEventProcessor', appendName: true, body: '''<% c.className = "${item.capShortName}StateEventProcessorBaseImpl" %> ${macros.generate('implStateEventProcessor', c)}''')
      template('implStateEventProcessorExtends', appendName: true, body: '''<% c.className = "${item.capShortName}StateEventProcessorImpl" %> ${macros.generate('implStateEventProcessorExtends', c)}''')
    }

    templates('context', type: LOGIC,
    items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {

      template('context', appendName: true, body: '''<% c.className = "${item.capShortName}ContextBase" %> ${macros.generate('context', c)}''')
      template('contextExtends', appendName: true, body: '''<% c.className = "${item.capShortName}Context" %> ${macros.generate('contextExtends', c)}''')
    }

    templates('contextManager', type: LOGIC,
    items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {

      template('contextManager', appendName: true, body: '''<% c.className = "${item.capShortName}ContextManagerBase" %> ${macros.generate('contextManager', c)}''')
      template('contextManagerExtends', appendName: true, body: '''<% c.className = "${item.capShortName}ContextManager" %> ${macros.generate('contextManagerExtends', c)}''')
      template('implContextManager', appendName: true, body: '''<% c.className = "${item.capShortName}ContextManagerBaseImpl" %> ${macros.generate('implContextManager', c)}''')
      template('implContextManagerExtends', appendName: true, body: '''<% c.className = "${item.capShortName}ContextManagerImpl" %> ${macros.generate('implContextManagerExtends', c)}''')
    }

    templates('execution', type: LOGIC,
    items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {

      template('actionExecutor', appendName: true, body:'''<% c.className = "${item.capShortName}ActionExecutor" %> ${macros.generate('actionExecutor', c)}''')
      template('transitionExecutionResult', appendName: true, body: '''<% c.className = "${item.capShortName}TransitionExecutionResult" %> ${macros.generate('transitionExecutionResult', c)}''')
    }

    templates('action', type: LOGIC,
    items: { c -> c.model.findAllRecursiveDown( {Action.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {

      template('actionEvent', appendName: true, body: '''<% if(item.async) { %><% c.className = "${item.cap}Event" %> ${macros.generate('actionEvent', c)}<% } %>''')
      template('actionEventReceiver', appendName: true, body: '''<% if(item.async) { %><% c.className = "${item.cap}EventReceiver" %> ${macros.generate('actionEventReceiver', c)}<% } %>''')
      template('executorIfc', appendName: true, body: '''<% if(!item.body && !item.async) { %><% c.className = "${item.cap}Executor" %> ${macros.generate('executorIfc', c)}<% } %>''')
      template('implExecutor', appendName: true, body: '''<% if (!item.body && !item.async && item.stateMachine.generateDefaultImpl) { %><% c.className = "${item.cap}ExecutorImpl" %> ${macros.generate('implExecutor', c)}<% } %>''')
    }

    templates('event',
    items: { c -> c.model.findAllRecursiveDown( {Event.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {
      template('eventIfc', appendName: true, body: '''<% c.className = "${item.cap}Event" %> ${macros.generate('eventIfc', c)}''')
      template('implEvent', appendName: true, body: '''<% c.className = "${item.cap}EventImpl" %> ${macros.generate('implEvent', c)}''')
    }

    templates('state', type: LOGIC,
    items: { c -> c.model.findAllRecursiveDown( {State.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {

      template('eventProcessor', appendName: true, body: '''<% c.className = "${item.stateMachine.capShortName}${item.cap}EventProcessor" %> ${macros.generate('eventProcessor', c)}''')
      template('implEventProcessor', appendName: true, body: '''<% c.className = "${item.stateMachine.capShortName}${item.cap}EventProcessorImpl" %> ${macros.generate('implEventProcessor', c)}''')
      template('stateMetaState', appendName: true, body: '''<% c.className = "${item.stateMachine.capShortName}${item.cap}MetaState" %> ${macros.generate('stateMetaState', c)}''')
    }

    templates('stateTimeoutHandler', type: LOGIC,
    items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {

      template('stateTimeoutHandler', appendName: true, body: '''<% c.className = "${item.capShortName}StateTimeoutHandler" %> ${macros.generate('stateTimeoutHandler', c)}''')
      template('stateTimeoutHandlerBean', appendName: true, body: '''<% c.className = "${item.capShortName}StateTimeoutHandlerBean" %> ${macros.generate('stateTimeoutHandlerBean', c)}''')
      template('stateTimeoutHandlerImpl', appendName: true, body: '''<% c.className = "${item.capShortName}StateTimeoutHandlerImpl" %> ${macros.generate('stateTimeoutHandlerImpl', c)}''')
      template('stateTimeoutHandlerMem', appendName: true, body: '''<% c.className = "${item.capShortName}StateTimeoutHandlerMem" %> ${macros.generate('stateTimeoutHandlerMem', c)}''')
    }

    templates('conditionVerifier', type: LOGIC,
    items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {

      template('condVerifier', appendName: true, body: '''<% c.className = "${item.capShortName}ConditionVerifier" %> ${macros.generate('condVerifier', c)}''')
    }

    templates('condition', type: LOGIC,
    items: { c -> c.model.findAllRecursiveDown( {Condition.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {

      template('conditionVerifierIfc', appendName: true, body: '''<% if(!item.body) { %><% c.className = "${item.cap}Verifier" %> ${macros.generate('conditionVerifierIfc', c)}<% } %>''')
      template('conditionVerifier', appendName: true, body: '''<% if(!item.body) { %><% c.className = "${item.cap}VerifierBase" %> ${macros.generate('conditionVerifier', c)}<% } %>''')
      template('implConditionVerifier', appendName: true, body: '''<% if(!item.body && item.parent.generateDefaultImpl) { %><% c.className = "${item.cap}VerifierImpl" %> ${macros.generate('implConditionVerifier', c)}<% } %>''')
    }

    templates('timeoutsConfig', type: LOGIC,
    items: { c -> c.model.findAllRecursiveDown( {StateMachine.isInstance(it) }) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'statemachine'] ) } ) {

      template('timeoutsConfig', appendName: true, body: '''<% c.className = "${item.capShortName}Timeouts" %> ${macros.generate('timeoutsConfig', c)}''')
    }
   }
