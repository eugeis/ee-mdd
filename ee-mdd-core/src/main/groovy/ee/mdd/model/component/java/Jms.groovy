/*
 * Controlguide
 * Copyright (c) Siemens AG 2015, All Rights Reserved, Confidential
 */
package ee.mdd.model.component.java

import ee.mdd.model.component.Facet

class Jms extends Facet {

  Closure childBuilder() {

    def nameToNamespace = ['ConnectionFactory' : 'javax.jms', 'MessageListener' :'javax.jms', 'Destination' : 'javax.jms', 'Topic' : 'javax.jms']

    return {
      extModule(name: 'Jms') {
        nameToNamespace.each { n, ns ->
          extType(name: n, namespace: ns)
        }
      }
    }
  }
}
