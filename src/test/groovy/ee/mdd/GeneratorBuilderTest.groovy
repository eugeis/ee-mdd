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
package ee.mdd

import ee.mdd.builder.GeneratorBuilder;
import ee.mdd.generator.Context;
import ee.mdd.generator.Generator;
import ee.mdd.model.component.Entity


class GeneratorBuilderTest extends GroovyTestCase {

	void testComponentChildren() {
		def templates = build()

		assertNotNull templates
		log.info "Build templates model $templates"
	}

	static Generator build() {
		def ret = new GeneratorBuilder().generator('common') {
			items ('entity',
			query: { Context c -> c.model.findRecursiveByType(Entity) },
			before: { Context c -> Entity entity = c.item; c.putAll( [ component: entity.component, module: entity.module, entity: entity ] ) } ) {
				template('ifc', body: '''<% c.src=true; c.className=entity.name %>
{{imports}}
public interface $c.className {<% entity.props.each { prop -> %>
  ${c.name(prop.type)} $prop.getter;<% } %>
}''')
//				template('test', body: '''Test entity $entity.name <% c.path="${model.name}-test.txt" %>''')
//				template('model', body: '''entity = $entity.name, model = $model.name <% c.path="${model.name}-model.txt" %> ''')
//				template('templateProcessor',
//				body: '''<% c.hello = 'Hello' %>model = $model.name, entity = $entity.name<% c.path="${model.name}-templateProcessor.txt" %>''') {
//					processor('test', after: { c -> c.output = "$c.hello $c.output" } )
//				}
//				template('useMacros', body: '''${macros.generate('header', c)}entity = $entity.name, model = $model.name <% c.path="${model.name}-useMacros.txt" %> ''')
			}
		}
		ret
	}
}
