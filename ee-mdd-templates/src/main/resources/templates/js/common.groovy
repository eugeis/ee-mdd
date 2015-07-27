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

  templates ('api',
  items: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
  context: { c -> def entity = c.item; c.putAll( [ component: entity.component, module: entity.module, entity: entity, subPkg: 'impl' ] ) } ) {

    template('impl', body: '''<% c.virtual=true; c.serializable=true; c.className="${entity.name}Base" %>${macros.generate('impl', c)}''')
    template('implExtends', body: '''<% c.serializable=true; c.className="${entity.name}" %>${macros.generate('implExtends', c)}''')
  }

  templates ('enum',
  items: { c -> c.model.findAllRecursiveDown( { EnumType.isInstance(it) }) },
  context: { c -> def enumType = c.item; c.putAll( [ component: enumType.component, module: enumType.module, enumType: enumType ] ) } ) {

    template('enum', body: '''${macros.generate('enum', c)}''')
  }

//  templates ('firstTestWithAngular',
//	  items: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
//	  context: { c -> def entity = c.item; c.putAll( [ component: entity.component, module: entity.module, entity: entity, subPkg: 'impl' ] ); c.filepath = 'ee-mdd_example-ui' } ) {
//
//		  template('htmlFile', body: '''<% c.path = "${c.filepath}/${item.name}.html" %>
//			${macros.generate('myhtmlheadermacro', c)}
//			${macros.generate('myhtmlbodymacro', c)}
//			${macros.generate('htmlfootermacro', c)}''')
//
//		  template('angularFile', appendName: true,  body: '''<% c.path = "${c.filepath}/${item.name}.js" %>
//			${macros.generate('myangularmacro', c)}''')
//
//		  template('cssFile', body: '''<% c.path = "${c.filepath}/${item.name}.css" %>
//			${macros.generate('mycssmacro', c)}''')
//
//		  //		template('javascriptBase', body: '''<% c.path = "${c.filepath}/${item.name}Base.js" %>
//		//			${macros.generate('myjavascriptmacro', c)}''')
//	  }
//
//	  templates ('experimentalView',
//		  items: { c -> c.model.findAllRecursiveDown( { View.isInstance(it) }) },
//		  context: { c -> def view = c.item; c.putAll( [ component: view.component, module: view.module, view: view, subPkg: 'impl' ] ); c.filepath = 'ee-mdd_example-ui' } ) {
//			  template('htmlFile', body: '''<% c.path = "${c.filepath}/${item.name}.html" %>
//			<div id="$c.item.name">
//	    <% c.item.controls.each { %>
//			<% if (it.widgetType == "Button") { %>
//				<input type="button" value="$it.name"></input>
//			<% } %>
//			<% if (it.widgetType == "Table") { %>
//				<table><tr><td>$it.name</td><td>$it.name</td></tr><tr><td>$it.name</td><td>$it.name</td></tr></table>
//			<% } %>
//			<% if (it.widgetType == "TextField") { %>
//				<input id="$it.name" type="text">
//			<% } %>
//	    <% } %>
//			</div>
//	    ''')
//		  }

	  templates ('mainView',
	  items: { c -> c.model.findAllRecursiveDown( { View.isInstance(it) && it.main}) },
	  context: { c -> def view = c.item; c.putAll( [ component: view.component, module: view.module, view: view, subPkg: 'impl' ] ); c.filepath = 'ee-mdd_example-ui' } ) {
		  template('htmlFile', body: '''<% c.path = "${c.filepath}/index.html" %>\
${macros.generate('indexheader', c)}\
${macros.generate('includedviews', c)}\
<section id="${c.item.name}">${macros.generate('html',c)}</section>\
${macros.generate('htmlfootermacro', c)}''')
		  template('cssfile', body: '''<% c.path = "${c.filepath}/stylesheet.css" %>${macros.generate('stylesheet', c)} ''')
		  template('angularFile', body: '''<% c.path = "${c.filepath}/app.js" %>${macros.generate('appjs', c)}''')
		  template('tablejs', body: '''<% c.path = "${c.filepath}/TableControllerBase.js" %>${macros.generate('tableControllerBase', c)}''')
	}

	templates ('frameViews',
	items: { c -> c.model.findAllRecursiveDown( { View.isInstance(it) && !it.main}) },
	context: { c -> def view = c.item; c.putAll( [ component: view.component, module: view.module, view: view, subPkg: 'impl' ] ); c.filepath = 'ee-mdd_example-ui' } ) {
		  template('htmlFile', body: '''<% c.path = "${c.filepath}/${item.name}.html" %>${macros.generate('html',c)}''')
		  template('angularFile', body: '''<% c.path = "${c.filepath}/${item.name}.js" %>${macros.generate('modulejs', c)}''')
	  }
}
