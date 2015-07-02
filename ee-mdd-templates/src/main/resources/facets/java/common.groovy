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
].sort()

def nameToNamespace = ['Date': 'java.util', 'List' : 'java.util', 'Map' : 'java.util', 'Set' : 'java.util',
  'ArrayList' : 'java.util', 'HashMap' : 'java.util' , 'Serializable': 'java.io', 'HashSet' : 'java.util', 'Collections' : 'java.util'] as TreeMap

extModule(name: 'Java') {

  primitiveTypes.each { n ->
    extType(name: n)
  }

  nameToNamespace.each { n, ns ->
    extType(name: n, namespace: ns)
  }
}

modelExtender = { model ->
  model.components.each { component ->
    if(component.module) {
      component.module.extend { config('Ml') }
    }
  }
}