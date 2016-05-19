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
package ee.mdd.builder

import ee.mdd.model.Base

/**
 *
 * @author Eugen Eisler
 */
class ListResolveHandler extends AbstractResolveHandler {
    List<ResolveHandler> handlers = new ArrayList<>()

    @Override
    void on(String ref, Base el, Base parent) {
        handlers.each { it.on(ref, el, parent) }
    }

    void addHandler(ResolveHandler handler) {
        handlers << handler
    }

    @Override
    void addResolveRequest(String ref, Base el, Base parent) {
        handlers.each { it.addResolveRequest(ref, el, parent) }
    }

    @Override
    Base resolve(String ref, Base el, Base parent) {
        handlers.findResult { it.resolve(ref, el, parent) }
    }

    @Override
    boolean isResolved() {
        handlers.find { !it.isResolved() }
    }

    @Override
    void printNotResolved() {
        handlers.each { it.printNotResolved() }
    }
}
