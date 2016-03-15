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

import ee.mdd.model.Element

/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */
class CompilationUnit extends Type {
  Names n
  boolean virtual = false
  boolean base = false
  boolean singleton = false
  boolean attributeChangeFlag = false
  boolean propSetters = true
  CompilationUnit superUnit
  List<String> superGenericRefs
  List<String> interfs = []
  List<Prop> props
  List<Constructor> constructors = []
  List<Operation> operations
  List<String> generics
  Set types = []

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

  Element find(Closure matcher) {
    Element ret = super.find(matcher)
    if(!ret && superUnit) {
      ret = superUnit.find(matcher)
    }
    ret
  }

  List findAll(Closure matcher) {
    def ret = super.findAll(matcher)
    if(superUnit) {
      ret.addAll(superUnit.findAll(matcher))
    }
    ret
  }

  Prop resolveProp(String propName) {
    Prop ret = props.find { it.name == propName }
    if(!ret && superUnit) {
      ret = superUnit.resolveProp(propName)
    }
    if(!ret) {
      println "Prop '$propName' can't be resolved in $name"
    }
    ret
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

  def add(String item) {
    if(!generics) {
      generics = []
    }; generics << super.add(item)
  }
}
