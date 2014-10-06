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

import ee.mdd.builder.GeneratorBuilder
import ee.mdd.generator.Generator
import ee.mdd.model.component.Controller
import ee.mdd.model.component.Entity
import ee.mdd.model.component.EnumType
import ee.mdd.model.component.Service



/**
 *
 * @author Eugen Eisler
 */
class TemplatesForJava {

  static Generator build() {
    def model = new GeneratorBuilder().generator('javaEe') {
      //model
      items ('modelApi',
      query: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {

        template('ifc', body: '''<% c.serializable = true; c.className = "${item.cap}Base" %>${macros.generate('ifc', c)}''')
        template('ifcExtends', body: '''${macros.generate('ifcExtends', c)}''')
      }

      items ('modelImpl',
      query: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'impl' ] ) } ) {

        template('impl', body: '''<% c.virtual = true; c.metas = item.metas; c.serializable = true; c.className = item.n.cap.implBase %>${macros.generate('impl', c)}''')
        template('implExtends', body: '''<% c.serializable = true; c.className = item.n.cap.impl %>${macros.generate('implExtends', c)}''')

      }

      items ('modelTest',
      query: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module, subPkg: 'impl', scope: 'test'] ) } ) {

        template('test', body: '''<% c.virtual = true; c.className = "${item.n.cap.test}Base"; c.itemInit = "new $item.n.cap.impl()" %>${macros.generate('test', c)}''')
        template('testExtends', body: '''<% c.className = item.n.cap.test %>${macros.generate('testExtends', c)}''')

      }

      items ('enum',
      query: { c -> c.model.findAllRecursiveDown( { EnumType.isInstance(it) }) },
      before: { c -> def enumType = c.item; c.putAll( [ component: enumType.component, module: enumType.module, enumType: enumType ] ) } ) {

        template('enum', body: '''${macros.generate('enum', c)}''')
      }

      items ('enumTest',
      query: { c -> c.model.findAllRecursiveDown( { EnumType.isInstance(it) }) },
      before: { c -> def enumType = c.item; c.putAll( [ component: enumType.component, module: enumType.module, enumType: enumType, scope: 'test' ] ) } ) {

        template('testEnum', body: '''<% c.className = "${item.n.cap.test}Base" %>${macros.generate('testEnum', c)}''')
        template('testEnumExtends', body: '''<% c.className = item.n.cap.test %>${macros.generate('testExtends', c)}''')
      }


      //logic
      items ('logicApi',
      query: { c -> c.model.findAllRecursiveDown( { Controller.isInstance(it) || Service.isInstance(it) }) },
      before: { c -> c.putAll( [ component: c.item.component, module: c.item.module ] ) } ) {

        template('ifc', body: '''<% c.className = "${item.cap}Base" %>${macros.generate('ifc', c)}''')
        template('ifcExtends', body: '''${macros.generate('ifcExtends', c)}''')
      }
    }
  }
}