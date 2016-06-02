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

import ee.mdd.model.Base

/**
 *
 * @author Eugen Eisler
 */
class GlobalResolveHandler extends AbstractResolveHandler {
    String name
    Class type
    Set<Class> registerElementsOfParentTypesOnly

    Closure setter
    Set<Class> globalTypes
    Map<String, Object> resolved = new HashMap<>()
    Set<String> duplicates = [] as Set

    Map<String, List<Object>> notResolvedToItems = [:]
    PathResolveHandler pathResolver

    void on(String ref, Base el, Base parent) {
        if (type.isInstance(el)) {
            Class globalType = globalTypes.find { Class clazz -> clazz.isInstance(el) }
            if (globalType) {
                Class parentType = registerElementsOfParentTypesOnly?.find { Class clazz -> clazz.isInstance(parent) }
                if (parentType) {
                    register(ref, el)
                    //resolve not resolved yet
                    if (notResolvedToItems.containsKey(ref)) {
                        notResolvedToItems[ref].each { item ->
                            setter(item, el)
                        }
                        notResolvedToItems.remove(ref)
                    }
                }
            }
        }
    }

    Base resolve(String ref, Base el, Base parent) {
        resolved[ref]
    }

    void addResolveRequest(String ref, Base el, Base parent) {
        if (resolved.containsKey(ref)) {
            setter.call(el, resolved[ref])
        } else {
            if (pathResolver?.isPath(ref, el, parent)) {
                pathResolver.addResolveRequest(ref, el, parent)
            } else {
                addRequest(ref, el)
            }
        }
    }

    private addRequest(String ref, Base el) {
        if (!notResolvedToItems[ref]) {
            notResolvedToItems[ref] = []
        }
        notResolvedToItems[ref] << el
    }

    @Override
    boolean isEmpty() {
        notResolvedToItems.isEmpty()
    }

    @Override
    void printNotResolved() {

        if (!notResolvedToItems.isEmpty()) {
            println "Not resolved: $name: ${notResolvedToItems.keySet()}"
        }
    }

    def register(String ref, Base node) {
        if (!resolved.containsKey(ref)) {
            resolved.put(ref, node)
        } else {
            def old = resolved.get(ref)
            println "Can't register the globale reference '$ref' with '$node of $node.parent', because it is already registered by '$old of $old.parent'."
            duplicates << ref
        }
    }
}
