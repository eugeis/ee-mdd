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
package ee.mdd.templates.java.cg

import ee.mdd.builder.GeneratorBuilder
import ee.mdd.generator.Generator
import ee.mdd.model.component.Component
import ee.mdd.templates.java.TemplatesForJava



/**
 *
 * @author Eugen Eisler
 */
class TemplatesForJavaCg {

	static Generator build() {
		Generator cg = new GeneratorBuilder().generator('model') {
			items ('component',
			query: { c -> c.model.findAllRecursiveDown( { Component.isInstance(it) } ) },
			before: { c -> def component = c.item; c.putAll( [ component: component ] ) } ) {
				//        template('initializer', body: '''<% c.className = component.n.cap.initializer %>${macros.generate('initializer', c)}''')
				//        template('initializerBean', body: '''<% c.className = component.n.cap.initializerBean %>${macros.generate('initializerBean', c)}''')
				//        template('initializerBase', body: '''<% c.classname = component.n.cap.initializerBase %>${macros.generate('initializerBase', c)}''')
			}
		}

		Generator ret = TemplatesForJava.build()
		ret.categories.putAll(cg.categories)
		ret
		//cg
	}
}
