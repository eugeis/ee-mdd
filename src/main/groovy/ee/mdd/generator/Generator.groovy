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
package ee.mdd.generator

import groovy.util.logging.Slf4j

import java.util.concurrent.Callable

import ee.mdd.model.component.Model

/**
 *
 * @author Eugen Eisler
 */
@Slf4j
class Generator extends AbstractGenerator {
	Map<String, CategoryGenerator> categories = [:]

	void generate(Model model) {
		log.info "$name: Generate for model '$model.name'"
		Executor executor = new Executor(5)

		Context c = new Context()
		c.model = model

		categories.each { catName, CategoryGenerator items ->
			items.generate(c) { template, templateContext, generator ->

				executor.submit ( {
					try {
						before(templateContext)
						generator(template, templateContext)
						after(templateContext)
					}catch(e) {
						log.error "$name: Generation of '${catName}.$template is not possible, because category '$catName' does not exists."
					}
				} as Callable )
			}
		}
		executor.shutdownAndAwaitTermination()
	}


	def generate(String items, String template, Context c) {
		def ret = ''
		if(items.containsKey(items)){
			before(c)
			ret = items[items].generate(template, c)
			after(c)
		} else {
			log.error "$name: Generation of '${items}.$template is not possible, because category '$items' does not exists."
		}
		ret
	}

	def add(CategoryGenerator child) {
		categories[child.name] = super.add(child); child
	}
}