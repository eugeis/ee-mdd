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
class CompilationUnit extends Type {
  Names n
  boolean virtual = false
  boolean singleton = false
  CompilationUnit superUnit
  List<Prop> props
  List<Constructor> constructors
  List<Operation> operations

  Module getModule() {
    parent.module
  }

  Component getComponent() {
    parent.component
  }

  Names getN() {
    if (!n) {
      n = new Names(this, name)
    }
    n
  }

  def add(Prop item) {
    if(!props) {
      props = []
    }; props << super.add(item)
  }

  def add(Constructor item) {
    if(!constructors) {
      constructors = []
    }; constructors << super.add(item)
  }

  def add(Operation item) {
    if(!operations) {
      operations = []
    }; operations << super.add(item)
  }
}
