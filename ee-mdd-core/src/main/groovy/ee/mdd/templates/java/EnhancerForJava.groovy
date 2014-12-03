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

import ee.mdd.builder.ModelBuilder
import ee.mdd.generator.Context
import ee.mdd.model.Element
import ee.mdd.model.component.Attribute
import ee.mdd.model.component.BasicType
import ee.mdd.model.component.Body
import ee.mdd.model.component.CompilationUnit
import ee.mdd.model.component.Count
import ee.mdd.model.component.DataTypeOperation
import ee.mdd.model.component.Delete
import ee.mdd.model.component.Entity
import ee.mdd.model.component.Exist
import ee.mdd.model.component.Find
import ee.mdd.model.component.Index
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

    CompilationUnit.metaClass {

      getIdProp << {
        ->
        def key = System.identityHashCode(delegate) + 'idProp'
        if(!properties.containsKey(key)) {
          def ret = delegate.props.find { it.primaryKey }
          if(!ret && delegate.superUnit) {
            ret = delegate.superUnit.idProp
          }
          properties[key] = ret
        }
        properties[key]
      }
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
          if(!delegate.virtual) {
            def table = builder.meta(type: 'Table', value: [:])
            table.value['name'] = c.className+'.TABLE'
            def indexes = entity.indexesForMeta(c)
            if(indexes != null) {
              table.value['indexes'] = indexes
            }
            metasForEntity << table
          }
          properties[key] = metasForEntity
        }
        properties[key]
      }

      indexesForMeta << { Context c ->
        def key = System.identityHashCode(delegate) + 'indexesForMeta'
        if(!properties.containsKey(key)) {
          ModelBuilder builder = delegate.component.builder
          String newLine = System.properties['line.separator']
          def ret = '{'+newLine
          def propIndex
          def index
          def separator = ', '+newLine

          delegate.props.each {
            propIndex = it.propIndex(c)
            if(propIndex) {
              ret += separator+'    '+propIndex.annotation(c)
            }
          }
          delegate.indexes.each  {
            index = it.metaIndex(c)
            if(index) {
              ret += separator+'    '+index.annotation(c)
            }
          }
          ret += '}'
          if(propIndex || index) {
            properties[key] = ret-separator
          }
        }
        properties[key]
      }

      getSqlName << {
        ->
        def key = System.identityHashCode(delegate) + 'sqlName'
        if(!properties.containsKey(key)) {
          def ret = delegate.underscored.replaceAll(/(?<!^)(?<!_)[QEUIOAJY]/, '')
          ret = ret.replaceAll(/(\w)\1+/, '$1')
          properties[key] = ret
        }
        properties[key]
      }

      getJpaConstants << {
        ->
        def key = System.identityHashCode(delegate) + 'jpaConstants'
        if(!properties.containsKey(key)) {
          String newLine = System.properties['line.separator']
          def ret = newLine
          def manager = delegate.manager
          if(!delegate.virtual) {
            ret = "public static final String TABLE = \"${delegate.sqlName}\";"+newLine
          }
          ret += newLine
          delegate.props.each { prop ->
            if(!delegate.virtual || (delegate.virtual && !prop.multi)) {
              ret += "  public static final String COLUMN_${prop.underscored} = \"${prop.sqlName}\";"+newLine
            }
          }
          ret += newLine
          if(manager && !delegate.virtual) {
            if(manager.finders != null) {
              manager.finders.each {
                def opName = it.operationName
                ret += "  public static final String $opName = \"${delegate.sqlName}.$opName\";"+newLine
              }
            }
            if(manager.counters != null) {
              manager.counters.each {
                def opName = it.operationName
                ret += "  public static final String $opName = \"${delegate.sqlName}.$opName\";"+newLine
              }
            }
            if(manager.exists != null) {
              manager.exists.each {
                def opName = it.operationName
                ret += "  public static final String $opName = \"${delegate.sqlName}.$opName\";"+newLine
              }
            }
            if(manager.deleters != null) {
              manager.deleters.each {
                def opName = it.operationName
                ret += "  public static final String $opName = \"${delegate.sqlName}.$opName\";"+newLine
              }
            }
          }
          properties[key] = ret
        }
        properties[key]
      }
    }

    Index.metaClass {

      metaIndex << { Context c ->
        ModelBuilder builder = c.item.component.builder
        def index = delegate
        def metaIndex = builder.meta(type: 'Index', value: [:])
        def sqlNames = []
        index.props.each { sqlNames << it.sqlName }
        def columns = sqlNames.join(', ')
        def indexName =  index.props.collect { it.sqlName }.join('_')
        metaIndex.value['name'] = "\"$indexName\""
        metaIndex.value['columnList'] = "\"$columns\""
        if(index.unique) {
          metaIndex['unique'] = true
        }
        metaIndex
      }
    }

    Manager.metaClass {

      finderNamedQuery << { Context c ->
        if(delegate.finders != null) {
          def finderQueries = []
          ModelBuilder builder = c.item.component.builder
          delegate.finders.each { finder ->
            def namedQuery = builder.meta(type: 'NamedQuery', value: [:])
            namedQuery.value['name'] = c.className+'.'+finder.operationName
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
            namedQuery.value['name'] = c.className+'.'+counter.operationName
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
            namedQuery.value['name'] = c.className+'.'+exist.operationName
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
            namedQuery.value['name'] = c.className+'.'+deleter.operationName
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

      getSqlName << {
        ->
        def key = System.identityHashCode(delegate) + 'sqlName'
        if(!properties.containsKey(key)) {
          def ret = delegate.underscored.replaceAll(/(?<!^)(?<!_)[QEUIOAJY]/, '')
          ret = ret.replaceAll(/(\w)\1+/, '$1')
          properties[key] = ret
        }
        properties[key]
      }

      propMapping << { Context c ->
        def key = System.identityHashCode(delegate) + 'propMapping'
        if(!properties.containsKey(key)) {
          ModelBuilder builder = c.item.component.builder
          def prop = delegate
          def propMapping = []
          c.propMeta = true
          if(!c.item.virtual && delegate.primaryKey) {
            propMapping << builder.meta(type: 'Column', value: ['name':"\"$prop.sqlName\""])
            propMapping << builder.meta(type: 'Id')
            if(!c.item.manualId) {
              def generator = c.item.idGeneratorName
              if(!generator) {
                generator = "${c.item.model.key.toUpperCase()}_${c.item.sqlName}_SEQ"
              }
              propMapping << builder.meta(type: 'GeneratedValue', value: ['strategy':"${c.name('GenerationType')}"+'.TABLE', 'generator':"\"$generator\""])
              propMapping << builder.meta(type: 'TableGenerator', value: ['name':"\"$generator\"", 'table':"\"SEQUENCER\""])
            }
          } else if(delegate.type instanceof Entity) {
            propMapping.addAll(delegate.entityPropMapping(c))
          } else {
            propMapping.addAll(delegate.jpaPropMapping(c))
          }
          properties[key] = propMapping
        }
        properties[key]
      }

      entityPropMapping << { Context c ->
        def key = System.identityHashCode(delegate) + 'entityPropMapping'
        if(!properties.containsKey(key)) {
          ModelBuilder builder = c.item.component.builder
          String newLine = System.properties['line.separator']
          def prop = delegate
          def opposite = prop.opposite(c)
          def currentParent = prop.parent
          def metas = []
          def association
          if(opposite) {
            if(prop.multi) {
              if(opposite.multi) {
                association = builder.meta(type: 'ManyToMany')
                association.value = ['mappedBy' : "\"$prop.opposite\""]
              } else {
                association = builder.meta(type: 'OneToMany')
                association.value = ['cascade' : "${c.name('CascadeType')}"+'.ALL', 'mappedBy' : "\"$prop.opposite\"", 'orphanRemoval' : true]
              }
            } else {
              if(opposite.multi) {
                association = builder.meta(type: 'ManyToOne')
              } else if(prop.owner) {
                association = builder.meta(type: 'OneToOne')
                association.value = ['cascade' : "${c.name('CascadeType')}"+'.PERSIST', 'mappedBy' : "\"$prop.opposite\""]
              } else {
                association = builder.meta(type: 'OneToOne')
                association.value = ['fetch' : "${c.name('FetchType')}"+'.LAZY']
              }
              metas << association
              if (opposite.multi || !prop.owner) {
                def joinColumn = builder.meta(type : 'JoinColumn')
                joinColumn.value =['name' : "COLUMN_$prop.underscored"]
                metas << joinColumn
              }
            }
          } else {
            if(prop.multi) {
              if(prop.mm) {
                association = builder.meta(type: 'ManyToMany')
                association.value = ['cascade' : "${c.name('CascadeType')}"+'.ALL']
              } else {
                association = builder.meta(type: 'OneToMany')
                association.value = ['cascade' : "${c.name('CascadeType')}"+'.ALL']
              }
              def joinTable = builder.meta(type: 'JoinTable', value: [:])
              joinTable.value['name'] =  "${currentParent.sqlName}_${prop.sqlName}"
              if(prop.type) {
                def invJoinColumn = builder.meta(type: 'JoinColumn')
                invJoinColumn.value = ['name' : "${prop.type.sqlName}_ID"]
                joinTable.value['inverseJoinColumns'] = invJoinColumn.annotation(c)
              }
              def joinColumn = builder.meta(type: 'JoinColumn')
              joinColumn.value = ['name' : "${currentParent.sqlName}_ID"]
              joinTable.value['joinColums'] = joinColumn.annotation(c)
              metas << joinTable
            } else {
              association = builder.meta(type: 'ManyToOne')
              def joinColumn = builder.meta(type: 'JoinColumn')
              joinColumn.value = ['name' : 'COLUMN_${prop.underscored}']
              metas << joinColumn
            }
            metas << association
          }
          properties[key] = metas
        }
        properties[key]
      }

      jpaPropMapping << { Context c ->
        def key = System.identityHashCode(delegate) + 'entityPropMapping'
        if(!properties.containsKey(key)) {
          ModelBuilder builder = c.item.component.builder
          String newLine = System.properties['line.separator']
          def prop = delegate
          def currentParent = prop.parent
          def metas = []
          if(prop.type.name.equals('Date')) {
            metas << builder.meta(type: 'Temporal', value: "${c.name('TemporalType')}"+'.TIMESTAMP')
          } else if(prop.type instanceof Enum) {
            metas << builder.meta(type: 'Enumerated', value: "${c.name('EnumType')}"+'.STRING')
          } else if(prop.type instanceof BasicType) {
            if(!prop.multi) {
              metas << builder.meta(type: 'Embedded')
              def attrOverrides = builder.meta(type: 'AttributeOverrides', multi: true, value: [])
              prop.type.props.each {
                def attrOverride = builder.meta(type: 'AttributeOverride')
                attrOverride.value = ['name' : "$it.underscored"]
                attrOverrides.value << attrOverride
              }
              attrOverrides.value.addAll()
            } else {
              metas << builder.meta(type: 'Embedded')
            }
          } else if (prop.lob) {
            metas << builder.meta(type: 'Lob')
          }

          if(prop.multi) {
            metas << builder.meta(type: 'ElementCollection', value: ['fetch' : "${c.name('FetchType')}"+'.EAGER'])
            def joinColum = builder.meta(type: 'JoinColumn', value: ['name' : "\"${currentParent.sqlName}_ID\""])
            metas << builder.meta(type: 'CollectionTable', value: ['name' : "\"${currentParent.sqlName}_${prop.sqlName}\"", 'joinColumns' : "${joinColum.annotation(c)}"])
          } else if (!(prop.type instanceof BasicType)) {
            metas << builder.meta(type:'Column', value: ['name' : "COLUMN_${prop.underscored}"])
          }
          properties[key] = metas
        }
        properties[key]
      }

      opposite << { Context c ->
        def module = c.item.module
        //def entity = module.entities.find { it.name == delegate.type.name }
        def entity = delegate.type
        def ret = entity.props.find { it.name == delegate.opposite }
        ret
      }

      propIndex << { Context c ->
        ModelBuilder builder = c.item.component.builder
        def prop = delegate
        def index
        Boolean manyToOne
        if(prop.type instanceof Entity && !prop.multi && !prop.opposite)
          manyToOne = true
        if(!prop.primaryKey && (prop.index || prop.unique || manyToOne)) {
          index =  builder.meta(type: 'Index', value: [:])
          index.value['name'] = "\"${c.item.sqlName}_$prop.sqlName\""
          if(prop.unique) {
            index.value['unique'] = true
          }
          index.value['columnList'] = "\"$prop.sqlName\""
        }
        index
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
          def ret = ''
          if(c.propMeta) {
            ret = "  @${c.name(delegate.type)}"
          } else {
            ret = "@${c.name(delegate.type)}"
          }
          if(delegate.multi && delegate.value) {
            ret += '({'
            ret += delegate.value.collect { '\n    '+it.annotation(c) }.join(', ')
            ret += '})'
          } else if(delegate.value) {
            if(Map.isInstance(delegate.value)) {
              if(delegate.type.cap == 'NamedQuery' || delegate.type.cap == 'JoinTable') {
                ret += '(' + delegate.value.collect { k, v -> "$k = $v" }.join(', '+newLine+'                ') + ')'
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

