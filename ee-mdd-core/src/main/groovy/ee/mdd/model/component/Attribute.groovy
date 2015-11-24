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
 * @author Niklas Cappelmann
 */
class Attribute extends Element {
  Names n
  Type type
  List<MetaAttribute> metas
  def value
  boolean multi = false
  boolean generic = false
  
  Names getN() {
    if (!n) {
      n = new Names(this, name)
    }
    n
  }

  def add(MetaAttribute item) {
    if(!metas) {
      metas = []
    }; metas << super.add(item)
  }

  String deriveName(Element p = parent ) {
    p ? "${p.name}${getClass().simpleName}".toLowerCase() : getClass().simpleName.toLowerCase()
  }
}
