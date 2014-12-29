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
import ee.mdd.model.component.DataTypeProp



/**
 *
 * @author Eugen Eisler
 */
class OppositeResolveHandler implements RefResolveHandler {
	String name
	Class type
	Map<DataTypeProp, String> notResolved = [:]

	Closure setter

	void onElement(Element el) {
	}

	void onDataTypeProp(DataTypeProp prop) {
		if(notResolved.containsKey(prop)) {
			if(resolve(prop, notResolved[prop])) {
				notResolved.remove(prop)
			}
		}

		//try to find opposite by type, also without definition
		if(!prop.opposite) {
			resolve(prop)
		}
	}

	void addResolveRequest(String oppositeName, Composite parent, prop) {
		if(!resolve(prop, oppositeName)) {
			notResolved[prop] = oppositeName
		}
	}

	private def resolve(DataTypeProp prop, String oppositeName = null) {
		if(prop.type) {
			if(oppositeName) {
				prop.opposite = prop.type.props.find { it.name == oppositeName }
			} else {
				List<DataTypeProp> possibleOpposites = prop.type.props.findAll { it.type == prop }
				//only if there is only one property with same type, can be auto opposite
				if(possibleOpposites && possibleOpposites.size() == 1) {
					prop.opposite = possibleOpposites.get(0)
				}
			}
		}
		prop.opposite
	}

	@Override
	boolean isResolved() {
		notResolved.isEmpty()
	}

	@Override
	void printNotResolved() {
		if(!notResolved.isEmpty()) {
			println "Not resolved $name: $notResolved"
		}
	}
}