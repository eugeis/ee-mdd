def nameToNamespace = ['Test': 'org.junit', 'After' : 'org.junit','Before': 'org.junit', 'BeforeClass' : 'org.junit', 'Assert': 'static junit.framework.Assert.*', 'RunWith' : 'org.junit.runner',
  'Mock' : 'org.mockito', 'MockitoJUnitRunner' : 'org.mockito.runners']

extModule(name: 'test') {
  nameToNamespace.each { n, ns ->
    extType(name: n, namespace: ns)
  }
}
