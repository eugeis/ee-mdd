def primitiveTypes = [
  'int',
  'long',
  'float',
  'double',
  'boolean',
  'Integer',
  'Long',
  'Float',
  'Double',
  'Boolean',
  'String'
]

def nameToNamespace = ['Date': 'java.util', 'List' : 'java.util', 'Map' : 'java.util', 'Set' : 'java.util',
  'ArrayList' : 'java.util', 'HashMap' : 'java.util' , 'Serializable': 'java.io']

extModule(name: 'Java') {

  primitiveTypes.each { n ->
    extType(name: n)
  }

  nameToNamespace.each { n, ns ->
    extType(name: n, namespace: ns)
  }
}
