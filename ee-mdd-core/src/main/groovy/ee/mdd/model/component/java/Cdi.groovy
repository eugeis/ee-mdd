package ee.mdd.model.component.java

import ee.mdd.model.component.Facet
import groovy.lang.Closure;


class Cdi extends Facet {

  Closure childBuilder() {

    def nameToNamespace = ['Produces' : 'javax.enterprise.inject.Produces', 'Alternative' : 'javax.enterprise.inject']

    return {
      extModule(name: 'Cdi') {
        nameToNamespace.each { n, ns ->
          extType(name: n, namespace: ns)
        }
      }
    }
  }
}
