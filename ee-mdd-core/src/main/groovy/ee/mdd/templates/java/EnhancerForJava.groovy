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
import ee.mdd.model.component.Body
import ee.mdd.model.component.Count
import ee.mdd.model.component.DataTypeOperation
import ee.mdd.model.component.Delete
import ee.mdd.model.component.Entity
import ee.mdd.model.component.Exist
import ee.mdd.model.component.Find
import ee.mdd.model.component.Literal
import ee.mdd.model.component.LogicUnit
import ee.mdd.model.component.Manager
import ee.mdd.model.component.MetaAttribute
import ee.mdd.model.component.Prop

/**
 *
 * @author Eugen Eisler
 * @author Niklas Cappelmann
 */
class EnhancerForJava {
  private static final Object[] EMPTY_ARGUMENTS = {
  }

  private static String resolveMacro(Context c, String value) {
    c.macros.generate("${value.substring(1, value.size())}", c)
  }

  static void enhanceClasses() {
    ExpandoMetaClass.enableGlobally()

    def properties = Collections.synchronizedMap([:])
    Map<String, String> typeToTestValue = [String: '\"TestString\"', Long: 'Long.value(1)', long: '1L',
      Integer: 'Integer.value(1)', int: '1', Date: 'new Date()', boolean: 'true', Boolean: 'Boolean.TRUE']

    Element.metaClass {
    }

    Entity.metaClass {

      metasForEntity << { Context c ->
        def key = System.identityHashCode(delegate) + 'metasForEntity'
        if(!properties.containsKey(key)) {
          Entity entity = delegate
          ModelBuilder builder = entity.component.builder
          def metasForEntity = []
          metasForEntity << builder.meta(type: 'Entity')
          if(entity.metas) {
            metasForEntity.addAll(entity.metas)
          }

          def namedQueries = builder.meta(type: 'NamedQueries', multi: true, value: [])

          if(entity.manager && entity.manager.operations) {
            namedQueries.value.addAll(entity.manager.finderNamedQuery(c))
            namedQueries.value.addAll(entity.manager.counterNamedQuery(c))
            namedQueries.value.addAll(entity.manager.existerNamedQuery(c))
            namedQueries.value.addAll(entity.manager.deleterNamedQuery(c))
          }
          metasForEntity << namedQueries

          def table = builder.meta(type: 'Table', value: [:])
          table.value['name'] = entity.name+'.TABLE'
          if(entity.indexes != null) {
            table.value['indexes'] = entity.indexesForMeta(c)
          }

          metasForEntity << table

          properties[key] = metasForEntity
        }
        properties[key]
      }

      //Replace underscored with appropriate sql name
      indexesForMeta << { Context c ->
        def key = System.identityHashCode(delegate) + 'indexesForMeta'
        if(!properties.containsKey(key)) {
          ModelBuilder builder = delegate.component.builder
          String newLine = System.properties['line.separator']
          def ret = '{'+newLine
          def separator = ', '+newLine
          delegate.indexes.each  {
            def index = builder.meta(type: 'Index', value: [:])
            String prefix = it.underscored.takeWhile { it != '_' } + '_'
            index.value['name'] = it.underscored
            index.value['columnList'] = it.underscored-prefix
            ret += separator+index.annotation(c)
          }
          ret += ' }'
          properties[key] = ret-separator
        }
        properties[key]
      }
    }

    Manager.metaClass {

      finderNamedQuery << { Context c ->
        if(delegate.finders != null) {
          def finderQueries = []
          ModelBuilder builder = c.item.component.builder
          delegate.finders.each { finder ->
            def namedQuery = builder.meta(type: 'NamedQuery', value: [:])
            namedQuery.value['name'] = c.item.name+'.'+finder.operationName
            namedQuery.value['query'] = "\"SELECT e FROM ${c.item.n.cap.entity} e WHERE ( ${finder.propWhere} )\""
            finderQueries << namedQuery
          }
          finderQueries
        }
      }

      counterNamedQuery << { Context c ->
        if(delegate.counters != null) {
          def counterQueries = []
          ModelBuilder builder = c.item.component.builder
          delegate.counters.each { counter ->
            def namedQuery = builder.meta(type: 'NamedQuery', value: [:])
            namedQuery.value['name'] = c.item.name+'.'+counter.operationName
            namedQuery.value['query'] = "\"SELECT COUNT(e) FROM ${c.item.n.cap.entity} e WHERE ( ${counter.propWhere} )\""
            counterQueries << namedQuery
          }
          counterQueries
        }
      }

      existerNamedQuery << { Context c ->
        if(delegate.exists != null) {
          def existsQueries = []
          ModelBuilder builder = c.item.component.builder
          delegate.exists.each { exist ->
            def namedQuery = builder.meta(type: 'NamedQuery', value: [:])
            namedQuery.value['name'] = c.item.name+'.'+exist.operationName
            namedQuery.value['query'] = "\"SELECT COUNT(e) FROM ${c.item.n.cap.entity} e WHERE ( ${exist.propWhere} )\""
            existsQueries << namedQuery
          }
          existsQueries
        }
      }

      deleterNamedQuery << { Context c ->
        if(delegate.deleters != null) {
          def deleterQueries = []
          ModelBuilder builder = c.item.component.builder
          delegate.deleters.each { deleter ->
            def namedQuery = builder.meta(type: 'NamedQuery', value: [:])
            namedQuery.value['name'] = c.item.name+'.'+deleter.operationName
            namedQuery.value['query'] = "\"DELETE FROM ${c.item.n.cap.entity} e WHERE ( ${deleter.propWhere} )\""
            deleterQueries << namedQuery
          }
          deleterQueries
        }
      }
    }

    DataTypeOperation.metaClass {

      getPropWhere << {
        ->
        String separator = ' AND '
        def ret = delegate.params.collect { param ->
          param.prop.multi?"e.$param.prop.name IN :${param.name}s":"e.$param.prop.name = :$param.name"
        }.join(separator)
        ret
      }

      getOperationName << {
        ->
        def ret = ''
        def separator = '_AND_'
        if(Find.isInstance(delegate)) {
          ret = 'FIND_BY_'
        } else if (Count.isInstance(delegate)) {
          ret = 'COUNT_BY_'
        } else if (Delete.isInstance(delegate)) {
          ret = 'DELETE_BY_'
        } else if (Exist.isInstance(delegate)) {
          ret = 'EXISTS_BY_'
        }
        delegate.params.each { param ->
          ret += separator+param.prop.underscored
        }
        ret-separator
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
          ret = value
        } else {
          if(delegate.value[0].equals('#')) {
            ret = "this.$delegate.prop.uncap = "+resolveMacro(c, value)
          } else {
            ret = "this.$delegate.prop.uncap = "+value
          }
        }
      }

    }

    Body.metaClass {

      resolveBody << { Context c ->
        def ret
        def body = "$delegate.body"
        if (delegate.body == null) {
          ret = ''
        } else {
          if(delegate.body[0].equals('#')) {
            resolveMacro(c, body)
          } else {
            ret = body
          }
        }
      }
    }

    MetaAttribute.metaClass {

      annotation << { Context c ->
        def key = System.identityHashCode(delegate) + 'annotation'
        if(!properties.containsKey(key)) {
          String newLine = System.properties['line.separator']
          def ret = "@${c.name(delegate.type)}"
          if(delegate.multi && delegate.value) {
            ret += '({'
            delegate.value.each { ret += "${newLine}${it.annotation(c)}" }
            ret += '})'
          } else if(delegate.value) {
            if(Map.isInstance(delegate.value)) {
              if(delegate.type.cap == 'NamedQuery') {
                ret += '(' + delegate.value.collect { k, v -> "$k = $v" }.join(', '+newLine+'            ') + ')'
              } else {
                ret += '(' + delegate.value.collect { k, v -> "$k = $v" }.join(', ') + ')'
              }
            } else {
              ret += "($delegate.value)"
            }
          } else if(delegate.multi) {
            ret += '({'+newLine+'})'
          }
          properties[key] = ret
        }
        properties[key]
      }

    }


  }
}

