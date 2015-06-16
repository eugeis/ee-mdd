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
import ee.mdd.builder.AbstractFactoryBuilder;
import ee.mdd.builder.BuilderAware
import ee.mdd.builder.MddFactory
import ee.mdd.model.Composite
import groovy.lang.Closure;



/**
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */
class StructureUnit extends Composite implements BuilderAware {
  AbstractFactoryBuilder builder
  MddFactory factory

    String artifact
  String key
  String version
  Namespace namespace
  Names n
  Map<String, Facet> facets = [:]

  protected boolean init() {
    super.init()
    initArtifact()
    true
  }

  protected initArtifact() {
    if(!artifact) {
      if(parent?.artifact) {
        artifact = "${parent.artifact}-${key ? key : name}"
      } else {
        artifact = key ? key : name
      }
    }
  }

  def add(Facet child) {
    facets[child.name] = super.add(child); child
  }

  StructureUnit getSu() {
    this
  }

  Namespace getNs() {
    namespace ? namespace : parent?.ns
  }

  Names getN() {
    if (!n) {
      if(key)
        n = new Names(this, key)
      else
        n = new Names(this, name)
    }
    n
  }

  Model getModel() {
    parent ? parent.model : null
  }

  String getVersion() {
    version ? version : parent.version
  }

  def add(Namespace item) {
    namespace = item
  }

  boolean isFacetEnabled(Facet facet) {
    boolean ret = facets[facet.rootFacet.name] != null
    ret
  }
  
  def extend(Closure closure) {
    builder.createChildNodes(this, factory, closure)
  }
}
