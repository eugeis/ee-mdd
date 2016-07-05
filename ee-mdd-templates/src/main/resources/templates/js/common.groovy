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
import ee.mdd.model.ui.Table
import ee.mdd.model.component.Module

/**
 *
 * @author Eugen Eisler
 */

templates ('common') {

	useMacros('macros')
	useMacros('commonMacros', '/common/macros')

	//  templates ('api',
	//  items: { c -> c.model.findAllDown(Entity) },
	//  context: { c -> def entity = c.item; c.putAll( [ component: entity.component, module: entity.module, entity: entity, subPkg: 'impl' ] ) } ) {
	//
	//    template('impl', body: '''<% c.virtual=true; c.serializable=true; c.className="${entity.name}Base" %>${macros.generate('impl', c)}''')
	//    template('implExtends', body: '''<% c.serializable=true; c.className="${entity.name}" %>${macros.generate('implExtends', c)}''')
	//  }
	//
	//  templates ('enum',
	//  items: { c -> c.model.findAllDown(EnumType) },
	//  context: { c -> def enumType = c.item; c.putAll( [ component: enumType.component, module: enumType.module, enumType: enumType ] ) } ) {
	//
	//    template('enum', body: '''${macros.generate('enum', c)}''')
	//  }

	templates ('independentGeneration',
	items: { c -> c.model.findAllDown( { Module.isInstance(it) && it.name == "ui"}) },
	context: { c -> def module = c.item; c.putAll( [ component: module.component, module: module, subPkg: 'impl', project: "eeMddUi"] ); c.filepath = 'generated' } ) {
		template('indexhtml', body: '''<% c.path = "${c.filepath}/index.html" %>${macros.generate('indexhtml', c)}''')
		template('appjs', body: '''<% c.path = "${c.filepath}/app.js" %>${macros.generate('appjs', c)}''')
	}


	templates ('mainViews',
	items: { c -> c.model.findAllDown( { View.isInstance(it) && it.main}) },
	context: { c -> def view = c.item; c.putAll( [ component: view.component, module: view.module, view: view, subPkg: 'impl' ] ); c.filepath = 'generated' } ) {
		template('framehtml', body: '''<% c.main = true; c.path = "${c.filepath}/src-gen/templates/${item.name}.html" %>${macros.generate('framehtml', c)}''')
		template('framejs', body: '''<% c.main = true; c.path = "${c.filepath}/src-gen/views/${item.name}.js" %>${macros.generate('framejs', c)}''')
		template('framesrcjs', body: '''<% c.main = true; c.path = "${c.filepath}/src/${item.name}.js" %>${macros.generate('framesrcjs', c)}''')
	}

	templates ('frameViews',
	items: { c -> c.model.findAllDown( { View.isInstance(it) && !it.main}) },
	context: { c -> def view = c.item; c.putAll( [ component: view.component, module: view.module, view: view, subPkg: 'impl' ] ); c.filepath = 'generated' } ) {
		template('framehtml', body: '''<% c.path = "${c.filepath}/src-gen/templates/${item.name}.html" %>${macros.generate('framehtml', c)}''')
		template('framejs', body: '''<% c.path = "${c.filepath}/src-gen/views/${item.name}.js" %>${macros.generate('framejs', c)}''')
		template('framesrcjs', body: '''<%  %><% c.path = "${c.filepath}/src/${item.name}.js" %>${macros.generate('framesrcjs', c)}''')
	}

	templates ('tableViews',
		items: { c -> c.model.findAllDown(Table) },
		context: { c -> def control = c.item; c.filepath = 'generated' } ) {
		template('tablejs', body: '''<% c.path = "${c.filepath}/src-gen/controls/${item.view.name}${item.type.name}.js" %>${macros.generate('tablejs', c)}''')
	}
}
