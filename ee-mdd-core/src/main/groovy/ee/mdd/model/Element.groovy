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
import java.util.Map;



/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */
class Element extends Base {
  String desc
  String uncap, cap, underscored, sqlName, xmlValue, description, uri
  boolean xml = true;

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

  String getReference() {
    getName()
  }

  void fillReference(Map<String, Base> fillRefToResolved) {
    fillRefToResolved[reference] = this
  }
}
