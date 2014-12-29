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

	RefResolveHandler addGlobalResolver(String name, Class type, Closure converter = null, boolean multi = false, Closure afterSetter = null) {
		addResolver(new RefGlobalResolveHandler(refToResolved: refToResolved, name: name, type: type,
		setter: setter(name, converter, multi, afterSetter)))
	}

	RefResolveHandler addParentResolver(String name, Class type, int depth = 0, Closure converter = null, boolean multi = false, Closure afterSetter = null) {
		addResolver(new RefParentResolveHandler(name: name, type: type, depth: depth,
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

	RefResolveHandler addResolver(RefResolveHandler resolver) {
		refHolders[resolver.name] = resolver
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

		refHolders.each { name, RefResolveHandler ref ->
			attributes.remove(name)
		}
	}

	boolean isCollectionOrArray(object) {
		[Collection, Object[]].any { it.isAssignableFrom(object.getClass()) }
	}

	void printNotResolved() {
		refHolders.each { name, RefResolveHandler handler ->
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
	}
}
