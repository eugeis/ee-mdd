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

import groovy.text.GStringTemplateEngine
import groovy.util.logging.Slf4j
import ee.mdd.model.component.Type

/**
 *
 * @author Eugen Eisler
 */
@Slf4j
class TemplateGenerator extends AbstractGenerator {
	private static final GStringTemplateEngine engine = new GStringTemplateEngine()
  
  String appendPrefix = '//'
  boolean appendName = false
  
	String body
	List<Type> types = []

	def generate(Context c) {
		def ret = ''
		before(c)
		try {
			def result = engine.createTemplate(body).make(c.storage)
			c.output = result.toString()
			
      if(appendName) {
        c.output += "$appendPrefix$name"
			}
      ret = c.output
		} catch(e) {
			log.error "Failed generation of template=$name, e=$e"
			c.error = e
		}
		after(c)
		ret
	}

	def add(Type child) {
		types << child; super.add(child)
	}
}
