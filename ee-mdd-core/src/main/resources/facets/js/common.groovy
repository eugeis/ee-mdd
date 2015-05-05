
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
  'String',
  'Date'
]

extModule(name: 'JavaScript') {

  primitiveTypes.each { n ->
    extType(name: n)
  }
}