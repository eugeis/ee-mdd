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

import ee.mdd.model.Composite
import ee.mdd.model.Element


/**
 *
 * @author Eugen Eisler
 */
class RefGlobalResolveHandler implements RefResolveHandler {
	final static SEPARATOR = '.'
	String name
	Class type
	Closure setter

	Map<String, Object> refToResolved
	Map<String, List<Object>> notResolvedRefToItems = [:]
	Map<String, List<Object>> notResolvedPathRefToItems = [:]

	void onElement(Element el) {
		if (type.isInstance(el)) {
			def ref = el.reference
			//resolve not resolved yet
			if(notResolvedRefToItems.containsKey(ref)) {
				resolve(ref, el)
			}
		}
	}

	private resolve(String ref, Element el) {
		notResolvedRefToItems[ref].each {  item ->
			setter(item, el)
		}
		notResolvedRefToItems.remove(ref)
	}

	void addResolveRequest(String ref, Composite parent, item) {
		if (refToResolved.containsKey(ref)) {
			setter.call(item, refToResolved[ref])
		} else {
			if(ref.contains(SEPARATOR)) {
				addResolvePathRequest(ref, parent, item)
			} else {
				addRequest(ref, item)
			}
		}
	}

	private addResolvePathRequest(String ref, Composite parent, item) {
		def parts = ref.split("\\$SEPARATOR")
		String refPart = parts[0]
		if(refToResolved.containsKey(refPart)) {
			resolve(refToResolved[refPart], parts[1..parts.length-1], item)
		} else {
			def subParts = parts[1..parts.length-1]
			notResolvedRefToItems[refPart] = { resolve(it, subParts, item) }
		}
	}

	private void resolve(Element base, List<String> parts, item) {
		def el = base
		def partsSize = parts.size()
		for(int i = 0; i < partsSize; i++) {
			String refPart = parts[i]
			def resolved = el.resolve(refPart)
			if(resolved) {
				el = resolved
			} else {
				def subParts = (i < partsSize-1) ? parts[i+1..partsSize-1] : []
				notResolvedPathRefToResolvers[refPart] = { resolve(el.resolve(it), subParts, item) }
				//println "Can not resolve ${refPart} in '$el.reference'"
				el = null
				break
			}
		}

		if(el) {
			setter(item, el)
		}
	}

	private addRequest(String ref, item) {
		if (!notResolvedRefToItems[ref]) {
			notResolvedRefToItems[ref] = []
		}
		notResolvedRefToItems[ref] << item
	}

	@Override
	boolean isResolved() {
		notResolvedRefToItems.isEmpty() && notResolvedPathRefToItems.isEmpty()
	}

	@Override
	void printNotResolved() {

		if(!notResolvedRefToItems.isEmpty()) {
			println "$name: ${notResolvedRefToItems.keySet()}"
		}

		if(!notResolvedPathRefToItems.isEmpty()) {
			println "$name: ${notResolvedPathRefToItems.keySet()}"
		}
	}
}
