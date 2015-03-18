/*
 * Controlguide
 * Copyright (c) Siemens AG 2015, All Rights Reserved, Confidential
 */
package ee.mdd.model.component.java

import ee.mdd.model.component.Facet

class Cg extends Facet {

  Closure childBuilder() {

    def nameToNamespace = ['Service' : 'com.siemens.ra.cg.pl.common.base.annotations',
      'Environment' : 'com.siemens.ra.cg.pl.common.base.cdi.env', 'SupportsEnvironments' : 'com.siemens.ra.cg.pl.common.base.cdi.env','LinkedObjectCache' : 'com.siemens.ra.cg.pl.common.base.cache',
      'Traceable' : 'com.siemens.ra.cg.pl.common.base.annotations', 'JmsDestinationConfig' : 'com.siemens.ra.cg.pl.common.ejb.messaging',
      'EventListener' : 'com.siemens.ra.cg.pl.common.base.messaging', 'JmsToEventListener' : 'com.siemens.ra.cg.pl.common.ejb.messaging.jse',
      'SingleTypeEventListenerBridgeByJms' : 'com.siemens.ra.cg.pl.common.ejb.messaging','ServiceLocator' : 'com.siemens.ra.cg.pl.common.ejb.locator']

    return {
      extModule(name: 'CgCommonShared', artifact: 'cg-pl-common-shared') {
        nameToNamespace.each { n, ns ->
          extType(name: n, namespace: ns)
        }
      }
    }
  }
}
