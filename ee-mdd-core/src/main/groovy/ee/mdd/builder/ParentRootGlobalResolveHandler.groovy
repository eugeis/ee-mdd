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
class ParentRootGlobalResolveHandler extends AbstractResolveHandler {
    Class type
    Class parentRootType
    Closure setter
    Set<Class> globalTypes
    Map<Object, GlobalResolveHandler> parentRootToResolveHandler = new HashMap<>()

    void on(String ref, Base el, Base parent) {
        if (type.isInstance(el)) {
            Class globalType = globalTypes?.find { Class clazz -> clazz.isInstance(el) }
            if (!globalTypes || globalType) {
                GlobalResolveHandler resolveHandler = getOrCreateParentRootHandler(el, parent)
                if (resolveHandler) {
                    resolveHandler.on(ref, el, parent)
                }
            }
        }
    }

    Base resolve(String ref, Base el, Base parent) {
        GlobalResolveHandler resolveHandler = getOrCreateParentRootHandler(el, parent)
        if (resolveHandler) {
            resolveHandler.resolve(ref, el, parent)
        }
    }

    protected GlobalResolveHandler getOrCreateParentRootHandler(Base el, Base parent) {
        GlobalResolveHandler ret
        if (el) {
            if (parentRootType.isInstance(el)) {
                ret = parentRootToResolveHandler[el];
                if (ret == null) {
                    parentRootToResolveHandler[el] = ret = new GlobalResolveHandler(name: name, type: type,
                            setter: setter, globalTypes: globalTypes)
                }
            } else if (parent) {
                ret = getOrCreateParentRootHandler(parent, parent.parent)
            }
        }
        ret
    }

    void addResolveRequest(String ref, Base el, Base parent) {
        GlobalResolveHandler resolveHandler = getOrCreateParentRootHandler(el, parent)
        resolveHandler.addResolveRequest(ref, el, parent)
    }

    @Override
    boolean isResolved() {
        !parentRootToResolveHandler.values().find { !it.isResolved() }
    }

    @Override
    void printNotResolved() {
        parentRootToResolveHandler.values().each { it.printNotResolved() }
    }
}
