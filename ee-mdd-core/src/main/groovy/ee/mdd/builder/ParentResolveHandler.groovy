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
package ee.mdd.builder

import ee.mdd.model.Composite
import ee.mdd.model.Element



/**
 *
 * @author Eugen Eisler
 */
class ParentResolveHandler implements ResolveHandler {
  String name
  Class type
  int depth = 0
  List<String> notResolved = []
  Closure setter

  void onElement(Element el) {
  }

  void addResolveRequest(String ref, Composite parent, item) {
    def el
    def base = parent
    for (int i = 0; i <= depth; i++) {
      if (base) {
        el = base.find { Element e -> type.isInstance(e) && e.name == ref }
        if(el) {
          break
        } else {
          base = base?.parent
        }
      }
    }

    if(el) {
      setter(item, el)
    } else {
      notResolved << "The '$ref' can not be resolved in $base for child of $parent"
    }
  }

  @Override
  boolean isResolved() {
    notResolved.isEmpty()
  }

  @Override
  void printNotResolved() {
    if(!notResolved.isEmpty()) {
      println "Not resolved: $name: $notResolved"
    }
  }
}
