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
package ee.mdd.templates.js

import ee.mdd.GeneratorBuilder;
import ee.mdd.generator.Generator
import ee.mdd.model.component.Entity
import ee.mdd.model.component.EnumType

/**
 *
 * @author Eugen Eisler
 */
class TemplatesForJs {

  static Generator build() {
    def ret = new GeneratorBuilder().generator('model') {

      templates ('api',
      items: { c -> c.model.findAllRecursiveDown( { Entity.isInstance(it) }) },
      before: { c -> def entity = c.item; c.putAll( [ component: entity.component, module: entity.module, entity: entity, subPkg: 'impl' ] ) } ) {

        template('impl', body: '''<% c.virtual=true; c.serializable=true; c.className="${entity.name}Base" %>${macros.generate('impl', c)}''')
        template('implExtends', body: '''<% c.serializable=true; c.className="${entity.name}" %>${macros.generate('implExtends', c)}''')
      }

      templates ('enum',
      items: { c -> c.model.findAllRecursiveDown( { EnumType.isInstance(it) }) },
      before: { c -> def enumType = c.item; c.putAll( [ component: enumType.component, module: enumType.module, enumType: enumType ] ) } ) {

        template('enum', body: '''${macros.generate('enum', c)}''')
      }
    }
  }
}