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

import ee.mdd.model.component.DataType
import ee.mdd.model.component.DataTypeProp
import ee.mdd.model.component.Prop


/**
 *
 * @author Eugen Eisler
 */
class PropFactory extends CompositeFactory {

  public PropFactory() {
    super()
    this.beanClass = Prop
  }

  Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
    if (checkValue(name, value)) {
      return value
    }
    def parent = builder.parent
    def ret
    if(DataType.isInstance(parent)) {
      ret = new DataTypeProp()
    } else {
      ret = new Prop()
    }

    if(value != null) {
      //attributes[valueProperty] = value
      ret[valueProperty] = value
    }
    ret
  }
}
