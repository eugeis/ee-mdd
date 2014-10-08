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

import ee.mdd.generator.CommonProcessorFactory
import ee.mdd.templates.java.ModelBuilderExample



/**
 *
 * @author Eugen Eisler
 */
class GeneratorForJs {

	static void main(def args) {
		String target = args ? new File("${args[0]}/../ee-mdd-example") : '/Users/eugeis/git/ee-mdd/ee-mdd-example'

		EnhancerForJs.enhanceClasses()

		def model =  ModelBuilderExample.build(new ExtTypeInjecterForJs().postInstantiateDelegate)

		def generator = TemplatesForJs.build()
		def commonProcessorFactory = new CommonProcessorFactory()
		def processorFactory = new ProcessorsForJs()

		generator.add(commonProcessorFactory.macrosProcessor(MacrosForJs.build()))
		generator.add(processorFactory.jsPathProcessor())
		generator.add(commonProcessorFactory.printProcessor())
		generator.add(commonProcessorFactory.fileProcessor(target))
		generator.generate(model)
	}
}
