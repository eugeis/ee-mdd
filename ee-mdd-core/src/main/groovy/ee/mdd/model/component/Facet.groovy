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

import ee.mdd.builder.BuilderAware;
import ee.mdd.builder.ModelBuilder
import ee.mdd.model.Composite


/**
 *
 * @author Eugen Eisler
 */
class Facet extends Composite implements BuilderAware {
  ModelBuilder builder
	Module module
  List<ExternalModule> externalModules = []
  List<Dependency> dependencies = []

  Closure childBuilder() {
  }
  
  def add(ExternalModule child) {
    externalModules << child; super.add(child)
  }
  
  def add(Dependency child) {
    dependencies << child; super.add(child)
  }
}