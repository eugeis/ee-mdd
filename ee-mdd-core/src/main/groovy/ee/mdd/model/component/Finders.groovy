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
package ee.mdd.model.component

import ee.mdd.model.Element

/**
 *
 * @author Niklas Cappelmann
 */
class Finders extends CompilationUnit {

  Entity getEntity() { parent }
  
  Finders getSuperFinders() { entity.superUnit?.finders}
  
  List<Find> getFinders() {
    def ret = []
    if(operations)
      ret = operations.findAll { Find.isInstance(it) }
    if (superFinders && superFinders.operations)
      ret.addAll(superFinders.operations.findAll { Find.isInstance(it) })
    ret
  }

  List<Count> getCounters() {
    def ret = []
    if(operations)
      ret = operations.findAll { Count.isInstance(it) }
    if (superFinders && superFinders.operations)
      ret.addAll(superFinders.operations.findAll { Count.isInstance(it) })
    ret
  }

  List<Exist> getExists() {
    def ret = []
    if(operations)
      ret = operations.findAll { Exist.isInstance(it) }
    if (superFinders && superFinders.operations)
      ret.addAll(superFinders.operations.findAll { Exist.isInstance(it) })
    ret
  }

  void fillReference(Map<String, Element> refToMe) {
    super.fillReference(refToMe)
    refToMe['finder'] = this
  }
}
