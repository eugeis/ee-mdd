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

extModule(name: 'CdiApi', namespace: 'javax.enterprise', artifact: 'cdi-api', version: '1.2') {
  ['ApplicationScoped' : 'context', 'Produces' : 'inject','Alternative' : 'inject',
    'Event' : 'event', 'Observes' : 'event', 'Reception' : 'event',
    'AFTER_COMPLETION' : 'static javax.enterprise.event.TransactionPhase.*', 'IF_EXISTS' : 'static javax.enterprise.event.Reception.*',
    'Instance' : 'inject', 'Default' : 'inject'].each { n, ns ->
    extType(name: n, namespace: ns)
  }
}

extModule(name: 'AnnotationApi', namespace: 'javax.annotation', artifact: 'annotation-api', version: '1.2') {
  ['Resource' : 'javax.annotation', 'PostConstruct' : 'javax.annotation'].each { n, ns ->
    extType(name: n, namespace: ns)
  }
}

extModule(name: 'Inject', artifact: 'inject', version: '1') {
  ['Inject' : 'javax.inject', 'Provider' : 'javax.inject', 'Qualifier' : 'javax.inject'].each { n, ns ->
    extType(name: n, namespace: ns)
  }
}




