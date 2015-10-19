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
  boolean cache = false
  
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
