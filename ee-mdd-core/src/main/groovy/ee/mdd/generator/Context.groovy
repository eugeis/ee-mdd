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
package ee.mdd.generator;

/**
 *
 * @author Eugen Eisler
 */
class Context {
  String name
  
  OutputPurpose outputPurpose
  OutputType outputType
  String facet
  
  Map storage = null

  Context() {
    storage = [c: this]
  }

  def propertyMissing(String name, value) {
    storage[name] = value
  }

  def propertyMissing(String name) {
    if(!storage.containsKey(name)) {
      storage[name] = [:]
    }
    storage[name]
  }

  void putAll(Map m) {
    storage.putAll(m)
  }

  def get(String name) {
    storage[name]
  }

  Context clone() {
    Context ret = new Context(name: name, outputPurpose:outputPurpose, outputType: outputType, facet: facet, storage: this.storage.clone())
    ret.c = ret
    ret
  }

  String toString() {
    "${getClass().simpleName} [name=$name]"
  }
}
