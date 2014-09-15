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

import ee.mdd.model.Element

/**
 *
 * @author Eugen Eisler
 */
class RefHolder {
  String name
  Class type
  boolean global = true

  Map<String, Object> refToResolved = [:]
  Map<String, List<Closure>> notResolvedRefToSetters = [:]

  void addResolved(Element el) {
    def ref = el.name
    refToResolved[ref] = el

    //resolve not resolved yet
    if(notResolvedRefToSetters.containsKey(ref)) {
      notResolvedRefToSetters[ref].each { it.call(el) }
      notResolvedRefToSetters.remove(ref)
    }
  }

  void resolveOrStore(String ref, Closure setter) {
    if(refToResolved.containsKey(ref)) {
      setter.call(refToResolved[ref])
    } else {
      if(!notResolvedRefToSetters[ref]) {
        notResolvedRefToSetters[ref] = []
      }
      notResolvedRefToSetters[ref] << setter
    }
  }
}
