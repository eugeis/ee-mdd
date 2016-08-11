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

def nameToNamespace = ['EventImpl' : 'com.siemens.ra.cg.pl.common.base.messaging.impl',
  'ConnectionMetaEvent' : 'com.siemens.ra.cg.pl.common.base.model.event', 'Base' : 'com.siemens.ra.cg.pl.common.base.model', 'BaseEntity' : 'com.siemens.ra.cg.pl.common.base.model',
  'IdSetter' : 'com.siemens.ra.cg.pl.common.base.model', 'BaseEntityImpl' : 'com.siemens.ra.cg.pl.common.ejb.model', 'ControlguideNotFoundException' : 'com.siemens.ra.cg.pl.common.base.exception',
  'IdEntity' : 'com.siemens.ra.cg.pl.common.base.model', 'TimeUtils' : 'com.siemens.ra.cg.pl.common.base.util', 'ActionType' : 'com.siemens.ra.cg.pl.common.base.model',
  'TimeUtils' : 'com.siemens.ra.cg.pl.common.base.util', 'IllegalStateException' : 'com.siemens.ra.cg.pl.common.base.exception', 'now' : 'static com.siemens.ra.cg.pl.common.base.util.TimeUtils.*',
  'CommonConstants' : 'com.siemens.ra.cg.pl.common.base.integ', 'ThreadBoundProxyHandler' : 'com.siemens.ra.cg.pl.common.base.integ', 'JmxUtils' : 'com.siemens.ra.cg.pl.common.base.util',
  'SingletonContainer' : 'com.siemens.ra.cg.pl.common.base.integ', 'NamedThreadFactoryHolderByPrefix' : 'com.siemens.ra.cg.pl.common.base.util', 'HolderImpl' : 'com.siemens.ra.cg.pl.common.base',
  'PublisherEmpty' : 'com.siemens.ra.cg.pl.common.base.cdi','ConditionVerifierAbstract' : 'com.siemens.ra.cg.pl.common.base.cond', 'StringUtils' : 'com.siemens.ra.cg.pl.common.base.util',
  'Link' : 'com.siemens.ra.cg.pl.common.base.model', 'Receiver' : 'com.siemens.ra.cg.pl.common.base.cdi', 'Cache' : 'com.siemens.ra.cg.pl.common.base.cache',
  'DeltaCache' : 'com.siemens.ra.cg.pl.common.base.cache', 'TempIdCache' : 'com.siemens.ra.cg.pl.common.base.cache','LongEntityDeltaCache' : 'com.siemens.ra.cg.pl.common.base.cache',
  'IntegerEntityDeltaCache' : 'com.siemens.ra.cg.pl.common.base.cache', 'StringEntityDeltaCache' : 'com.siemens.ra.cg.pl.common.base.cache',
  'DeltaCacheImpl' : 'com.siemens.ra.cg.pl.common.base.cache', 'CollectionUtils' : 'com.siemens.ra.cg.pl.common.base.util', 'areEquals' : 'static com.siemens.ra.cg.pl.common.base.util.ComparisonUtils.*',
  'lessOrEqual' : 'static com.siemens.ra.cg.pl.common.base.util.ComparisonUtils.*', 'less' : 'static com.siemens.ra.cg.pl.common.base.util.ComparisonUtils.*',
  'greaterOrEqual' : 'static com.siemens.ra.cg.pl.common.base.util.ComparisonUtils.*', 'greater' : 'static com.siemens.ra.cg.pl.common.base.util.ComparisonUtils.*',
  'Manager' : 'com.siemens.ra.cg.pl.common.ejb.core',
  'StringEntityCache' : 'com.siemens.ra.cg.pl.common.base.cache','LongEntityCache' : 'com.siemens.ra.cg.pl.common.base.cache', 'IntegerEntityCache' : 'com.siemens.ra.cg.pl.common.base.cache',
  'LongCacheOverride' : 'com.siemens.ra.cg.pl.common.base.cache', 'IntegerCacheOverride' : 'com.siemens.ra.cg.pl.common.base.cache', 'StringCacheOverride' : 'com.siemens.ra.cg.pl.common.base.cache',
  'CacheOverride' : 'com.siemens.ra.cg.pl.common.base.cache', 'DiffIds' : 'com.siemens.ra.cg.pl.common.base.cache', 'ExceptionUtils' : 'com.siemens.ra.cg.pl.common.base.util',
  'LogStringBuilder' : 'com.siemens.ra.cg.pl.common.base.util', 'AssertionUtils' : 'com.siemens.ra.cg.pl.common.base.assertion', 'LogStringProvider' : 'com.siemens.ra.cg.pl.common.base.util',
  'RootScoped' : 'com.siemens.ra.cg.pl.common.base.cdi.root', 'RootType' : 'com.siemens.ra.cg.pl.common.base.cdi.root.RootScoped', 'View' : 'com.siemens.ra.cg.pl.common.base.annotations',
  'Event' : 'com.siemens.ra.cg.pl.common.base.messaging', 'EventListener' : 'com.siemens.ra.cg.pl.common.base.messaging', 'SingleTypeEventListenerBridgeByJms' : 'com.siemens.ra.cg.pl.common.ejb.messaging',
  'MultiTypeCdiEventListener' : 'com.siemens.ra.cg.pl.common.base.messaging.impl', 'MultiSourceConverter' : 'com.siemens.ra.cg.pl.common.base.converter', 'XmlUtils' : 'com.siemens.ra.cg.pl.common.base.util',
  'Transactional' : 'com.siemens.ra.cg.pl.common.base.annotations', 'Builder' : 'com.siemens.ra.cg.pl.common.base', 'Factory' : 'com.siemens.ra.cg.pl.common.base',
  'AbstractEntityFactory' : 'com.siemens.ra.cg.pl.common.base.model', 'AbstractFactory' : 'com.siemens.ra.cg.pl.common.base', 'EntityManagerFactoryLocator' : 'com.siemens.ra.cg.pl.common.ejb.locator',
  'DbSchemaGenerator' : 'com.siemens.ra.cg.pl.common.ejb.schemagen', 'ReconnectServiceProvider' : 'com.siemens.ra.cg.pl.common.ejb.locator', 'ServiceLocatorFactory' : 'com.siemens.ra.cg.pl.common.ejb.locator',
  'Internal' : 'com.siemens.ra.cg.pl.common.base.cdi', 'TransactionProxyHandler' : ' com.siemens.ra.cg.pl.common.ejb.integ', 'TraceProxyHandler' : ' com.siemens.ra.cg.pl.common.ejb.integ',
  'BaseTestCase' : 'com.siemens.ra.cg.pl.common.base.testcase', 'ManagerMemAbstract' : 'com.siemens.ra.cg.pl.common.ejb.core.mem', 'ManagerAbstract' : 'com.siemens.ra.cg.pl.common.ejb.core',
  'StringLink' : 'com.siemens.ra.cg.pl.common.base.model', 'SupportsEnvironments' : 'com.siemens.ra.cg.pl.common.base.cdi.env', 'Environment' : 'com.siemens.ra.cg.pl.common.base.cdi.env',
  'Traceable' : 'com.siemens.ra.cg.pl.common.base.annotations', 'Service' : 'com.siemens.ra.cg.pl.common.base.annotations',
  'Backend' : 'com.siemens.ra.cg.pl.common.base.annotations', 'SERVER' : 'static com.siemens.ra.cg.pl.common.base.integ.RuntimeType.*', 'CLIENT' : 'static com.siemens.ra.cg.pl.common.base.integ.RuntimeType.*',
  'LOCAL' : 'static com.siemens.ra.cg.pl.common.base.integ.ExecutionType.*', 'MEMORY' : 'static com.siemens.ra.cg.pl.common.base.integ.ExecutionType.*', 'PRODUCTIVE' : 'static com.siemens.ra.cg.pl.common.base.integ.ExecutionType.*',
  'Labeled' : 'com.siemens.ra.cg.pl.common.base.model', 'MlKeyBuilder' : 'com.siemens.ra.cg.pl.common.base.ml', 'MLKey' : 'com.siemens.ra.cg.pl.common.base.ml', 'MLKeyImpl' : 'com.siemens.ra.cg.pl.common.base.ml',
  'Controller' : 'com.siemens.ra.cg.pl.common.base.annotations', 'Config' : 'com.siemens.ra.cg.pl.common.base.config', 'DateTimeProvider' : 'com.siemens.ra.cg.pl.common.base.util'] as TreeMap

extModule(name: 'CgCommonShared', artifact: 'cg-pl-common-shared') {
  nameToNamespace.each { n, ns ->
    extType(name: n, namespace: ns)
  }
}

extModule(name: 'CgCommonEjb', namespace: 'com.siemens.ra.cg.pl.common.ejb', artifact: 'cg-pl-common-ejb') {
  ['UserInRoleConditionVerifier' : 'cond', 'ServiceProviderRemote' : 'locator', 'ServiceLocator' : 'locator'].each { n, ns ->
    extType(name: n, namespace: ns)
  }
}

extModule(name: 'CgCommonProfileShared', artifact: 'cg-pl-common-profile_shared') {
  ['ProfileManager' : 'com.siemens.ra.cg.pl.common.profile.core'].each { n, ns ->
    extType(name: n, namespace: ns)
  }
}

extModule(name: 'CgEnvShared', artifact: 'cg-pl-env-shares') {
  ['ClusterSingleton' : 'com.siemens.ra.cg.pl.env.integ', 'ApplicationMeta' : 'com.siemens.ra.cg.pl.env.integ'].each { n, ns ->
    extType(name: n, namespace: ns)
  }
}

extModule(name: 'CgEnvBridge', artifact: 'cg-pl-env-bridge') {
  ['ModuleInitializerBase' : 'com.siemens.ra.cg.pl.env.bridge.integ', 'ApplicationInitializerBase' : 'com.siemens.ra.cg.pl.env.bridge.integ'].each { n, ns ->
    extType(name: n, namespace: ns)
  }
}

extModule(name: 'CgUifShared', artifact: 'cg-pl-uif-shared') {
  ['Button' : 'com.siemens.ra.cg.pl.uif.widget',
    'DialogViewInterface' : 'com.siemens.ra.cg.pl.uif.widget', 'TextInput' : 'com.siemens.ra.cg.pl.uif.widget', 'Table' : 'com.siemens.ra.cg.pl.uif.widget',
    'ContextMenu' : 'com.siemens.ra.cg.pl.uif.widget', 'ViewInterface' : 'com.siemens.ra.cg.pl.uif.widget', 'Presenter' : 'com.siemens.ra.cg.pl.uif.mvp'].each { n, ns ->
    extType(name: n, namespace: ns)
  }
}

extModule(name: 'CgUifGuido', artifact: 'cg-pl-uif-guido') {
  ['View' : 'com.siemens.ra.cg.pl.uif.guido.widget', 'BaseView' : 'com.siemens.ra.cg.pl.uif.guido.widget',
    'DialogView' : 'com.siemens.ra.cg.pl.uif.guido.widget', 'DialogContentView' : 'com.siemens.ra.cg.pl.uif.guido.widget'].each { n, ns ->
    extType(name: n, namespace: ns)
  }
}