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
  Map<String, RefHolder> refHolders = [:]

  RefHolder add(String name, Class type, boolean global = true) {
    refHolders[name] = new RefHolder(name: name, type: type, global: global)
  }

  RefHolder get(String name) {
    refHolders.get(name)
  }

  def attributteDelegate = { FactoryBuilderSupport builder, node, Map attributes ->
    refHolders.each { name, RefHolder ref ->
      if (attributes.containsKey(name)) {
        String refName = attributes[name]
        if(ref.global) {
          ref.resolveOrStore(refName, { node[name] = it })
        } else {
          //try to resolve by parents
          def parent = builder.parent?.parent
          def resolved = parent?.find { Element el -> ref.type.isInstance(el) && el.name == refName }
          assert resolved, "The '$refName' can not be resolved in $node.parent for $node"

          node[name] = resolved
        }
      }
    }
    refHolders.each { name, RefHolder ref ->
      attributes.remove(name)
    }
  }

  def postInstantiateDelegate = { FactoryBuilderSupport builder, Map attributes, Object node ->
    if(node instanceof Element) {
      refHolders.findAll { name, RefHolder ref -> ref.global && ref.type.isInstance(node) }.each { name, RefHolder ref -> ref.addResolved(node) }
    }
  }
}
