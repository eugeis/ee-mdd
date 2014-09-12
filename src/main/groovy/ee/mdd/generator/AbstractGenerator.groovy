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
import ee.mdd.model.Composite

/**
 *
 * @author Eugen Eisler
 */
@Slf4j
abstract class AbstractGenerator extends Composite {
	List<Processor> processors

	protected void before(Context c) {
		processors?.findAll { it.before }.each { Processor p ->
			try {
				p.before(c)
			}catch(e) {
				log.error  "$name: Before '$p' failed '$e'", e
			}
		}
	}

	protected  void after(Context c) {
		processors?.findAll { it.after }.each { Processor p ->
			try {
				p.after(c)
			} catch(e) {
				log.error  "$name: After '$p' failed '$e'", e
			}
		}
	}


	def add(Processor child) {
		if(processors == null) {
			processors = []
		}
		processors << child; super.add(child)
	}
}
