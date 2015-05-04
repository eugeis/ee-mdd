def nameToNamespace = ['MessageDriven' : 'javax.ejb', 'ActivationConfigProperty' : 'javax.ejb',
  'ConnectionFactory' : 'javax.jms', 'MessageListener' :'javax.jms', 'Destination' : 'javax.jms', 'Topic' : 'javax.jms']

extModule(name: 'Jms') {
  nameToNamespace.each { n, ns ->
    extType(name: n, namespace: ns)
  }
}
