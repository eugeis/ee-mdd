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
 * @author Niklas Cappelmann
 */
class Module extends StructureUnit {
  boolean startupInitializer = false
  boolean facade = false

  List<EnumType> enumTypes = []
  List<Pojo> pojos = []
  List<Entity> entities = []
  List<BasicType> basicType = []
  List<Controller> controllers = []
  List<Initializer> initializers = []
  List<Facade> services = []
  List<Channel> channels = []
  List<Module> dependencies = []
  List<Config> configs = []
  List<Container> containers = []

  Component getComponent() {
    parent.component
  }

  Module getModule() {
    this
  }

  def add(EnumType child) {
    enumTypes << child; super.add(child)
  }
  def add(Pojo child) {
    pojos << child; super.add(child)
  }
  def add(Entity child) {
    entities << child; super.add(child)
  }
  def add(BasicType child) {
    basicType << child; super.add(child)
  }
  def add(Controller child) {
    controllers << child; super.add(child)
  }
  def add(Facade child) {
    services << child; super.add(child)
  }
  def add(Initializer child) {
    initializers << child; super.add(child)
  }
  def add(Module child) {
    dependencies << child; super.add(child)
  }
  def add(Channel child) {
    channels << child; super.add(child)
  }
  def add(Config child) {
    configs << child; super.add(child)
  }
  def add(Container child) {
    containers << child; super.add(child)
  }
}
