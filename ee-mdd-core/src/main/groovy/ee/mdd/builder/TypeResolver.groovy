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
class TypeResolver {
  Set<String> duplicates = [] as Set
  Set<Class> types = [] as Set
  Map<String, Element> refToElement = [:]
  Map<String, ResolveHandler> handlers = [:]

  ResolveHandler addGlobalResolver(String name, Class type, Closure converter = null, boolean multi = false, Closure afterSetter = null) {
    addResolver(new GlobalResolveHandler(resolved: refToElement, name: name, type: type,
    setter: setter(name, converter, multi, afterSetter)))
  }

  ResolveHandler addParentResolver(String name, Class type, int depth = 0, Closure converter = null, boolean multi = false, Closure afterSetter = null) {
    addResolver(new ParentResolveHandler(name: name, type: type, depth: depth,
    setter: setter(name, converter, multi, afterSetter)))
  }

  Closure setter(String name, Closure converter, boolean multi, Closure afterSetter) {
    def setter = multi ? { node, resolved ->
      node.add(converter ? converter(resolved) : resolved); if(afterSetter) {
        afterSetter(node, resolved)
      }
    } : { node, resolved ->
      node[name] = converter ? converter(resolved) : resolved; if(afterSetter) {
        afterSetter(node, resolved)
      }
    }
  }

  ResolveHandler addResolver(ResolveHandler resolver) {
    handlers[resolver.name] = resolver
  }

  ResolveHandler get(String name) {
    handlers.get(name)
  }

  boolean addGlobalType(Class item) {
    types.add(item)
  }

  boolean addGlobalTypes(Collection<? extends Class> items) {
    types.addAll(items)
  }

  def attributteDelegate = { FactoryBuilderSupport builder, node, Map attributes ->
    handlers.each { name, ResolveHandler ref ->
      if (attributes.containsKey(name)) {
        def parent = builder.parent
        def refName = attributes[name]
        if (isCollectionOrArray(refName)) {
          refName.each {
            ref.addResolveRequest(it, parent, node)
          }
        } else {
          ref.addResolveRequest(refName, parent, node)
        }
      }
    }

    handlers.each { name, ResolveHandler ref ->
      attributes.remove(name)
    }
  }

  boolean isCollectionOrArray(object) {
    [Collection, Object[]].any { it.isAssignableFrom(object.getClass()) }
  }

  void printNotResolved() {
    handlers.each { name, ResolveHandler handler ->
      if(!handler.resolved) {
        handler.printNotResolved()
      }
    }
  }

  def postInstantiateDelegate = { FactoryBuilderSupport builder, Map attributes, Object node ->
  }

  def postNodeCompletionDelegate = { FactoryBuilderSupport builder, Object parent, Object node ->
    if (node instanceof Element) {

      def ref = node.reference
      Class globalType = types.find { Class clazz -> clazz.isInstance(node) }
      if(globalType && !duplicates.contains(ref)) {
        def old = refToElement.put(ref, node)
        if(old) {
          println "Duplicate global reference '$ref' first='$old' and second='$node'."
          duplicates << ref
        }
      }

      handlers.each { name, ResolveHandler handler ->
        handler.onElement(node)
      }
    }
  }
}
