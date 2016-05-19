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
import ee.mdd.model.Element
import ee.mdd.model.component.ExternalType

/**
 *
 * @author Eugen Eisler
 */
class TypeResolver extends AbstractResolveHandler {
    Set<Class> globalTypes = [] as Set
    Map<String, ResolveHandler> handlers = [:]

    ResolveHandler addGlobalResolver(String name, Class type, Set<Class> allowedSourceTypes = null,
                                     Closure converter = null, boolean multi = false, Closure afterSetter = null) {
        addResolver(new GlobalResolveHandler(name: name, type: type, parentTypes: allowedSourceTypes,
                setter: setter(name, converter, multi, afterSetter), globalTypes: globalTypes))
    }

    ResolveHandler addGlobalResolverByParentRoot(String name, Class type, Class parentRootType,
                                                 Closure converter = null, boolean multi = false, Closure afterSetter = null) {
        addResolver(new ParentRootGlobalResolveHandler(name: name, type: type, parentRootType: parentRootType,
                setter: setter(name, converter, multi, afterSetter), globalTypes: globalTypes))
    }

    ResolveHandler addParentResolver(String name, Class type, int depth = 0, Closure converter = null, boolean multi = false, Closure afterSetter = null) {
        addResolver(new ParentResolveHandler(name: name, type: type, depth: depth,
                setter: setter(name, converter, multi, afterSetter)))
    }

    Closure setter(String name, Closure converter, boolean multi, Closure afterSetter) {
        def setter = multi ? { node, resolved ->
            node.add(converter ? converter(resolved) : resolved); if (afterSetter) {
                afterSetter(node, resolved)
            }
        } : { node, resolved ->
            if (Closure.isInstance(node)) {
                node(converter ? converter(resolved) : resolved); if (afterSetter) {
                    afterSetter(node, resolved)
                }
            } else {
                node[name] = converter ? converter(resolved) : resolved; if (afterSetter) {
                    afterSetter(node, resolved)
                }
            }
        }
    }

    ResolveHandler addResolver(ResolveHandler resolver) {
        if (handlers.containsKey(resolver.name)) {
            ListResolveHandler listResolveHandler
            ResolveHandler handler = handlers.get(resolver.name)
            if (!ListResolveHandler.isInstance(handler)) {
                listResolveHandler = new ListResolveHandler(name: resolver.name)
                listResolveHandler.addHandler(handler)
                handlers[resolver.name] = listResolveHandler
            } else {
                listResolveHandler = handler
            }
            listResolveHandler.addHandler(resolver)
        } else {
            handlers[resolver.name] = resolver
        }
    }

    ResolveHandler get(String name) {
        handlers.get(name)
    }

    def attributteDelegate = { FactoryBuilderSupport builder, node, Map attributes ->
        handlers.each { name, ResolveHandler ref ->
            if (attributes.containsKey(name)) {
                def parent = builder.parent
                def refName = attributes[name]
                if (isCollectionOrArray(refName)) {
                    refName.each {
                        ref.addResolveRequest(it, node, parent)
                    }
                } else {
                    ref.addResolveRequest(refName, node, parent)
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
            if (!handler.resolved) {
                handler.printNotResolved()
            }
        }
    }

    def postInstantiateDelegate = { FactoryBuilderSupport builder, Map attributes, Object node ->
    }

    def postNodeCompletionDelegate = { FactoryBuilderSupport builder, Object parent, Object node ->
        if (node instanceof Element) {
            def ref = node.reference
            on(ref, node, parent)
            if (ExternalType.isInstance(node) && node.alias) {
                on node.alias, node, parent
            }
        }
    }

    @Override
    void on(String ref, Base el, Base parent) {
        handlers.each { name, ResolveHandler handler ->
            handler.on(ref, el, parent)
        }
    }

    @Override
    void addResolveRequest(String ref, Base el, Base parent) {
    }

    @Override
    Base resolve(String ref, Base el, Base parent) {
        handlers.values().findResult { it.resolve(ref, el, parent) }
    }

    boolean addGlobalType(Class item) {
        globalTypes.add(item)
    }

    boolean addGlobalTypes(Collection<? extends Class> items) {
        globalTypes.addAll(items)
    }
}
