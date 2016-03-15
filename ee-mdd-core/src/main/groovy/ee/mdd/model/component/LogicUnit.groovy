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

import ee.mdd.model.Body


/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */
class LogicUnit extends Body {
  List<MetaAttribute> metas
  List<Param> params = []
  String paramsLogicName

  String deriveParamsLogicName() {
    paramsLogicName = ''
    params.each {
      paramsLogicName += 'And' + it.cap
    }
    paramsLogicName -= 'And'
  }

  def add(Param child) {
    params << child; super.add(child)
  }

  List<Param> getParamsCustom() {
    params.findAll { !it.value }
  }

  def add(MetaAttribute item) {
    if(!metas) {
      metas = []
    }; metas << super.add(item)
  }
}
