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

import javafx.beans.binding.*
import javafx.beans.property.*
import javafx.collections.*
import javafx.scene.control.*
import ee.mdd.builder.ModelBuilder
import ee.mdd.generator.Context
import ee.mdd.model.Element
import ee.mdd.model.component.Attribute
import ee.mdd.model.component.Entity
import ee.mdd.model.component.Literal
import ee.mdd.model.component.LogicUnit
import ee.mdd.model.component.Manager
import ee.mdd.model.component.MetaAttribute
import ee.mdd.model.component.Prop
import ee.mdd.templates.java.model.annotations.MetaAttributeNamedQuery

/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */
class EnhancerForJava {
  private static final Object[] EMPTY_ARGUMENTS = {
  }

  static void enhanceClasses() {
    ExpandoMetaClass.enableGlobally()

    def properties = Collections.synchronizedMap([:])
    Map<String, String> typeToTestValue = [String: '\"TestString\"', Long: 'Long.value(1)', long: '1L',
      Integer: 'Integer.value(1)', int: '1', Date: 'new Date()', boolean: 'true', Boolean: 'Boolean.TRUE']

    Element.metaClass {
    }

    Entity.metaClass {

      getMetasForEntity << {
        ->
        def key = System.identityHashCode(delegate) + 'metasForEntity'
        if(!properties.containsKey(key)) {
          Entity entity = delegate
          def metasForEntity = properties[key] = []
          if(entity.metas) {
            metasForEntity.addAll(entity.metas)
          }
          ModelBuilder builder = entity.component.builder
          metasForEntity << builder.meta(type: 'Entity')

          def namedQueries = builder.meta(type: 'NamedQueries', multi: true, value: [])

          namedQueries.value.addAll(entity.manager.finderNamedQuery)
          namedQueries.value.addAll(entity.manager.counterNamedQuery)
          namedQueries.value.addAll(entity.manager.existerNamedQuery)
          namedQueries.value.addAll(entity.manager.deleterNamedQuery)

          metasForEntity << namedQueries
          properties[key] = metasForEntity
        }
        properties[key]
      }
    }

    Manager.metaClass {

      finderNamedQuery << {
        ->
        if(delegate.finders != null) {
          def finderQueries = []
          delegate.finders.each { finder ->
            def namedQuery = new MetaAttributeNamedQuery(type: 'NamedQuery', value: [:])
            namedQuery.name = entity.name+'.'+finder.underscored
            namedQuery.query = "\"SELECT e FROM ${entity.n.cap.entity} e WHERE ( "+delegate.getPropWhere+"\" )"
            finderQueries << namedQuery
          }
          finderQueries
        }
      }

      counterNamedQuery << {
        ->
        if(delegate.counters != null) {
          def counterQueries = []
          delegate.counters.each { counter ->
            def namedQuery = new MetaAttributeNamedQuery(type: 'NamedQuery', value: [:])
            namedQuery.name = entity.name+'.'+finder.underscored
            namedQuery.query = "\"SELECT COUNT(e) FROM ${entity.n.cap.entity} e WHERE ( "+delegate.getPropWhere+"\")"
            counterQueries << namedQuery
          }
          counterQueries
        }
      }

      existerNamedQuery << {
        ->
        if(delegate.exists != null) {
          def existsQueries = []
          delegate.exists.each { exist ->
            def namedQuery = new MetaAttributeNamedQuery(type: 'NamedQuery', value: [:])
            namedQuery.name = entity.name+'.'+finder.underscored
            namedQuery.query = "\"SELECT COUNT(e) FROM ${entity.n.cap.entity} e WHERE ( "+delegate.getPropWhere+"\")"
            existsQueries << namedQuery
          }
          existsQueries
        }
      }

      deleterNamedQuery << {
        ->
        if(delegate.deleters != null) {
          def deleterQueries = []
          delegate.deleters.each { deleter ->
            def namedQuery = new MetaAttributeNamedQuery(type: 'NamedQuery', value: [:])
            namedQuery.name = entity.name+'.'+finder.underscored
            namedQuery.query = "\"DELETE FROM ${entity.n.cap.entity} e WHERE ( "+delegate.getPropWhere+"\")"
            deleterQueries << namedQuery
          }
          deleterQueries
        }
      }

      getPropWhere << {
        ->
        String seperator = 'AND'
        ret = delegate.props.collect { prop ->
          prop.multi?"e.$prop.name IN :${prop.name}s":"e.$prop.name = :$prop.name"
        }.join(separator)
      }
    }

    Literal.metaClass {

      getIs << {
        ->
        def key = System.identityHashCode(delegate) + 'is'
        if(!properties.containsKey(key)) {
          properties[key] = "is$delegate.cap()"
        }
        properties[key]
      }
    }

    Prop.metaClass {

      getGetter << {
        ->
        def key = System.identityHashCode(delegate) + 'getter'
        if(!properties.containsKey(key)) {
          properties[key] = "get$delegate.cap()"
        }
        properties[key]
      }

      getSetter << {
        ->
        def key = System.identityHashCode(delegate) + 'setter'
        if(!properties.containsKey(key)) {
          properties[key] = "set$delegate.cap($delegate.type.name $delegate.uncap)"
        }
        properties[key]
      }

      getCall << {
        ->
        def key = System.identityHashCode(delegate) + 'call'
        if(!properties.containsKey(key)) {
          properties[key] = "set$delegate.cap($delegate.uncap)"
        }
        properties[key]
      }

      getTestable << {
        ->
        def key = System.identityHashCode(delegate) + 'testable'
        if(!properties.containsKey(key)) {
          properties[key] = typeToTestValue.containsKey(delegate.type.name)
        }
        properties[key]
      }

      getTestValue << {
        ->
        def key = System.identityHashCode(delegate) + 'testValue'
        if(!properties.containsKey(key)) {
          properties[key] = typeToTestValue.get(delegate.type.name)
        }
        properties[key]
      }
    }

    LogicUnit.metaClass {

      getCall << {
        ->
        def key = System.identityHashCode(delegate) + 'call'
        if(!properties.containsKey(key)) {
          properties[key] = delegate.paramsCustom.collect { it.uncap }.join(', ')
        }
        properties[key]
      }

      getSignature << {
        ->
        def key = System.identityHashCode(delegate) + 'signature'
        if(!properties.containsKey(key)) {
          properties[key] = delegate.paramsCustom.collect { it.signature }.join(', ')
        }
        properties[key]
      }

      signature << { Context c ->
        //register usage of the type, in order to calculate imports, etc.
        delegate.params.each { c.name(it.type) }
        delegate.signature
      }

      getParamsName << {
        ->
        def key = System.identityHashCode(delegate) + 'paramsName'
        if(!properties.containsKey(key)) {
          properties[key] = delegate.paramsCustom.collect { it.cap }.join('And')
        }
        properties[key]
      }
    }

    Attribute.metaClass {

      getSignature << {
        ->
        def key = System.identityHashCode(delegate) + 'signature'
        if(!properties.containsKey(key)) {
          //use 'name', because of primitive types
          properties[key] = "$delegate.type.name $delegate.uncap"
        }
        properties[key]
      }

      signature << { Context c ->
        //register usage of the type, in order to calculate imports, etc.
        c.name(delegate.type)
        delegate.signature
      }

      resolveValue << { Context c ->
        def ret
        def value = "$delegate.value"
        if (delegate.prop == null) {
          ret = "$delegate.value"
        } else {
          if(delegate.value[0].equals('#')) {
            ret = "this.$delegate.prop.uncap = ${c.macros.generate("${value.substring(1, value.size())}", c)}"
        } else {
          ret = "this.$delegate.prop.uncap = $delegate.value"
        }
      }
      ret
    }

  }

  MetaAttribute.metaClass {

    annotation << { Context c ->
      def key = System.identityHashCode(delegate) + 'annotation'
      if(!properties.containsKey(key)) {
        def ret = "@${c.name(delegate.type)}"
        if(delegate.multi && delegate.value) {
          String newLine = System.properties['line.separator']
          ret += ' {'
          delegate.value.each { ret += "${newLine}${it.annotation(c)}" }
          ret += '}'
        } else if(delegate.value) {
          if(Map.isInstance(delegate.value)) {
            ret += '(' + delegate.value.collect { k, v -> "$k = $v" }.join(', ') + ')'
          } else {
            ret += "($delegate.value)"
          }
        }
        properties[key] = ret
      }
      properties[key]
    }

  }

}
}
