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
    final static SEPARATOR = '.'
    String name
    Class type
    Set<Class> parentTypes

    Closure setter
    Set<Class> globalTypes
    Map<String, Object> resolved = new HashMap<>()
    Set<String> duplicates = [] as Set

    Map<String, List<Object>> notResolvedToItems = [:]
    Map<String, List<Object>> notResolvedPathToItems = [:]
    Map<String, List<Object>> notResolvedPathRefToResolvers = [:]

    void on(String ref, Base el, Base parent) {
        if (type.isInstance(el)) {
            Class globalType = globalTypes.find { Class clazz -> clazz.isInstance(el) }
            if (!globalTypes || globalType) {
                Class parentType = parentTypes?.find { Class clazz -> clazz.isInstance(parent) }
                if (!parentTypes || parentType) {
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
            if (ref.contains(SEPARATOR)) {
                addResolvePathRequest(ref, el)
            } else {
                addRequest(ref, el)
            }
        }
    }

    private addResolvePathRequest(String ref, Base el) {
        def parts = ref.split("\\$SEPARATOR")
        String refPart = parts[0]
        if (resolved.containsKey(refPart)) {
            resolveParts(resolved[refPart], parts[1..parts.length - 1], el)
        } else {
            def subParts = parts[1..parts.length - 1]
            notResolvedToItems[refPart] = { resolveParts(it, subParts, el) }
        }
    }

    private void resolveParts(Base base, List<String> parts, Base item) {
        def el = base
        def partsSize = parts.size()
        for (int i = 0; i < partsSize; i++) {
            String refPart = parts[i]
            def resolved = el.resolve(refPart)
            if (resolved) {
                el = resolved
            } else {
                def subParts = (i < partsSize - 1) ? parts[i + 1..partsSize - 1] : []
                notResolvedPathRefToResolvers[refPart] = { resolveParts(el.resolve(it), subParts, item) }
                //println "Can not resolve ${refPart} in '$el.reference'"
                el = null
                break
            }
        }

        if (el) {
            setter(item, el)
        }
    }

    private addRequest(String ref, Base el) {
        if (!notResolvedToItems[ref]) {
            notResolvedToItems[ref] = []
        }
        notResolvedToItems[ref] << el
    }

    @Override
    boolean isResolved() {
        notResolvedToItems.isEmpty() && notResolvedPathToItems.isEmpty()
    }

    @Override
    void printNotResolved() {

        if (!notResolvedToItems.isEmpty()) {
            println "Not resolved: $name: ${notResolvedToItems.keySet()}"
        }

        if (!notResolvedPathToItems.isEmpty()) {
            println "Not resolved: $name: ${notResolvedPathToItems.keySet()}"
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
