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




/**
 *
 * @author Eugen Eisler
 */
class Model extends StructureUnit {
  List<Model> model = []
  List<Component> components = []
  List<ExternalType> externalTypes = []

  def init() {
    if(!namespace) {
      namespace = new Namespace(name: key, parent: this).init()
    }
    super.init()
  }

  Model getModel() {
    this
  }

  def add(ExternalType child) {
    externalTypes << super.add(child)
  }

  def add(Component child) {
    components << super.add(child)
  }

  def add(Model child) {
    model << super.add(child)
  }
}
