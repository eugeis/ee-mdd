/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ee.mdd.model.component.java

import ee.mdd.model.component.Facet

/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */
class Jpa extends Facet {

  Closure childBuilder() {

    def nameToNamespace = ['ApplicationScoped' : 'javax.enterprise.context', 'NamedQuery' : 'javax.persistence',
      'NamedQueries' : 'javax.persistence', 'Entity' : 'javax.persistence',
      'Table' : 'javax.persistence', 'Index' : 'javax.persistence', 'Column' : 'javax.persistence',
      'Id' : 'javax.persistence', 'OneToOne' : 'javax.persistence', 'OneToMany' : 'javax.persistence',
      'ManyToOne' : 'javax.persistence', 'ManyToMany' : 'javax.persistence', 'JoinColumn' : 'javax.persistence',
      'JoinTable' : 'javax.persistence', 'Temporal' : 'javax.persistence', 'Enumerated' : 'javax.persistence',
      'Embedded' : 'javax.persistence', 'Lob' : 'javax.persistence', 'ElementCollection' : 'javax.persistence',
      'CollectionTable' : 'javax.persistence', 'CascadeType' : 'javax.persistence', 'FetchType' : 'javax.persistence',
      'TemporalType' : 'javax.persistence', 'GenerationType' : 'javax.persistence', 'TableGenerator' : 'javax.persistence',
      'GeneratedValue' : 'javax.persistence', 'MappedSuperclass' : 'javax.persistence', 'Transient' : 'javax.persistence',
      'Transactional' : 'javax.transaction', 'Stateless' : 'javax.ejb', 'Remote' : 'javax.ejb', 'TransactionAttribute' : 'javax.ejb',
      'MessageDriven' : 'javax.ejb', 'ActivationConfigProperty' : 'javax.ejb']

    return {
      extModule(name: 'Jpa') {
        nameToNamespace.each { n, ns ->
          extType(name: n, namespace: ns)
        }
      }
    }
  }
}
