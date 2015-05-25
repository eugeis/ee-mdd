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

import ee.mdd.model.Composite
import groovy.util.logging.Slf4j

/**
 *
 * @author Eugen Eisler
 */
@Slf4j
abstract class AbstractGenerator extends Composite {
  List<Processor> processors
  OutputPurpose purpose = OutputPurpose.PRODUCTION
  OutputType type = OutputType.API
  String facet

  protected void before(Context c) {
    if(processors) {
      processors.findAll { it.before }.each { Processor p ->
        try {
          p.before(c)
        }catch(e) {
          log.error  "$name: Before '$p' failed '$e'", e
        }
      }
    }
  }

  protected  void after(Context c) {
    if(processors) {
      processors.findAll { it.after }.each { Processor p ->
        try {
          p.after(c)
        } catch(e) {
          log.error  "$name: After '$p' failed '$e'", e
        }
      }
    }
  }
  
  protected void extendContextOutput(Context c) {
    if(purpose) {
      c.outPurpose = purpose
    }

    if(type) {
      c.outType = type
    }

    if(facet) {
      c.facet = facet
    }
  }

  def add(Processor child) {
    if(processors == null) {
      processors = []
    }
    processors << child; super.add(child)
  }
}
