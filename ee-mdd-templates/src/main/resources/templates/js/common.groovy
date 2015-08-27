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

import ee.mdd.model.component.Entity
import ee.mdd.model.component.EnumType
import ee.mdd.model.ui.View

/**
 *
 * @author Eugen Eisler
 */

templates ('common') {

  useMacros('macros')
  useMacros('commonMacros', '/common/macros')

//  templates ('api',
//  items: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
//  context: { c -> def entity = c.item; c.putAll( [ component: entity.component, module: entity.module, entity: entity, subPkg: 'impl' ] ) } ) {
//
//    template('impl', body: '''<% c.virtual=true; c.serializable=true; c.className="${entity.name}Base" %>${macros.generate('impl', c)}''')
//    template('implExtends', body: '''<% c.serializable=true; c.className="${entity.name}" %>${macros.generate('implExtends', c)}''')
//  }
//
//  templates ('enum',
//  items: { c -> c.model.findAllRecursiveDown( { EnumType.isInstance(it) }) },
//  context: { c -> def enumType = c.item; c.putAll( [ component: enumType.component, module: enumType.module, enumType: enumType ] ) } ) {
//
//    template('enum', body: '''${macros.generate('enum', c)}''')
//  }

  	templates ('mainView',
  		items: { c -> c.model.findAllRecursiveDown( { View.isInstance(it) && it.main}) },
  		context: { c -> def view = c.item; c.putAll( [ component: view.component, module: view.module, view: view, subPkg: 'impl' ] ); c.filepath = 'ee-mdd_example-ui' } ) {
  			template('indexhtml', body: '''<% c.path = "${c.filepath}/index.html" %>${macros.generate('indexhtml', c)}''')
  			template('appjs', body: '''<% c.path = "${c.filepath}/app.js" %>${macros.generate('appjs', c)}''')
  			template('stylecss', body: '''<% c.path = "${c.filepath}/stylesheet.css" %>${macros.generate('stylecss', c)}''')
  			template('tablehtml', body: '''<% c.path = "${c.filepath}/src-gen/templates/table.html" %>${macros.generate('tablehtml', c)}''')
  			template('tablejs', body: '''<% c.path = "${c.filepath}/src-gen/base/Table.js" %>${macros.generate('tablejs', c)}''')
  			template('dispatcherjs', body: '''<% c.path = "${c.filepath}/src-gen/base/Dispatcher.js" %>${macros.generate('dispatcherjs', c)}''')
  			template('manipulatorjs', body: '''<% c.path = "${c.filepath}/src-gen/base/Manipulator.js" %>${macros.generate('manipulatorjs', c)}''')
  			template('srcguard', body: '''<% c.path = "${c.filepath}/src/SrcIncludeGuard.js" %>${macros.generate('srcguard', c)}''')
  		}

  	templates ('frameView',
  		items: { c -> c.model.findAllRecursiveDown( { View.isInstance(it)}) },
  		context: { c -> def view = c.item; c.putAll( [ component: view.component, module: view.module, view: view, subPkg: 'impl' ] ); c.filepath = 'ee-mdd_example-ui' } ) {
 			template('framehtml', body: '''<% c.path = "${c.filepath}/src-gen/templates/${item.name}.html" %>${macros.generate('framehtml', c)}''')
  			template('framejs', body: '''<% c.path = "${c.filepath}/src-gen/scripts/${item.name}.js" %>${macros.generate('framejs', c)}''')
  			template('framesrcjs', body: '''<% c.path = "${c.filepath}/src/${item.name}.js" %>${macros.generate('framesrcjs', c)}''')
  		}
}
