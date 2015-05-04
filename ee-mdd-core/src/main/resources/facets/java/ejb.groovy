def nameToNamespace = ['Stateless' : 'javax.ejb', 'Remote' : 'javax.ejb', 'TransactionAttribute' : 'javax.ejb']

extModule(name: 'Ejb') {
  nameToNamespace.each { n, ns ->
    extType(name: n, namespace: ns)
  }
}