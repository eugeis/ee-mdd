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
package ee.mdd.model

import java.beans.Introspector

/**
 *
 * @author Eugen Eisler
 */
class Names {
  Element el
  String _base
  NamesBuilder cap, uncap, underscored

  Names(Element el, String base) {
    this.el = el
    this._base = base
    cap = new NamesBuilder(el: el, _base: base.capitalize(), builderClosure: { b, n -> "${b}${n.capitalize()}" })
    uncap = new NamesBuilder(el: el, _base: Introspector.decapitalize(base), builderClosure: { b, n -> "${b}${Introspector.decapitalize(n)}" })
    underscored = new NamesBuilder(el: el, _base: base.replaceAll(/(\B[A-Z])/,'_$1').toUpperCase(), builderClosure: { b, n -> n + n.replaceAll(/(\B[A-Z])/,'_$1').toUpperCase() })
  }

  void addAll(List<String> names, String namespace = null) {
    names.each { add(it, namespace )}
  }

  void add(String name, String namespace = null) {
    cap.add(name, namespace)
    uncap.add(name, namespace)
    underscored.add(name, namespace)
  }
}
