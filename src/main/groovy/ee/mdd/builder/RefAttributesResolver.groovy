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
package ee.mdd.builder

import ee.mdd.model.Element

/**
 *
 * @author Eugen Eisler
 */
class RefAttributesResolver {
  Set<String> duplicateReferences = [] as Set
  Set<Class> globalTypes = [] as Set
  Map<String, Element> refToResolved = [:]
  Map<String, RefResolveHandler> refHolders = [:]


  RefResolveHandler addGlobalResolver(String name, Class type) {
    refHolders[name] = new RefGlobalResolveHandler(refToResolved: refToResolved, name: name, type: type)
  }

  RefResolveHandler addParentResolver(String name, Class type) {
    refHolders[name] = new RefParentResolveHandler(name: name, type: type)
  }

  RefResolveHandler get(String name) {
    refHolders.get(name)
  }

  boolean addGlobalType(Class item) {
    globalTypes.add(item)
  }

  boolean addGlobalTypes(Collection<? extends Class> items) {
    globalTypes.addAll(items)
  }

  def attributteDelegate = { FactoryBuilderSupport builder, node, Map attributes ->
    refHolders.each { name, RefResolveHandler ref ->
      if (attributes.containsKey(name)) {
        String refName = attributes[name]
        def parent = builder.parent
        ref.addResolveRequest(refName, parent, { node[name] = it })
      }
    }

    refHolders.each { name, RefResolveHandler ref ->
      attributes.remove(name)
    }
  }

  def postInstantiateDelegate = { FactoryBuilderSupport builder, Map attributes, Object node ->
  }

  def postNodeCompletionDelegate = { FactoryBuilderSupport builder, Object parent, Object node ->
    if(node instanceof Element) {

      def ref = node.reference
      Class globalType = globalTypes.find { Class clazz -> clazz.isInstance(node) }
      if(globalType && !duplicateReferences.contains(ref)) {
        def old = refToResolved.put(ref, node)
        if(old) {
          refToResolved.remove(ref)
          println "Duplicate global reference '$ref' first='$old' and second='$node', remove and ignore this reference."
          duplicateReferences << ref
        }
      }

      refHolders.each { name, RefResolveHandler handler ->
        handler.onElement(node)
      }
    }

    if(parent == null) {
      refHolders.each { name, RefResolveHandler handler ->
        if(!handler.resolved) {
          handler.printNotResolved()
        }
      }
    }
  }
}
