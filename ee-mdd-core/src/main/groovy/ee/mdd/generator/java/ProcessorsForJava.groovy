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
package ee.mdd.generator.java

import ee.mdd.builder.TypeResolver
import ee.mdd.generator.Context
import ee.mdd.generator.Processor
import ee.mdd.model.Base
import ee.mdd.model.Element
import ee.mdd.model.component.Module


/**
 *
 * @author Eugen Eisler
 */
class ProcessorsForJava {
    TypeResolver typeResolver
    Closure targetModuleResolver = { Context c ->
        def outType = c.outputType
        def outPurp = c.outputPurpose
        def defaultType = (outType.logic ? true : false)
        def defaultPurpose = (outPurp.production ? true : false)
        def modules = c.component.modules
        def targetLayout = c.targetLayout

        if (targetLayout.equals('standard')) {

            if (defaultPurpose) {

                if (outType.logic || outType.integ) {
                    c.module
                } else if (outType.api) {
                    modules.find { it.name.equals('api') }
                } else if (outType.apiImpl) {
                    modules.find { it.name.equals('api_impl') }
                } else if (outType.shared) {
                    modules.find { it.name.equals('shared') }
                } else if (outType.frontend) {
                    modules.find { it.name.equals('ui') }
                } else if (outType.resource) {
                    modules.find { it.name.equals('resource') }
                }

            } else if (outPurp.simulation) {
                modules.find { it.name.equals('test') }
            } else if (outPurp.test) {
                modules.find { it.name.equals('test') }
            } else {
                c.module
            }

        } else if (targetLayout.equals('shared')) {

            if (defaultPurpose) {

                if (outType.logic || outType.integ || outType.api || outType.apiImpl || outType.shared) {
                    modules.find { it.name.equals('shared') }
                } else if (outType.frontend) {
                    modules.find { it.name.equals('ui') }
                }

            } else if (outPurp.simulation) {
                modules.find { it.name.equals('test') }
            } else if (outPurp.test) {
                modules.find { it.name.equals('test') }
            }
        } else if (targetLayout.equals('single')) {
            modules.find { it.name.equals('backend') }
        } else {
            c.module
        }
    }

    Processor javaImportsPathProcessor() {
        Processor ret = new Processor(name: 'javaImportsPath')

        def nameToPackage = [:]

        ret.before = { c ->
            //add 'name' method to context object
            c.staticImports = [] as Set
            c.imports = [] as Set
            def meta = c.metaClass

            meta.addImport = { String name ->
                String pkg = nameToPackage[name]
                if (pkg) {
                    if (pkg.startsWith('static')) {
                        staticImports << pkg
                    } else {
                        if (c.subPkg || !(c.module ? c.module.ns : c.item.ns).dot != pkg) {
                            imports << "$pkg.$name"
                        }
                    }
                }
                name
            }

            meta.name = { Base base -> name(base, base.name) }

            meta.name = { Base base, String derivedName ->
                if (!nameToPackage.containsKey(derivedName)) {
                    nameToPackage[derivedName] = base.ns?.dot
                }
                addImport(derivedName)
            }

            meta.name = { String ref ->
                //static import for Tests?
                if (ref.startsWith('assert')) {
                    addImport('Assert')
                    ref
                } else {
                    if (!nameToPackage.containsKey(ref)) {
                        Base resolved = typeResolver.resolve(ref, delegate.module)
                        if (resolved) {
                            name(resolved)
                        } else {
                            throw new IllegalStateException("Can't resolve '$ref'")
                        }
                    } else {
                        addImport(ref)
                    }
                }
            }

            meta.getNameRegister << { { Base base, String derivedName -> name(base, derivedName) } }
        }

        ret.after = { Context c ->
            if (!c.error && c.className) {
                def ns = (c.module ? c.module.ns : c.item.ns)
                def subPkg = c.subPkg ? ".$c.subPkg" : ''
                def imports = c.imports.toList().sort().collect { "import $it;" }.join('\n')
                def staticImports = c.staticImports ? c.staticImports.toList().sort().collect {
                    "import $it;"
                }.join('\n') : ''
                staticImports = staticImports ? "\n$staticImports\n" : ''
                imports = imports ? "$staticImports\n$imports\n" : ''
                c.output = "package $ns.dot$subPkg;\n" + c.output.replace('{{imports}}', imports)
                if (!c.scope) {
                    c.scope = c.outputPurpose?.test ? 'test' : 'main'
                }
                Module outputModule = targetModuleResolver(c)
                def artifact = (outputModule ? "${outputModule.artifact}/" : '/')
                def path = c.src ? "${artifact}src/$c.scope/java" : "${artifact}src-gen/$c.scope/java"
                def subPath = c.subPkg ? "/$c.subPkg" : ''
                if (!c.path) {
                    c.path = "$path/$ns.path$subPath/${c.className}.java"
                }
                c.overwrite = !c.src
            }
        }
        ret
    }
}
