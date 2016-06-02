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

    List findAllDown(Closure matcher, boolean stopSteppingDownIfFound = false) {
        fillDown(matcher, [], stopSteppingDownIfFound)
    }

    List fillDown(Closure matcher, List fill, boolean stopSteppingDownIfFound = false) {
        def items = children.findAll(matcher)
        if (items) {
            fill.addAll(items)
        }
        if (!stopSteppingDownIfFound || !items) {
            children.findAll { Composite.isInstance(it) }*.fillDown(matcher, fill)
        }
        fill
    }

    Element findDown(Closure matcher) {
        Element ret = children.find(matcher)
        if (!ret) {
            ret = children.find { Composite.isInstance(it) }*.findRecursiveDown(matcher)
        }
        ret
    }

    Element findUp(Base p = parent, Closure matcher) {
        Element ret = children.find(matcher)
        if (!ret && p) {
            if (matcher(p)) {
                ret = p
            } else {
                ret = p.findUp(matcher)
            }
        }
        ret
    }

    Element resolve(String ref) {
        //lazy init of resolved
        if (refToResolved == null) {
            refToResolved = [:]
            children.each { Element element -> element.fillReference(refToResolved) }
        }
        refToResolved[ref]
    }

    /** resolve */
    def propertyMissing(String ref) {
        resolve(ref)
    }

    void buildChildren() {
        if (children) {
            children.clone().each { it.buildMe(); it.buildChildren() }
        }
    }
}
