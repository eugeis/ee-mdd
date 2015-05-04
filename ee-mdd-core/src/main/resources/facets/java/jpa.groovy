def nameToNamespace = ['NamedQuery' : 'javax.persistence',
  'NamedQueries' : 'javax.persistence', 'Entity' : 'javax.persistence',
  'Table' : 'javax.persistence', 'Index' : 'javax.persistence', 'Column' : 'javax.persistence',
  'Id' : 'javax.persistence', 'OneToOne' : 'javax.persistence', 'OneToMany' : 'javax.persistence',
  'ManyToOne' : 'javax.persistence', 'ManyToMany' : 'javax.persistence', 'JoinColumn' : 'javax.persistence',
  'JoinTable' : 'javax.persistence', 'Temporal' : 'javax.persistence', 'Enumerated' : 'javax.persistence',
  'Embedded' : 'javax.persistence', 'Embeddable' : 'javax.persistence', 'Lob' : 'javax.persistence', 'ElementCollection' : 'javax.persistence',
  'CollectionTable' : 'javax.persistence', 'CascadeType' : 'javax.persistence', 'FetchType' : 'javax.persistence',
  'TemporalType' : 'javax.persistence', 'GenerationType' : 'javax.persistence', 'TableGenerator' : 'javax.persistence',
  'GeneratedValue' : 'javax.persistence', 'MappedSuperclass' : 'javax.persistence', 'Transient' : 'javax.persistence',
  'Transactional' : 'javax.transaction', 'Stateless' : 'javax.ejb', 'Remote' : 'javax.ejb', 'TransactionAttribute' : 'javax.ejb',
  'Resource' : 'javax.annotation']

extModule(name: 'Jpa') {
  nameToNamespace.each { n, ns ->
    extType(name: n, namespace: ns)
  }
}