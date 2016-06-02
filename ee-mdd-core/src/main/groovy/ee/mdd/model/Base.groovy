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
 *
 * @author Eugen Eisler
 */
class Base {
    protected boolean init = false
    String name
    Base parent

    void checkAndInit(Base parent) {
        if (!init) {
            this.parent = parent
            init = init()
        }
    }

    protected boolean init() {
        true
    }

    Base findParent(Base p = parent, Closure matcher) {
        if (p) {
            if (matcher(p)) {
                p
            } else {
                p.findParent(matcher)
            }
        }
    }

    Element findUp(Base p = parent, Closure matcher) {
        findParent(p, matcher)
    }

    List<Base> findParents(Base p = parent, boolean stopAfterFirstMismatch = false, def fill = [], Closure matcher) {
        if (p) {
            if (matcher(p)) {
                fill << p
                fill = p.findParents(stopAfterFirstMismatch, fill, matcher)
            } else if (!stopAfterFirstMismatch) {
                fill = p.findParents(stopAfterFirstMismatch, fill, matcher)
            }
        }
        fill
    }

    String deriveName(Base p = parent) {
        p ? "${p.name}${getClass().simpleName}" : getClass().simpleName
    }

    String getName() {
        if (!name) {
            name = deriveName()
        }; name
    }

    Map attributes() {}
}
