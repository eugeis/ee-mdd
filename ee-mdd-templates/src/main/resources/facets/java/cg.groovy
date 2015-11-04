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
  'ConnectionMetaEvent' : 'com.siemens.ra.cg.pl.common.base.model.event', 'BaseEntity' : 'com.siemens.ra.cg.pl.common.base.model', 'IdSetter' : 'com.siemens.ra.cg.pl.common.base.model',
  'BaseEntityImpl' : 'com.siemens.ra.cg.pl.common.ejb.model', 'ControlguideNotFoundException' : 'com.siemens.ra.cg.pl.common.base.exception',
  'IdEntity' : 'com.siemens.ra.cg.pl.common.base.model', 'TimeUtils' : 'com.siemens.ra.cg.pl.common.base.util', 'ActionType' : 'com.siemens.ra.cg.pl.common.base.model',
  'DependsOnExecutionType' : 'com.siemens.ra.cg.pl.common.base.cdi', 'UserInRoleConditionVerifier' : 'com.siemens.ra.cg.pl.common.ejb.cond', 'TimeUtils' : 'com.siemens.ra.cg.pl.common.base.util',
  'IllegalStateException' : 'com.siemens.ra.cg.pl.common.base.exception', 'now' : 'static com.siemens.ra.cg.pl.common.base.util.TimeUtils.*', 'CommonConstants' : 'com.siemens.ra.cg.pl.common.base.integ',
  'ThreadBoundProxyHandler' : 'com.siemens.ra.cg.pl.common.base.integ', 'JmxUtils' : 'com.siemens.ra.cg.pl.common.base.util', 'SingletonContainer' : 'com.siemens.ra.cg.pl.common.base.integ',
  'NamedThreadFactoryHolderByPrefix' : 'com.siemens.ra.cg.pl.common.base.util'] as TreeMap

extModule(name: 'CgCommonShared', artifact: 'cg-pl-common-shared') {
  nameToNamespace.each { n, ns ->
    extType(name: n, namespace: ns)
  }
}