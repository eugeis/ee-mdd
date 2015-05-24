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
package ee.mdd.model

/**
 * @author Eugen Eisler
 */
class Composite extends Element {
  List<Element> children = []
  Map<String, Element> refToResolved

  def add(Element child) {
    children << child; child.checkAndInit(this); child
  }

  Element find(Closure matcher) {
    children.find(matcher)
  }

  List findAll(Closure matcher) {
    children.findAll(matcher)
  }

  List findAllRecursiveDown(Closure matcher) {
    fillRecursiveDown(matcher, [])
  }

  List fillRecursiveDown(Closure matcher, List fill) {
    fill.addAll ( children.findAll(matcher) )
    children.findAll { Composite.isInstance(it) }*.fillRecursiveDown(matcher, fill)
    fill
  }

  Element findRecursiveDown(Closure matcher) {
    Element ret = children.find(matcher)
    if(!ret) {
      children.find { Composite.isInstance(it) }*.findRecursiveDown(matcher)
    }
    ret
  }

  Element findRecursiveUp(Closure matcher) {
    Element ret = children.find(matcher)
    if(!ret && parent) {
      parent.findRecursiveUp(matcher)
    }
    ret
  }

  Element resolve(String ref) {
    //lazy init of resolved
    if(refToResolved == null) {
      refToResolved = [:]
      children.each { Element element -> element.fillReference(refToResolved) }
    }
    refToResolved[ref]
  }

  /** resolve */
  def propertyMissing(String ref) {
    resolve(ref)
  }
}
