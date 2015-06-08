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

def nameToNamespace = ['Backend' : 'com.siemens.ra.cg.pl.common.base.annotations', 'Service' : 'com.siemens.ra.cg.pl.common.base.annotations',
  'Environment' : 'com.siemens.ra.cg.pl.common.base.cdi.env', 'SupportsEnvironments' : 'com.siemens.ra.cg.pl.common.base.cdi.env','LinkedObjectCache' : 'com.siemens.ra.cg.pl.common.base.cache',
  'JmsDestinationConfig' : 'com.siemens.ra.cg.pl.common.ejb.messaging', 'JmsSender' : 'com.siemens.ra.cg.pl.common.ejb.messaging',
  'Event' : 'com.siemens.ra.cg.pl.common.base.messaging', 'EventImpl' : 'com.siemens.ra.cg.pl.common.base.messaging.impl',
  'EventListener' : 'com.siemens.ra.cg.pl.common.base.messaging', 'JmsToEventListener' : 'com.siemens.ra.cg.pl.common.ejb.messaging.jse',
  'SingleTypeEventListenerBridgeByJms' : 'com.siemens.ra.cg.pl.common.ejb.messaging','ServiceLocator' : 'com.siemens.ra.cg.pl.common.ejb.locator',
  'LifecycleEvent' : 'com.siemens.ra.cg.pl.common.app.event', 'Traceable' : 'com.siemens.ra.cg.pl.common.base.annotations',
  'CollectionUtils' : 'com.siemens.ra.cg.pl.common.base.util', 'Factory' : 'com.siemens.ra.cg.pl.common.base', 'MultiTypeCdiEventListener' : 'com.siemens.ra.cg.pl.common.base.messaging.impl',
  'ConnectionMetaEvent' : 'com.siemens.ra.cg.pl.common.base.model.event', 'Backend':'com.siemens.ra.cg.pl.common.base.annotations', 'External' : 'com.siemens.ra.cg.pl.common.base.annotations',
  'BaseTestCase' : 'com.siemens.ra.cg.pl.common.base.testcase', 'JmsMessagingAdapterTestCase' : 'com.siemens.ra.cg.pl.common.ejb.messaging.jse', 'JmsSendExecutor' : 'com.siemens.ra.cg.pl.common.ejb.messaging',
  'BaseEntity' : 'com.siemens.ra.cg.pl.common.base.model', 'IdSetter' : 'com.siemens.ra.cg.pl.common.base.model', 'BaseEntityImpl' : 'com.siemens.ra.cg.pl.common.ejb.model',
  'LogStringProvider' : 'com.siemens.ra.cg.pl.common.base.util', 'LogStringBuilder' : 'com.siemens.ra.cg.pl.common.base.util', 'AssertionUtils' : 'com.siemens.ra.cg.pl.common.base.assertion',
  'MLKey' : 'com.siemens.ra.cg.pl.common.base.ml', 'MLKeyBuilder' : 'com.siemens.ra.cg.pl.common.base.ml' , 'MLKeyImpl' : 'com.siemens.ra.cg.pl.common.base.ml',
  'ControlguideNotFoundException' : 'com.siemens.ra.cg.pl.common.base.exception', 'ComparisonUtils' : 'com.siemens.ra.cg.pl.common.base.util',
  'Manager' : 'com.siemens.ra.cg.pl.common.ejb.core', 'IdEntity' : 'com.siemens.ra.cg.pl.common.base.model', 'StringUtils' : 'com.siemens.ra.cg.pl.common.base.util'] as TreeMap

extModule(name: 'CgCommonShared', artifact: 'cg-pl-common-shared') {
  nameToNamespace.each { n, ns ->
    extType(name: n, namespace: ns)
  }
}