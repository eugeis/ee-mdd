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

import ee.mdd.ModelBuilder
import ee.mdd.builder.BuilderAware
import ee.mdd.model.Composite


/**
 *
 * @author Eugen Eisler
 */
class Facet extends Composite implements BuilderAware {
  ModelBuilder builder
  Module module
  String path
  List<ExternalModule> externalModules = []
  Map<String, Facet> facets = [:]
  List<Module> dependencies = []

  def add(ExternalModule child) {
    externalModules << child; super.add(child)
  }

  def add(Module child) {
    dependencies << child; super.add(child)
  }

  def add(Facet child) {
    facets[child.name] = super.add(child); child
  }

  Facet getRootFacet() {
    Facet ret = this
    if(Facet.isInstance(parent)) {
      ret = parent.rootFacet
    }
    ret
  }

  @Override
  public String toString() {
    "${getClass().simpleName} [name=$name]"
  }
}