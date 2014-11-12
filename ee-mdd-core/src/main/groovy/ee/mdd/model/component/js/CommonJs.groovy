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
package ee.mdd.model.component.js

import java.util.Map;

import ee.mdd.model.component.Facet;

/**
 *
 * @author Eugen Eisler
 */
class CommonJs extends Facet {

	def primitiveTypes = [
		'int',
		'long',
		'float',
		'double',
		'boolean',
		'Integer',
		'Long',
		'Float',
		'Double',
		'Boolean',
		'String',
		'Date'
	]

	def init() {
		primitiveTypes.each { n -> builder.extType(name: n) }
		super.init();
	}
}
