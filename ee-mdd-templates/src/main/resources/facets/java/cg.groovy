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

def nameToNamespace = ['MultiTypeCdiEventListener' : 'com.siemens.ra.cg.pl.common.base.messaging.impl', 'EventImpl' : 'com.siemens.ra.cg.pl.common.base.messaging.impl',
  'ConnectionMetaEvent' : 'com.siemens.ra.cg.pl.common.base.model.event', 'Base' : 'com.siemens.ra.cg.pl.common.base.model', 'BaseEntity' : 'com.siemens.ra.cg.pl.common.base.model', 'IdSetter' : 'com.siemens.ra.cg.pl.common.base.model',
  'BaseEntityImpl' : 'com.siemens.ra.cg.pl.common.ejb.model', 'ControlguideNotFoundException' : 'com.siemens.ra.cg.pl.common.base.exception',
  'IdEntity' : 'com.siemens.ra.cg.pl.common.base.model', 'TimeUtils' : 'com.siemens.ra.cg.pl.common.base.util', 'ActionType' : 'com.siemens.ra.cg.pl.common.base.model',
  'DependsOnExecutionType' : 'com.siemens.ra.cg.pl.common.base.cdi', 'UserInRoleConditionVerifier' : 'com.siemens.ra.cg.pl.common.ejb.cond', 'TimeUtils' : 'com.siemens.ra.cg.pl.common.base.util',
  'IllegalStateException' : 'com.siemens.ra.cg.pl.common.base.exception', 'now' : 'static com.siemens.ra.cg.pl.common.base.util.TimeUtils.*', 'CommonConstants' : 'com.siemens.ra.cg.pl.common.base.integ',
  'ThreadBoundProxyHandler' : 'com.siemens.ra.cg.pl.common.base.integ', 'JmxUtils' : 'com.siemens.ra.cg.pl.common.base.util', 'SingletonContainer' : 'com.siemens.ra.cg.pl.common.base.integ',
  'NamedThreadFactoryHolderByPrefix' : 'com.siemens.ra.cg.pl.common.base.util', 'HolderImpl' : 'com.siemens.ra.cg.pl.common.base', 'PublisherEmpty' : 'com.siemens.ra.cg.pl.common.base.cdi',
  'ConditionVerifierAbstract' : 'com.siemens.ra.cg.pl.common.base.cond', 'StringUtils' : 'com.siemens.ra.cg.pl.common.base.util', 'Link' : 'com.siemens.ra.cg.pl.common.base.model',
  'Receiver' : 'com.siemens.ra.cg.pl.common.base.cdi', 'Cache' : 'com.siemens.ra.cg.pl.common.base.cache', 'DeltaCache' : 'com.siemens.ra.cg.pl.common.base.cache',
  'TempIdCache' : 'com.siemens.ra.cg.pl.common.base.cache', 'LongEntityCache' : 'com.siemens.ra.cg.pl.common.base.cache', 'LongEntityDeltaCache' : 'com.siemens.ra.cg.pl.common.base.cache', 'IntegerEntityDeltaCache' : 'com.siemens.ra.cg.pl.common.base.cache',
  'StringEntityDeltaCache' : 'com.siemens.ra.cg.pl.common.base.cache', 'DeltaCacheImpl' : 'com.siemens.ra.cg.pl.common.base.cache', 'CollectionUtils' : 'com.siemens.ra.cg.pl.common.base.util',
  'Manager' : 'com.siemens.ra.cg.pl.common.ejb.core', 'LongCacheOverride' : 'com.siemens.ra.cg.pl.common.base.cache', 'DiffIds' : 'com.siemens.ra.cg.pl.common.base.cache', 'ExceptionUtils' : 'com.siemens.ra.cg.pl.common.base.util',
  'LogStringBuilder' : 'com.siemens.ra.cg.pl.common.base.util', 'AssertionUtils' : 'com.siemens.ra.cg.pl.common.base.assertion', 'LogStringProvider' : 'com.siemens.ra.cg.pl.common.base.util'] as TreeMap

extModule(name: 'CgCommonShared', artifact: 'cg-pl-common-shared') {
  nameToNamespace.each { n, ns ->
    extType(name: n, namespace: ns)
  }
}