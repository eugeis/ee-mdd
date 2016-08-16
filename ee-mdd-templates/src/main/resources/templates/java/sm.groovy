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


import ee.mdd.model.statemachine.*

import static ee.mdd.generator.OutputType.API
import static ee.mdd.generator.OutputType.LOGIC

/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */

templates('sm') {

  useMacros('commonMacros', '/common/macros')
  useMacros('macros')

    templates('metas', type: API,
    init: { c -> c.model.findAllDown({ StateMachine.isInstance(it) }).each { it.n.cap.addAll(['stateMetaModel', 'metaState'], 'statemachine') } },
    items: { c -> c.model.findAllDown(ee.mdd.model.statemachine.StateMachine) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
      template('metaModel', appendName: true, body: '''<% c.className = item.n.cap.stateMetaModel %> ${macros.generate('metaModel', c)}''')
      template('metaState', appendName: true, body: '''<% c.className = item.n.cap.metaState %> ${macros.generate('metaState', c)}''')
    }

    templates('typeEnums', type: API,
    init: { c -> c.model.findAllDown({ StateMachine.isInstance(it) }).each { it.n.cap.addAll(['stateActionType', 'stateConditionType'], 'statemachine') } },
    items: { c -> c.model.findAllDown(ee.mdd.model.statemachine.StateMachine) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
      template('actionType', appendName: true, body: '''<% c.className = item.n.cap.stateActionType %> ${macros.generate('actionType', c)}''')
      template('conditionType', appendName: true, body: '''<% c.className = item.n.cap.stateConditionType" %> ${macros.generate('conditionType', c)}''')
    }

    templates('controller', type: LOGIC,
    init: { c -> c.model.findAllDown({ StateMachine.isInstance(it) }).each { it.n.cap.addAll(['controllerBase', 'controller',  'controllerBaseImpl', 'controllerImpl', 'bootstrapBase', 'factoryBase'], 'statemachine') } },
    items: { c -> c.model.findAllDown(ee.mdd.model.statemachine.StateMachine) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
      template('controller', appendName: true, body: '''<% c.className = item.n.cap.controllerBase %> ${macros.generate('stateMachineController', c)}''')
      template('controllerExtends', appendName: true, body: '''<% c.className = item.n.cap.controller %> ${macros.generate('stateMachineControllerExtends', c)}''')
      template('implStateMachineController', appendName: true, body: '''<% c.className = item.n.cap.controllerBaseImpl %> ${macros.generate('implStateMachineController', c)}''')
      template('implStateMachineControllerExtends', appendName: true, body: '''<% c.className = item.n.cap.controllerImpl %> ${macros.generate('implStateMachineControllerExtends', c)}''')
      template('controllerBootstrapBase', appendName: true, body: '''<% c.className = item.controller.n.cap.bootstrapBase %> ${macros.generate('controllerBootstrapBase', c)}''')
      template('controllerFactoryBase', appendName: true, body: '''<% c.className = item.controller.n.cap.factoryBase %> ${macros.generate('controllerFactoryBase', c)}''')
    }

    templates('stateMachineEvents', type: API,
    init: { c -> c.model.findAllDown({ StateMachine.isInstance(it) }).each { it.n.cap.addAll(['stateEvent', 'stateEventImpl',  'stateEventType', 'eventFactory', 'eventFactoryBaseImpl', 'eventFactoryImpl'], 'statemachine') } },
    items: { c -> c.model.findAllDown(ee.mdd.model.statemachine.StateMachine) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {

      template('event', appendName: true, body: '''<% c.className = item.n.cap.stateEvent %> ${macros.generate('eventStateMachine', c)}''')
      template('implEventStateMachine', appendName: true, body: '''<% c.className = item.n.cap.stateEventImpl %> ${macros.generate('implEventStateMachine', c)}''')
      template('eventType', appendName: true, body: '''<% c.className = item.n.cap.stateEventType %> ${macros.generate('eventType', c)}''')
      template('eventFactory', appendName: true, body: '''<% c.className = item.n.cap.eventFactory %> ${macros.generate('eventFactory', c)}''')
      template('implEventFactory', appendName: true, body: '''<% c.className = item.n.cap.eventFactoryBaseImpl %> ${macros.generate('implEventFactory', c)}''')
      template('implEventFactoryExtends', appendName: true, body: '''<% c.className = item.n.cap.eventFactoryImpl %> ${macros.generate('implEventFactoryExtends', c)}''')
    }

    templates('stateEventProcessor', type: LOGIC,
    init: { c -> c.model.findAllDown({ StateMachine.isInstance(it) }).each { it.n.cap.addAll(['stateEventProcessor', 'stateEventProcessorBaseImpl',  'stateEventProcessorImpl'], 'statemachine') } },
    items: { c -> c.model.findAllDown(ee.mdd.model.statemachine.StateMachine) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {

      template('stateEventProcessor', appendName: true, body: '''<% c.className = item.n.cap.stateEventProcessor %> ${macros.generate('stateEventProcessor', c)}''')
      template('implStateEventProcessor', appendName: true, body: '''<% c.className = item.n.cap.stateEventProcessorBaseImpl %> ${macros.generate('implStateEventProcessor', c)}''')
      template('implStateEventProcessorExtends', appendName: true, body: '''<% c.className = item.n.cap.stateEventProcessorImpl %> ${macros.generate('implStateEventProcessorExtends', c)}''')
    }

    templates('context', type: LOGIC,
    init: { c -> c.model.findAllDown({ StateMachine.isInstance(it) }).each { it.n.cap.addAll(['contextBase', 'context'], 'statemachine') } },
    items: { c -> c.model.findAllDown(ee.mdd.model.statemachine.StateMachine) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {

      template('context', appendName: true, body: '''<% c.className = item.n.cap.contextBase %> ${macros.generate('context', c)}''')
      template('contextExtends', appendName: true, body: '''<% c.className = item.n.cap.context %> ${macros.generate('contextExtends', c)}''')
    }

    templates('contextManager', type: LOGIC,
    init: { c -> c.model.findAllDown({ StateMachine.isInstance(it) }).each { it.n.cap.addAll(['contextManagerBase', 'contextManager',  'contextManagerBaseImpl', 'contextManagerImpl'], 'statemachine') } },
    items: { c -> c.model.findAllDown(ee.mdd.model.statemachine.StateMachine) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {

      template('contextManager', appendName: true, body: '''<% c.className = item.n.cap.contextManagerBase %> ${macros.generate('contextManager', c)}''')
      template('contextManagerExtends', appendName: true, body: '''<% c.className = item.n.cap.contextManager %> ${macros.generate('contextManagerExtends', c)}''')
      template('implContextManager', appendName: true, body: '''<% c.className = item.n.cap.contextManagerBaseImpl %> ${macros.generate('implContextManager', c)}''')
      template('implContextManagerExtends', appendName: true, body: '''<% c.className = item.n.cap.contextManagerImpl %> ${macros.generate('implContextManagerExtends', c)}''')
    }

    templates('execution', type: LOGIC,
    init: { c -> c.model.findAllDown({ StateMachine.isInstance(it) }).each { it.n.cap.addAll(['actionExecutor', 'transitionExecutionResult'], 'statemachine') } },
    items: { c -> c.model.findAllDown(ee.mdd.model.statemachine.StateMachine) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {

      template('actionExecutor', appendName: true, body:'''<% c.className = item.n.cap.actionExecutor %> ${macros.generate('actionExecutor', c)}''')
      template('transitionExecutionResult', appendName: true, body: '''<% c.className = item.n.cap.transitionExecutionResult %> ${macros.generate('transitionExecutionResult', c)}''')
    }

    templates('action', type: LOGIC,
    init: { c -> c.model.findAllDown({ Action.isInstance(it) }).each { it.n.cap.addAll(['event', 'eventReceiver',  'executor', 'executorImpl'], 'statemachine') } },
    items: { c -> c.model.findAllDown(ee.mdd.model.statemachine.Action) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {

      template('actionEvent', appendName: true, body: '''<% if(item.async) { %><% c.className = item.n.cap.event %> ${macros.generate('actionEvent', c)}<% } %>''')
      template('actionEventReceiver', appendName: true, body: '''<% if(item.async) { %><% c.className = item.n.cap.eventReceiver %> ${macros.generate('eventReceiver', c)}<% } %>''')
      template('executorIfc', appendName: true, body: '''<% if(!item.body && !item.async) { %><% c.className = item.n.cap.executor %> ${macros.generate('executorIfc', c)}<% } %>''')
      template('implExecutor', appendName: true, body: '''<% if (!item.body && !item.async && item.stateMachine.generateDefaultImpl) { %><% c.className = item.n.cap.executorImpl %> ${macros.generate('implExecutor', c)}<% } %>''')
    }

    templates('event',
    init: { c -> c.model.findAllDown({ Event.isInstance(it) }).each { it.n.cap.addAll(['event', 'eventImpl'], 'statemachine') } },
    items: { c -> c.model.findAllDown(ee.mdd.model.statemachine.Event) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {
      template('eventIfc', appendName: true, body: '''<% c.className = item.n.cap.event %> ${macros.generate('eventIfc', c)}''')
      template('implEvent', appendName: true, body: '''<% c.className = item.n.cap.eventImpl %> ${macros.generate('implEvent', c)}''')
    }

    templates('state', type: LOGIC,
    init: { c -> c.model.findAllDown({ State.isInstance(it) }).each { it.n.cap.addAll(['baseEntity', 'entity',  'entityFactory', 'entityBuilderBase', 'entityBuilder'], 'statemachine') } },
    items: { c -> c.model.findAllDown(ee.mdd.model.statemachine.State) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {

      template('eventProcessor', appendName: true, body: '''<% c.className = "${item.stateMachine.capShortName}${item.cap}EventProcessor" %> ${macros.generate('eventProcessor', c)}''')
      template('implEventProcessor', appendName: true, body: '''<% c.className = "${item.stateMachine.capShortName}${item.cap}EventProcessorImpl" %> ${macros.generate('implEventProcessor', c)}''')
      template('stateMetaState', appendName: true, body: '''<% c.className = "${item.stateMachine.capShortName}${item.cap}MetaState" %> ${macros.generate('stateMetaState', c)}''')
    }

    templates('stateTimeoutHandler', type: LOGIC,
    init: { c -> c.model.findAllDown({ StateMachine.isInstance(it) }).each { it.n.cap.addAll(['baseEntity', 'entity',  'entityFactory', 'entityBuilderBase', 'entityBuilder'], 'statemachine') } },
    items: { c -> c.model.findAllDown(ee.mdd.model.statemachine.StateMachine) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {

      template('stateTimeoutHandler', appendName: true, body: '''<% c.className = "${item.capShortName}StateTimeoutHandler" %> ${macros.generate('stateTimeoutHandler', c)}''')
      template('stateTimeoutHandlerBean', appendName: true, body: '''<% c.className = "${item.capShortName}StateTimeoutHandlerBean" %> ${macros.generate('stateTimeoutHandlerBean', c)}''')
      template('stateTimeoutHandlerImpl', appendName: true, body: '''<% c.className = "${item.capShortName}StateTimeoutHandlerImpl" %> ${macros.generate('stateTimeoutHandlerImpl', c)}''')
      template('stateTimeoutHandlerMem', appendName: true, body: '''<% c.className = "${item.capShortName}StateTimeoutHandlerMem" %> ${macros.generate('stateTimeoutHandlerMem', c)}''')
    }

    templates('conditionVerifier', type: LOGIC,
    init: { c -> c.model.findAllDown({ StateMachine.isInstance(it) }).each { it.n.cap.addAll(['conditionVerifier'], 'statemachine') } },
    items: { c -> c.model.findAllDown(ee.mdd.model.statemachine.StateMachine) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {

      template('condVerifier', appendName: true, body: '''<% c.className = item.n.cap.conditionVerifier %> ${macros.generate('condVerifier', c)}''')
    }

    templates('condition', type: LOGIC,
    init: { c -> c.model.findAllDown({ Condition.isInstance(it) }).each { it.n.cap.addAll(['baseEntity', 'entity',  'entityFactory', 'entityBuilderBase', 'entityBuilder'], 'statemachine') } },
    items: { c -> c.model.findAllDown(ee.mdd.model.statemachine.Condition) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {

      template('conditionVerifierIfc', appendName: true, body: '''<% if(!item.body) { %><% c.className = "${item.cap}Verifier" %> ${macros.generate('conditionVerifierIfc', c)}<% } %>''')
      template('conditionVerifier', appendName: true, body: '''<% if(!item.body) { %><% c.className = "${item.cap}VerifierBase" %> ${macros.generate('conditionVerifier', c)}<% } %>''')
      template('implConditionVerifier', appendName: true, body: '''<% if(!item.body && item.parent.generateDefaultImpl) { %><% c.className = "${item.cap}VerifierImpl" %> ${macros.generate('implConditionVerifier', c)}<% } %>''')
    }

    templates('timeoutsConfig', type: LOGIC,
    init: { c -> c.model.findAllDown({ StateMachine.isInstance(it) }).each { it.n.cap.addAll(['timeouts'], 'statemachine') } },
    items: { c -> c.model.findAllDown(ee.mdd.model.statemachine.StateMachine) },
    context: { c -> c.putAll( [ component: c.item.component, module: c.item.module] ) } ) {

      template('timeoutsConfig', appendName: true, body: '''<% c.className = item.n.cap.timeouts %> ${macros.generate('timeoutsConfig', c)}''')
    }
   }
