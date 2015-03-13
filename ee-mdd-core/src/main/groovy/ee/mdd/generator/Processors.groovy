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

import ee.mdd.generator.AbstractGenerator
import ee.mdd.generator.Processor
import ee.mdd.model.Element
import ee.mdd.model.component.Namespace

/**
 *
 * @author Eugen Eisler
 */
class Processors {

  Processor fileProcessor(String target) {
    Processor ret = new Processor(name: 'writeFile')

    ret.before = { c ->
      c.path = null
      c.overwrite = true
    }

    ret.after = { c ->
      if(!c.error && c.path) {
        File file = new File("$target/$c.path")
        if(!file.parentFile.exists()) {
          file.parentFile.mkdirs()
        }
        if(!file.exists() || c.overwrite) {
          println "Write file $file"
          file.withWriter('unicode'){ w-> w << c.output }
        }
      }
    }
    ret
  }

  Processor printProcessor(String target) {
    Processor ret = new Processor(name: 'prinln')

    ret.after = { c -> if (!c.error) { println c.output } }
    ret
  }

  Processor macrosProcessor(AbstractGenerator macrosGenerator) {
    Processor ret = new Processor(name: 'macros')

    ret.before = { c ->
      c.macros = macrosGenerator
    }
    ret
  }
}
