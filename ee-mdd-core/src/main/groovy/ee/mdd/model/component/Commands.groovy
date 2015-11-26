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
class Commands extends Controller {

  Entity getEntity() {
    parent
  }

  Commands getSuperCommands() {
    entity.superUnit?.commands
  }

  List<Delete> getDeleters() {
    def ret = []
    if(operations)
      ret = operations.findAll { Delete.isInstance(it) }
    if (superCommands)
      ret.addAll(superCommands.operations?.findAll { Delete.isInstance(it) })
    ret
  }

  List<Delete> getUpdates() {
    def ret = []
    if(operations)
      ret = operations.findAll { Update.isInstance(it) }
    if (superCommands)
      ret.addAll(superCommands.operations?.findAll { Update.isInstance(it) })
    ret
  }

  List<Delete> getCreates() {
    def ret = []
    if(operations)
      ret = operations.findAll { Create.isInstance(it) }
    if (superCommands)
      ret.addAll(superCommands.operations?.findAll { Create.isInstance(it) })
    ret
  }

  void fillReference(Map<String, Element> refToMe) {
    super.fillReference(refToMe)
    refToMe['commands'] = this
  }

  String deriveName() {
    if(!parent) {
      super.deriveName()
    } else {
      "${parent.cap}Commands"
    }
  }
}
