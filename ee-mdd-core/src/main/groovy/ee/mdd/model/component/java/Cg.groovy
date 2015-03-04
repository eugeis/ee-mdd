/*
 * Controlguide
 * Copyright (c) Siemens AG 2015, All Rights Reserved, Confidential
 */
package ee.mdd.model.component.java

import ee.mdd.model.component.Facet

class Cg extends Facet {
  protected Map nameToNamespace() {
    ['Service' : 'com.siemens.ra.cg.pl.common.base.annotations', 'Environment' : 'com.siemens.ra.cg.pl.common.base.cdi.env', 'SupportsEnvironment' : 'com.siemens.ra.cg.pl.common.base.cdi.env',
      'LinkedObjectCache' : 'com.siemens.ra.cg.pl.common.base.cache']
  }
}
