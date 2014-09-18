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
class RefParentResolveHandler implements RefResolveHandler {
  String name
  Class type
  List<String> notResolved = []

  void onElement(Element el) {
  }

  void addResolveRequest(String ref, Composite parent, Closure setter) {
    //e.g. parent is constructor for param(prop: ref), so we need compilation unit => parent.parent
    def base = parent?.parent
    def el = base?.find { Element el -> type.isInstance(el) && el.name == ref }
    if(el) {
      setter(el)
    } else {
      notResolved[ref] = "The '$ref' can not be resolved in $base for child of $parent"
    }
  }

  @Override
  boolean isResolved() {
    notResolved.isEmpty()
  }

  @Override
  void printNotResolved() {
    if(!notResolved.isEmpty()) {
      println "$name: $notResolved"
    }
  }

}
