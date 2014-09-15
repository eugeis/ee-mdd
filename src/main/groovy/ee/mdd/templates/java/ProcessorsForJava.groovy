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
package ee.mdd.templates.java

import ee.mdd.generator.Processor
import ee.mdd.model.Element



/**
 *
 * @author Eugen Eisler
 */
class ProcessorsForJava {

  Processor javaImportsPathProcessor() {
    Processor ret = new Processor(name: 'javaImportsPath')

    def noImportTypes = [
      'int',
      'Integer',
      'long',
      'float',
      'Float',
      'double',
      'Double'
    ]

    //default types and resolved
    def nameToPackage = ['Test': 'org.junit', 'Date': 'java.util', 'Before': 'org.junit',
      'Assert': 'static junit.framework.Assert.*', 'Serializable': 'java.io', 'ApplicationScoped' : 'javax.enterprise.context.ApplicationScoped', 'Produces' : 'javax.enterprise.inject.Produces']

    ret.before = { c ->
      //add 'name' method to context object
      c.staticImports = [] as Set
      c.imports = [] as Set
      c.metaClass {

        name = { Element element ->
          if(!nameToPackage.containsKey(element.name)) {
            nameToPackage[element.name] = element.ns?.dot
          }
          addImport(element.name)
        }

        addImport = { String name ->
          String pkg = nameToPackage[name]
          if(pkg) {
            if(pkg.startsWith('static')) {
              staticImports << pkg
            } else {
              if(c.subPkg || !(c.module ? c.module.ns : c.item.ns).dot != pkg ) {
                imports << "$pkg.$name"
              }
            }
          }
          name
        }

        name = { String ref ->
          //static import for Tests?
          if(ref.startsWith('assert')) {
            addImport('Assert')
            ref
          } else {
            if(!nameToPackage.containsKey(ref)) {
              Element resolved = c.model.findRecursiveUp { ref.equals(it.name) }
              if(resolved) {
                name(resolved)
              } else {
                throw new IllegalStateException("Can't resolve '$ref' in model '$c.model'")
              }
            } else {
              addImport(ref)
            }
          }
        }
      }
    }
    ret.after = { c ->
      if (!c.error && c.className) {
        def ns = (c.module ? c.module.ns : c.item.ns)
        def subPkg = c.subPkg ? ".$c.subPkg" : ''
        def imports = c.imports.toList().sort().collect { "import $it;" }.join('\n')
        def staticImports =  c.staticImports ? c.staticImports.toList().sort().collect { "import $it;" }.join('\n') : ''
        staticImports = staticImports ? "\n$staticImports\n" : ''
        imports = imports ? "$staticImports\n$imports\n" : ''
        c.output = "package $ns.dot$subPkg;\n" + c.output.replace( '{{imports}}', imports )
        if(!c.scope) { c.scope = 'main' }
        def path = c.src ? "src/$c.scope/java" : "src-gen/$c.scope/java"
        def subPath = c.subPkg ? "/$c.subPkg" : ''
        c.path = "$path/$ns.path$subPath/${c.className}.java"
        c.overwrite = !c.src
      }
    }
    ret
  }
}
