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
  'byte',
  'short',
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
  'byte[]'
].sort()

def nameToNamespace = ['Comparator' : 'java.util', 'Date': 'java.util', 'List' : 'java.util', 'List<String>' : 'java.util', 'Map' : 'java.util', 'Set' : 'java.util', 'ArrayList' : 'java.util',
  'HashMap' : 'java.util' , 'LinkedHashMap' : 'java.util', 'ConcurrentHashMap' : 'java.util.concurrent', 'Serializable': 'java.io', 'HashSet' : 'java.util', 'Collection' : 'java.util', 'Collections' : 'java.util',
  'RunAs' : 'javax.annotation.security', 'PreDestroy' : 'javax.annotation', 'Executors' : 'java.util.concurrent',
  'ScheduledExecutorService' : 'java.util.concurrent' , 'ScheduledFuture' : 'java.util.concurrent', 'TimeUnit' : 'java.util.concurrent', 'ElementType' : 'java.lang.annotation',
  'Retention' : 'java.lang.annotation', 'RetentionPolicy' : 'java.lang.annotation', 'Target' : 'java.lang.annotation', 'Method' : 'java.lang.reflect',
  'Proxy' : 'java.lang.reflect', 'Arrays' : 'java.util', 'AtomicLong' : 'java.util.concurrent.atomic', 'AtominInteger' : 'java.util.concurrent.atomic'] as TreeMap

extModule(name: 'Java') {

  primitiveTypes.each { n ->
    extType(name: n)
  }

  nameToNamespace.each { n, ns ->
    extType(name: n, namespace: ns)
  }
}

extModule(name: 'Slf4j', artifact: 'slf4j-ext', version: '1.7.12') {
  
    ['XLogger' : 'org.slf4j.ext', 'XLoggerFactory' : 'org.slf4j.ext'].each { n, ns ->
      extType(name: n, namespace: ns)
    }
}

extModule(name: 'MockitoCore', artifact: 'mockito-core', version: '1.9.5') {
  
    ['mock' : 'static org.mockito.Mockito.*'].each { n, ns ->
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