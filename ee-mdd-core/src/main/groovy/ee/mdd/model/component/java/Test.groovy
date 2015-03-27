package ee.mdd.model.component.java

import ee.mdd.model.component.Facet

class Test extends Facet {

  Closure childBuilder() {

    def nameToNamespace = ['Test': 'org.junit', 'Before': 'org.junit', 'Assert': 'static junit.framework.Assert.*', 'RunWith' : 'org.junit.runner',
      'Mock' : 'org.mockito', 'MockitoJUnitRunner' : 'org.mockito.runners']

    return {
      extModule(name: 'JUnit') {
        nameToNamespace.each { n, ns ->
          extType(name: n, namespace: ns)
        }
      }
    }
  }
}
