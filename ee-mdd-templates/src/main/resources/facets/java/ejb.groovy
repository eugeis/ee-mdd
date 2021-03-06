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

/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */

def nameToNamespace = ['Stateless' : 'javax.ejb', 'Remote' : 'javax.ejb', 'TransactionAttribute' : 'javax.ejb', 'Local' : 'javax.ejb', 'Timeout' : 'javax.ejb',
  'Timer' : 'javax.ejb', 'TimerConfig' : 'javax.ejb', 'TimerService' : 'javax.ejb', 'ActivationConfigProperty' : 'javax.ejb', 'MessageDriven' : 'javax.ejb',
  'Singleton' : 'javax.ejb', 'Startup' : 'javax.ejb', 'TransactionManagement' : 'javax.ejb', 'TransactionManagementType' : 'javax.ejb',
  'Schedule' : 'javax.ejb'] as TreeMap

extModule(name: 'EjbApi', artifact: 'jboss-ejb-api_3.2_spec', version: '1.0.0.Final') {
  nameToNamespace.each { n, ns ->
    extType(name: n, namespace: ns)
  }
}