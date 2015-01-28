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
 * @author Niklas Cappelmann
 */
class Element {
  String name, desc
  Element parent
  String uncap, cap, underscored, sqlName, description, uri
  boolean xml = true;

  def init() {
    this
  }

  Element findParent(Closure matcher) {
    if(parent) {
      if(matcher(parent)){
        parent
      } else {
        parent.findParent(matcher)
      }
    }
  }

  List<Element> findParents(Closure matcher, def fill = []) {
    if(parent) {
      if(matcher(parent)) {
        fill << parent
      }
      fill = parent.findParents(matcher, fill)
    }
    fill
  }

  String deriveName(Element p = parent ) {
    p ? "${p.name}${getClass().simpleName}" : getClass().simpleName
  }

  String getName() {
    if(!name) {
      name = deriveName()
    }; name
  }

  String getCap() {
    if(cap == null) {
      cap = getName().capitalize()
    }; cap
  }

  String getUncap() {
    if(uncap == null) {
      uncap = Introspector.decapitalize(getName())
    }; uncap
  }

  String getUnderscored() {
    if(underscored == null) {
      underscored = getName().replaceAll(/(\B[A-Z])/,'_$1').toUpperCase()
    }; underscored
  }

  String getSqlName() {
    if(sqlName == null) {
      sqlName = getUnderscored().replaceAll(/(?<!^)(?<!_)[QEUIOAJY]/, '')
      sqlName = sqlName.replaceAll(/(\w)\1+/, '$1')
    }; sqlName
  }

  String getUri() {
    if(uri == null) {
      uri = "${parent.getUri()}"
    }; uri
  }

  String getReference() {
    getName()
  }

  void fillReference(Map<String, Element> fillRefToResolved) {
    fillRefToResolved[reference] = this
  }
}
