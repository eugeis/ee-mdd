/*
 * Controlguide
 * Copyright (c) Siemens AG 2015, All Rights Reserved, Confidential
 */
package ee.mdd.model.component.java

import ee.mdd.model.component.Facet

class Cg extends Facet {

  Closure childBuilder() {

    def nameToNamespace = ['Backend' : 'com.siemens.ra.cg.pl.common.base.annotations', 'Service' : 'com.siemens.ra.cg.pl.common.base.annotations',
      'Environment' : 'com.siemens.ra.cg.pl.common.base.cdi.env', 'SupportsEnvironments' : 'com.siemens.ra.cg.pl.common.base.cdi.env','LinkedObjectCache' : 'com.siemens.ra.cg.pl.common.base.cache',
      'JmsDestinationConfig' : 'com.siemens.ra.cg.pl.common.ejb.messaging', 'JmsSender' : 'com.siemens.ra.cg.pl.common.ejb.messaging',
      'Event' : 'com.siemens.ra.cg.pl.common.base.messaging', 'EventImpl' : 'com.siemens.ra.cg.pl.common.base.messaging.impl',
      'EventListener' : 'com.siemens.ra.cg.pl.common.base.messaging', 'JmsToEventListener' : 'com.siemens.ra.cg.pl.common.ejb.messaging.jse',
      'SingleTypeEventListenerBridgeByJms' : 'com.siemens.ra.cg.pl.common.ejb.messaging','ServiceLocator' : 'com.siemens.ra.cg.pl.common.ejb.locator',
      'LifecycleEvent' : 'import com.siemens.ra.cg.pl.common.app.event', 'Traceable' : 'com.siemens.ra.cg.pl.common.base.annotations',
      'CollectionUtils' : 'com.siemens.ra.cg.pl.common.base.util', 'Factory' : 'com.siemens.ra.cg.pl.common.base', 'MultiTypeCdiEventListener' : 'com.siemens.ra.cg.pl.common.base.messaging.impl',
      'ConnectionMetaEvent' : 'com.siemens.ra.cg.pl.common.base.model.event', 'Backend':'com.siemens.ra.cg.pl.common.base.annotations', 'External' : 'com.siemens.ra.cg.pl.common.base.annotations',
      'BaseTestCase' : 'com.siemens.ra.cg.pl.common.base.testcase', 'JmsMessagingAdapterTestCase' : 'com.siemens.ra.cg.pl.common.ejb.messaging.jse', 'JmsSendExecutor' : 'com.siemens.ra.cg.pl.common.ejb.messaging',
      'BaseEntity' : 'com.siemens.ra.cg.pl.common.base.model', 'IdSetter' : 'com.siemens.ra.cg.pl.common.base.model', 'BaseEntityImpl' : 'com.siemens.ra.cg.pl.common.ejb.model',
      'LogStringProvider' : 'com.siemens.ra.cg.pl.common.base.util', 'LogStringBuilder' : 'com.siemens.ra.cg.pl.common.base.util', 'AssertionUtils' : 'com.siemens.ra.cg.pl.common.base.assertion']

    return {
      extModule(name: 'CgCommonShared', artifact: 'cg-pl-common-shared') {
        nameToNamespace.each { n, ns ->
          extType(name: n, namespace: ns)
        }
      }
    }
  }
}
