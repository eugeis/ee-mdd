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
abstract class AbstractResolveHandler implements ResolveHandler {
    String name

    @Override
    void on(String ref, Base el) {
        on(ref, el, el?.parent)
    }

    @Override
    void on(String ref, Base el, Base parent) {
    }

    @Override
    void addResolveRequest(String ref, Base el) {
        addResolveRequest(ref, el, el?.parent)
    }

    @Override
    Base resolve(String ref, Base el) {
        resolve(ref, el, el?.parent)
    }

    @Override
    boolean isResolved() {
        true
    }

    @Override
    void printNotResolved() {
    }
}
