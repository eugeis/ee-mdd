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
package ee.mdd.templates.js

import ee.mdd.builder.ModelBuilder
import ee.mdd.generator.CommonProcessorFactory
import ee.mdd.model.component.Model
import ee.mdd.model.component.js.Js
import ee.mdd.templates.java.ModelBuilderExample



/**
 *
 * @author Eugen Eisler
 */
class GeneratorForJs {

	static void main(def args) {
		String target = args ? new File("${args[0]}/../ee-mdd-example_js") : '/Users/eugeis/git/ee-mdd/ee-mdd-example_js'

		EnhancerForJs.enhanceClasses()

		def builder = new ModelBuilder()
		registerJs(builder)

		Model model =  ModelBuilderExample.build (builder)

		def generator = TemplatesForJs.build()
		def commonProcessorFactory = new CommonProcessorFactory()
		def processorFactory = new ProcessorsForJs()

		generator.add(commonProcessorFactory.macrosProcessor(MacrosForJs.build()))
		generator.add(processorFactory.jsPathProcessor())
		generator.add(commonProcessorFactory.printProcessor())
		generator.add(commonProcessorFactory.fileProcessor(target))
		generator.generate(model)
	}

	private static registerJs(ModelBuilder builder) {
		builder.registerFacet(Js)

		def factets = builder.model('facets') {
			commonJs()
		}
	}
}
