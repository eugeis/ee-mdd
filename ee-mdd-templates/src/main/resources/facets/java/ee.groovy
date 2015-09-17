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
 * @author Eugen Eisler
 */

extModule(name: 'EeCommonBase', artifact: 'ee-common-base') {
  ['Base' : 'ee.common.model', 'EntityIfc' : 'ee.common.model', 'EntityImpl' : 'ee.common.model',
  'Factory' : 'ee.common.model',
  'Labeled' : 'ee.common.model', 'Event' : 'ee.common.model', 'EventImpl' : 'ee.common.model',
  'ConnectionEvent' : 'ee.common.model', 'LinkedObjectCache' : 'ee.common.cache',
  'Finder' : 'ee.common.core', 'Manager' : 'ee.common.core', 'EventListener' : 'ee.common.core',
  'MultiTypeEventListener' : 'ee.common.core', 'ServiceLocator' : 'ee.common.locator',
  'MlKey' : 'ee.common.ml', 'MlKeyBuilder' : 'ee.common.ml' , 'MlKeyImpl' : 'ee.common.ml',
  'Backend' : 'ee.common.annotation', 'Service' : 'ee.common.annotation', 'Environment' : 'ee.common.annotation', 
  'External' : 'ee.common.annotation', 'SupportsEnvironments' : 'ee.common.annotation', 'Traceable' : 'ee.common.annotation',
  'SERVER' : 'static ee.common.integ.RuntimeType.*', 'CLIENT' : 'static ee.common.integ.RuntimeType.*', 'LOCAL' : 'static ee.common.integ.ExecutionType.*', 'MEMORY' : 'static ee.common.integ.ExecutionType.*', 
  'StringUtils' : 'ee.common.util', 'CollectionUtils' : 'ee.common.util', 'ComparisonUtils' : 'ee.common.util',
  'AssertionUtils' : 'ee.common.util', 'LogStringProvider' : 'ee.common.util', 'LogStringBuilder' : 'ee.common.util',
  'BaseTestCase' : 'ee.common.test', 'LifecycleEvent' : 'ee.common.model',
  'NotFoundException' : 'ee.common.exception', 'LongEntityCache' : 'ee.common.cache', 'IntegerEntityCache' : 'ee.common.cache', 'StringEntityCache' : 'ee.common.cache',
  'IntegerCacheOverride' : 'ee.common.cache', 'LongCacheOverride' : 'ee.common.cache', 'StringCacheOverride' : 'ee.common.cache',
  'TempIdCache' : 'ee.common.cache', 'Cache' : 'ee.common.cache', 'LinkToSetCache' : 'ee.common.cache', 'LinkToSet' : 'ee.common.model', 'Model' : 'ee.common.annotation',
  'RootScoped' : 'ee.common.cdi', 'Presenter' : 'ee.common.ui', 'Controller' : 'ee.common.annotation', 'WorkstationType' : 'ee.common.integ', 'SessionPrincipal' : 'ee.common.model',
  'RepState' : 'ee.common.model'].each { n, ns ->
    extType(name: n, namespace: ns)
  }
}

extModule(name: 'EeCommonBaseJpa', artifact: 'ee-common-base_jpa') {
  ['EntityJpa' : 'ee.common.jpa'].each { n, ns ->
    extType(name: n, namespace: ns)
  }
}

extModule(name: 'EeCommonJms', artifact: 'ee-common-jms') {
  ['JmsSendExecutor' : 'ee.common.jms', 'JmsSender' : 'ee.common.jms',
    'JmsDestinationConfig' : 'ee.common.jms',
    'JmsMessagingAdapterTestCase' : 'ee.common.jms',
    'JmsToEventListener' : 'ee.common.jms.jse',
    'SingleTypeEventListenerBridgeByJms' : 'ee.common.jms'].each { n, ns ->
    extType(name: n, namespace: ns)
  }
}