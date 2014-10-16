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

import ee.mdd.builder.BuilderAware
import ee.mdd.builder.ModelBuilder
import ee.mdd.model.Composite



/**
 *
 * @author Eugen Eisler
 */
class StructureUnit extends Composite implements BuilderAware {
  ModelBuilder builder
  String key
  Namespace namespace
  Names n
  Map<String, Facet> facets = [:]

  def add(Facet child) {
    facets[child.name] = super.add(child); child
  }

  StructureUnit getSu() {
    this
  }

  Namespace getNs() {
    namespace ? namespace : parent.ns
  }

  Names getN() {
    if (!n) {
      n = new Names(key)
    }
    n
  }

  Model getModel() {
    parent ? parent.model : null
  }
}
