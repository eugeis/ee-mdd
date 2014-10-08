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

import ee.mdd.generator.Processor
import ee.mdd.model.Element



/**
 *
 * @author Eugen Eisler
 */
class ProcessorsForJs {

  Processor jsPathProcessor() {
    Processor ret = new Processor(name: 'jsImportsPath')

    ret.before = { c ->
      //add 'name' methods to context object
      c.metaClass {

        name = { Element element -> element.name  }
        name = { String ref -> ref }
      }
    }

    ret.after = { c ->
      if(c.className) {
        def ns = c.module?.ns
        def subPkg = c.subPkg ? ".$c.subPkg" : ''
        if(!c.scope) { c.scope = 'main' }
        def path = c.src ? "src/$c.scope/js" : "src-gen/$c.scope/js"
        def subPath = c.subPkg ? "/$c.subPkg" : ''
        c.path = "$path/$ns.name$subPath/${c.className}.js"
        c.overwrite = !c.src
      }
    }
    ret
  }
}
