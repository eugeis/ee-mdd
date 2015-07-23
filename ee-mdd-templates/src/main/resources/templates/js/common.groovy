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

/**
 *
 * @author Eugen Eisler
 */

templates ('common') {

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

  templates ('firstTestWithAngular',
	  items: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
	  context: { c -> def entity = c.item; c.putAll( [ component: entity.component, module: entity.module, entity: entity, subPkg: 'impl' ] ); c.filepath = 'ee-mdd_example-ui' } ) {

		  template('htmlFile', body: '''<% c.path = "${c.filepath}/${item.name}.html" %>
			${macros.generate('myhtmlheadermacro', c)}
			${macros.generate('myhtmlbodymacro', c)}
			${macros.generate('myhtmlfootermacro', c)}''')

		  template('angularFile', appendName: true,  body: '''<% c.path = "${c.filepath}/${item.name}.js" %>
			${macros.generate('myangularmacro', c)}''')

		  template('cssFile', body: '''<% c.path = "${c.filepath}/${item.name}.css" %>
			${macros.generate('mycssmacro', c)}''')

		  //		template('javascriptBase', body: '''<% c.path = "${c.filepath}/${item.name}Base.js" %>
		//			${macros.generate('myjavascriptmacro', c)}''')
	  }
}
