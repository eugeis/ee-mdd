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
 * @author Eugen Eisler
 */
class NamesBuilder {
  Map storage = [:]
  Element el
  String _base
  Closure builder = { b, n -> "${b}$n" }

  def propertyMissing(String name, value) {
    storage[name] = value
  }

  def propertyMissing(String name) {
    if(!storage.containsKey(name)) {
      storage[name] = builder(_base, name)
      //println "New name $name ${storage[name]}"
    }
    storage[name]
  }

  def methodMissing(String name, callback) {
    String ret = propertyMissing(name)
    callback[0](el, ret)
  }

  void putAll(Map m) {
    storage.putAll(m)
  }
}
