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

import ee.mdd.model.Namespace
import ee.mdd.model.realm.Realm


/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */
class Component extends StructureUnit {
    List<ModuleGroup> moduleGroups = []
    List<Module> modules = []
    Module shared
    Realm realm
    ComponentProfile componentProfile
    UserProfile userProfile
    String group

    protected boolean init() {
        if (!namespace) {
            namespace = new Namespace(name: key ?: name); namespace.checkAndInit(this)
        }
        super.init()
    }

    Component getComponent() { this }

    boolean hasProfiles() { componentProfile || userProfile }

    def add(Module child) {
        modules << child; super.add(child)
        this.metaClass."$child.ref" = child
        this
    }

    def add(Realm item) {
        realm = super.add(item)
        if (!realm)
            realm = item
    }

    def add(ModuleGroup child) {
        moduleGroups << child; super.add(child)
    }

    Module getModule() {
        if (!shared && modules) {
            shared = modules.get(0)
        }
        shared
    }

    protected StringBuffer fillToString(StringBuffer buffer) {
        super.fillToString(buffer).append(SEPARATOR)
        if (group) {
            buffer.append(group)
        }
    }
}
