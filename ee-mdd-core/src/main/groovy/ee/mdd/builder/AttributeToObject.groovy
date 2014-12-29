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


/**
 *
 * @author Eugen Eisler
 */
class AttributeToObject {
	Map<String, Factory> attributeNameToFactory = [:]

	void add(String name, Factory factory) {
		attributeNameToFactory[name] = factory
	}

	def attributteDelegate = { FactoryBuilderSupport builder, node, Map attributes ->
		attributeNameToFactory.each { name, Factory factory ->
			if (attributes.containsKey(name)) {
				def attrs = [:]
				Object item = factory.newInstance(builder, name, attributes[name], attrs)
				builder.postInstantiate(null, attrs, item)
				builder.handleNodeAttributes(item, attrs)
				item.parent = node
				node[name] = item
			}
		}
		attributeNameToFactory.each { name, Factory factory -> attributes.remove(name) }
	}
}
