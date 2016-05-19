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

import ee.mdd.model.Base

/**
 *
 * @author Eugen Eisler
 */
class ParentResolveHandler extends AbstractResolveHandler {
  String name
  Class type
  int depth = 0
  List<String> notResolved = []
  Closure setter

  void addResolveRequest(String ref, Base el, Base parent) {
    def ret
    def base = parent
    for (int i = 0; i <= depth; i++) {
      if (base) {
        ret = base.find { Base e -> type.isInstance(e) && e.name == ref }
        if(ret) {
          break
        } else {
          base = base?.parent
        }
      }
    }

    if(ret) {
      setter(el, ret)
    } else {
      notResolved << "The '$ref' can not be resolved in $base for child of $parent"
    }
  }

  Base resolve(String ref, Base el, Base parent) {
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
