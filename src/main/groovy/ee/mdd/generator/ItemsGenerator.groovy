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
import ee.mdd.model.component.Model

/**
 *
 * @author Eugen Eisler
 */
@Slf4j
class ItemsGenerator extends CategoryGenerator {
	Closure query

	void generate(Context globalContext, Closure executor) {
		log.info "$name: Generate for contetx '$globalContext'"

		select(globalContext).each { def item ->

			templates.each { templateName, TemplateGenerator template ->
				Context templateContext = globalContext.clone()
				templateContext.item = item

				extendContext(templateContext)

				generate(template, templateContext, executor)
			}
		}
	}

	private def select(Context c) {
		def ret
		try {
			ret = query(c)
		} catch (e) {
			log.error "$name: Items can not be selected because of exception $e"
			ret = []
		}
		ret
	}
}
