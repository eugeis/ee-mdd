package ee.mdd.model.component.java

import ee.mdd.model.component.Facet


class Cdi extends Facet {

  Closure childBuilder() {

    def nameToNamespace = ['Produces' : 'javax.enterprise.inject.Produces', 'Alternative' : 'javax.enterprise.inject', 'Inject' : 'javax.inject',
      'Observes' : 'javax.enterprise.event', 'Reception' : 'javax.enterprise.event']

    return {
      extModule(name: 'Cdi') {
        nameToNamespace.each { n, ns ->
          extType(name: n, namespace: ns)
        }
      }
    }
  }
}
