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
package ee.mdd.templates.java




/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */
class EnhancerForJava {
	private static final Object[] EMPTY_ARGUMENTS = {}

	static void enhanceClasses() {
		ExpandoMetaClass.enableGlobally()

		def properties = Collections.synchronizedMap([:])
		Map<String, String> typeToTestValue = [String: '\"TestString\"', Long: 'Long.value(1)', long: '1L',
			Integer: 'Integer.value(1)', int: '1', Date: 'new Date()', boolean: 'true', Boolean: 'Boolean.TRUE']

		Element.metaClass {
		}

		Entity.metaClass {

			getMetasForEntity << {
				->
				def key = System.identityHashCode(delegate) + 'metasForEntity'
				if(!properties.containsKey(key)) {
					Entity entity = delegate
					def metasForEntity = properties[key] = []
					if(entity.metas) {
						metasForEntity.addAll(entity.metas)
					}
					metasForEntity << new MetaAttribute(type: 'Entity')

					def namedQueries = new MetaAttribute(type: 'NamedQueries', multi: true, value: [])
					metasForEntity << namedQueries

					def finders = entity.manager?.finders
					def counters = entity.manager?.counters
					def existers = entity.manager?.finders
					def deleters = entity.manager?.deleters
					if ( finders != null ) {
						finders.each { finder ->
							def namedQuery =  new MetaAttributeNamedQuery(type: 'NamedQuery', value: [:])
							namedQuery.name = entity.name+'.'+finder.underscored
							namedQuery.query = "\"SELECT e FROM ${entity.n.cap.entity} e $propWhere\")"
							namedQueries.value << new MetaAttribute(type: 'NamedQuery', namedQuery)
						}
					}
					properties[key] = "is$delegate.cap()"
				}
				properties[key]
			}
		}

		Literal.metaClass {

			getIs << {
				->
				def key = System.identityHashCode(delegate) + 'is'
				if(!properties.containsKey(key)) {
					properties[key] = "is$delegate.cap()"
				}
				properties[key]
			}
		}

		Prop.metaClass {

			getGetter << {
				->
				def key = System.identityHashCode(delegate) + 'getter'
				if(!properties.containsKey(key)) {
					properties[key] = "get$delegate.cap()"
				}
				properties[key]
			}

			getSetter << {
				->
				def key = System.identityHashCode(delegate) + 'setter'
				if(!properties.containsKey(key)) {
					properties[key] = "set$delegate.cap($delegate.type.name $delegate.uncap)"
				}
				properties[key]
			}

			getCall << {
				->
				def key = System.identityHashCode(delegate) + 'call'
				if(!properties.containsKey(key)) {
					properties[key] = "set$delegate.cap($delegate.uncap)"
				}
				properties[key]
			}

			getTestable << {
				->
				def key = System.identityHashCode(delegate) + 'testable'
				if(!properties.containsKey(key)) {
					properties[key] = typeToTestValue.containsKey(delegate.type.name)
				}
				properties[key]
			}

			getTestValue << {
				->
				def key = System.identityHashCode(delegate) + 'testValue'
				if(!properties.containsKey(key)) {
					properties[key] = typeToTestValue.get(delegate.type.name)
				}
				properties[key]
			}
		}

		LogicUnit.metaClass {

			getCall << {
				->
				def key = System.identityHashCode(delegate) + 'call'
				if(!properties.containsKey(key)) {
					properties[key] = delegate.paramsCustom.collect { it.uncap }.join(', ')
				}
				properties[key]
			}

			getSignature << {
				->
				def key = System.identityHashCode(delegate) + 'signature'
				if(!properties.containsKey(key)) {
					properties[key] = delegate.paramsCustom.collect { it.signature }.join(', ')
				}
				properties[key]
			}

			signature << { Context c ->
				//register usage of the type, in order to calculate imports, etc.
				delegate.params.each { c.name(it.type) }
				delegate.signature
			}

			getParamsName << {
				->
				def key = System.identityHashCode(delegate) + 'paramsName'
				if(!properties.containsKey(key)) {
					properties[key] = delegate.paramsCustom.collect { it.cap }.join('And')
				}
				properties[key]
			}
		}

		Attribute.metaClass {

			getSignature << {
				->
				def key = System.identityHashCode(delegate) + 'signature'
				if(!properties.containsKey(key)) {
					//use 'name', because of primitive types
					properties[key] = "$delegate.type.name $delegate.uncap"
				}
				properties[key]
			}

			signature << { Context c ->
				//register usage of the type, in order to calculate imports, etc.
				c.name(delegate.type)
				delegate.signature
			}

			resolveValue << { Context c ->
				def ret
				def value = "$delegate.value"
				if (delegate.prop == null) {
					ret = "$delegate.value"
				} else {
					if(delegate.value[0].equals('#')) {
						ret = "this.$delegate.prop.uncap = ${c.macros.generate("${value.substring(1, value.size())}", c)}"
				} else {
					ret = "this.$delegate.prop.uncap = $delegate.value"
				}
			}
			ret
		}

	}

	MetaAttribute.metaClass {

		annotation << { Context c ->
			def key = System.identityHashCode(delegate) + 'annotation'
			if(!properties.containsKey(key)) {
				def ret = "@${c.name(delegate.type)}"
				if(delegate.multi && delegate.value){
					String newLine = System.properties['line.separator']
					ret += ' {'
					delegate.value.each { ret += "${newLine}${it.annotation(c)}" }
					ret += '}'
				} else if(delegate.value) {
					if(Map.isInstance(delegate.value)) {
						ret += '(' + delegate.value.collect { k, v -> "$k = $v" }.join(', ') + ')'
					} else {
						ret += "($delegate.value)"
					}
				}
				properties[key] = ret
			}
			properties[key]
		}

	}

}
}
