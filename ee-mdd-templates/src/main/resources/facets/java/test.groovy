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

def nameToNamespace = ['Test': 'org.junit', 'After' : 'org.junit','Before': 'org.junit', 'BeforeClass' : 'org.junit', 'Assert': 'static junit.framework.Assert.*', 'RunWith' : 'org.junit.runner'
  ] as TreeMap

extModule(name: 'JUnit', namespace: 'org.junit', artifact: 'junit', version: '4.12') {
  nameToNamespace.each { n, ns ->
    extType(name: n, namespace: ns)
  }
}

extModule(name: 'MockitoCore', namespace: 'org.mockito', artifact: 'mockito-core', version: '1.9.5') {
  ['Mock' : 'org.mockito', 'MockitoJUnitRunner' : 'org.mockito.runners'].each { n, ns ->
    extType(name: n, namespace: ns)
  }
}
