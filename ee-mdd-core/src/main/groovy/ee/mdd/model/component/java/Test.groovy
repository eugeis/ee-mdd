package ee.mdd.model.component.java

import groovy.lang.Closure;

import ee.mdd.model.component.Facet

class Test extends Facet {

  Closure childBuilder() {

    def nameToNamespace = ['Test': 'org.junit', 'Before': 'org.junit', 'Assert': 'static junit.framework.Assert.*']

    return {
      extModule(name: 'JUnit') {
        nameToNamespace.each { n, ns ->
          extType(name: n, namespace: ns)
        }
      }
    }
  }
}
