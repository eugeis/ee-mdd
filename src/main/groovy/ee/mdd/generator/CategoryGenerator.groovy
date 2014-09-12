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
class CategoryGenerator extends AbstractGenerator {
	Map<String, TemplateGenerator> templates = [:]
	Closure before

	void generate(Context globalContext, Closure executor) {
		log.info "$name: Generate for contetx '$globalContext'"

		templates.each { templateName, TemplateGenerator template ->
			Context templateContext = globalContext.clone()
			extendContext(templateContext)
			generate(template, templateContext, executor)
		}
	}

	def generate(TemplateGenerator template, Context templateContext, Closure executor) {
		def ret
		executor(template, templateContext) { tmpl, c ->
			ret = generateTemplate(tmpl, c)
		}
		ret
	}

	def generate(String template, Context c) {
		def ret = ''
		if(templates.containsKey(template)){
			ret = generateTemplate(templates[template], c)
		} else {
			log.error "$name: Generation of '$template' is not possible, because template '$template' does not exists."
		}
		ret
	}

	protected def generateTemplate(TemplateGenerator template, Context c) {
		before(c)
		template.generate(c)
		after(c)
		c.output
	}

	protected void extendContext(Context c) {
		try {
			before?.call(c)
		} catch (e) {
			log.error "Context can not be extended.", e
		}
	}

	def add(TemplateGenerator child) {
		templates[child.name] = super.add(child); child
	}

	def add(Processor child) {
		if(processors == null) {
			processors = []
		}
		processors << child; super.add(child)
	}
}
