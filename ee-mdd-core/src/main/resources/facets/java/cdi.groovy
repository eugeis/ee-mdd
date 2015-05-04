def nameToNamespace = ['ApplicationScoped' : 'javax.enterprise.context', 'Produces' : 'javax.enterprise.inject.Produces',
  'Alternative' : 'javax.enterprise.inject', 'Inject' : 'javax.inject',
  'Observes' : 'javax.enterprise.event', 'Reception' : 'javax.enterprise.event', 'Resource' : 'javax.annotation', 'PostConstruct' : 'javax.annotation']

extModule(name: 'Cdi') {
  nameToNamespace.each { n, ns ->
    extType(name: n, namespace: ns)
  }
}
