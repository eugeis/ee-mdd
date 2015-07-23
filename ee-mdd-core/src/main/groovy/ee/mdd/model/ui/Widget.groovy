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
package ee.mdd.model.ui

import ee.mdd.model.component.LogicUnit
import ee.mdd.model.component.Module
import ee.mdd.model.component.Names
import ee.mdd.model.component.Namespace
import ee.mdd.model.component.Type


/**
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */
class Widget extends LogicUnit {
  Names n
  boolean ml = false
  boolean _static = false
  Type type

  Names getN() {
    if (!n) {
      n = new Names(this, name)
    }
    n
  }

  Namespace getNs() {
    parent.ns
  }

  Module getModule() {
    parent.module
  }

  View getView() {
    this instanceof View ? this : parent.view
  }

  String getWidgetType() {
    getClass().getSimpleName()
  }

  String getWidgetTypeShort() {
    widgetType
  }

  String getWidgetInterface() {
    widgetType
  }

  String getFieldName() {
    "$uncap$widgetType"
  }

  String getGetter() {
    "get$cap$widgetType"
  }
}