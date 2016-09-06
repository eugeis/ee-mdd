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

import ee.mdd.model.statemachine.StateMachine


/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */
class Controller extends CompilationUnit {
  boolean importChanges = false
  boolean deleteBeforeImport = true
  boolean cache = false
  boolean asyncImport = false
  
  protected boolean init() {
    boolean ret = super.init()
    if(Container.isInstance(parent)) {
      def op
      add(new Operation(name: 'loadAll', nameExternal: "load${parent.cap}", ret: parent))
      // ret should be: parent.n.cap.versions but cannot be resolved
      add(new Operation(name: 'loadVersions', nameExternal: "load${parent.n.cap.versions}", ret: parent))
      add(new Operation(name: 'deleteAll', nameExternal: "delete${parent.cap}"))
      add(new Operation(name: 'loadDiff', nameExternal: "load${parent.n.cap.diff}", ret: parent))
      op = new Operation(name: 'importContainer', nameExternal: "import${parent.cap}", transactional: true)
      op.add(new Param(name: 'container', type: parent))
      add(op)
      op = new Operation(name: 'importChangesContainer', nameExternal: "importChanges${parent.cap}", transactional: true)
      op.add(new Param(name: 'container', type: parent))
      add(op)
    }
  }
  
  String deriveName() {
    if(!parent) {
      getClass().simpleName
    } else if (parent instanceof StructureUnit || parent instanceof StateMachine) {
      "${parent.capShortName}Controller"
    } else { 
      "${parent.cap}Controller"
    }
  }
  
}
