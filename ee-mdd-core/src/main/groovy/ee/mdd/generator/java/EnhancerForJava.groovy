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
package ee.mdd.generator.java

import ee.mdd.ModelBuilder
import ee.mdd.generator.Context
import ee.mdd.model.Body
import ee.mdd.model.Element
import ee.mdd.model.component.Attribute
import ee.mdd.model.component.BasicType
import ee.mdd.model.component.Channel
import ee.mdd.model.component.Commands
import ee.mdd.model.component.CompilationUnit
import ee.mdd.model.component.Component
import ee.mdd.model.component.Count
import ee.mdd.model.component.DataTypeOperation
import ee.mdd.model.component.Delete
import ee.mdd.model.component.Entity
import ee.mdd.model.component.EnumType
import ee.mdd.model.component.Exist
import ee.mdd.model.component.Facade
import ee.mdd.model.component.Find
import ee.mdd.model.component.Finders
import ee.mdd.model.component.Index
import ee.mdd.model.component.Literal
import ee.mdd.model.component.LogicUnit
import ee.mdd.model.component.MetaAttribute
import ee.mdd.model.component.Operation
import ee.mdd.model.component.OperationRef
import ee.mdd.model.component.Prop
import ee.mdd.model.component.Type


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
    Map<String, String> typeToTestValue = [String: '\"TestString\"', Long: 'Long.valueOf(1)', long: '1L',
      Integer: 'Integer.valueOf(1)', int: '1', Date: 'new Date()', boolean: 'true', Boolean: 'Boolean.TRUE']

    Element.metaClass {

      isTypeEnum << {
        ->
        def key = System.identityHashCode(delegate) +'typeEnum'
        if(!properties.containsKey(key)) {
          def el = delegate
          def ret = false
          if(EnumType.isInstance(el)) {
            ret = true
          }
          properties[key] = ret
        }
        properties[key]
      }
    }

    Type.metaClass {

      isTypeProp << {
        ->
        def key = System.identityHashCode(delegate) + 'typeProp'
        if(!properties.containsKey(key)) {
          def ret = false
          if(Prop.isInstance(delegate)) {
            ret = true
          }
          properties[key] = ret
        }
        properties[key]
      }
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

      getPropsRecursive << {
        ->
        def key = System.identityHashCode(delegate) + 'propsRecursive'
        if(!properties.containsKey(key)) {
          def ret = []
          if(delegate.superUnit) {
            def superUnit = delegate.superUnit
            ret.addAll(superUnit.propsRecursive)
          }
          delegate.props.each { ret << it }
          properties[key] = ret
        }
        properties[key]
      }

      getMultiSuperProps << {
        ->
        def key = System.identityHashCode(delegate) + 'multiSuperProps'
        if(!properties.containsKey(key)) {
          def superUnit = delegate.superUnit
          def ret = superUnit ? superUnit.props.findAll{ it.multi } : []
        }
      }

      getBeanName << {
        ->
        def key = System.identityHashCode(delegate) + 'beanName'
        if(!properties.containsKey(key)) {
          def ret = "${delegate.cap}"
          if(Entity.isInstance(delegate)) {
            ret = "${delegate.n.cap.entity}"
          } else if (BasicType.isInstance(delegate)) {
            ret = "${delegate.n.cap.embeddable}"
          }
          properties[key] = ret
        }
        properties[key]
      }

      getInstancesName << {
        ->
        delegate.name[-1] == 'y' ? "${delegate.uncap[0..-2]}ies" : "${delegate.uncap}s"
      }

      getGenericsName << {
        ->
        def key = System.identityHashCode(delegate) + 'genericsName'
        if(!properties.containsKey(key)) {
          def ret = delegate.beanName
          if(delegate.generic) {
            def suffix = "<${delegate.generics.join(', ')}>"
            ret += suffix
          }
          properties[key] = ret
        }
        properties[key]
      }

      getGenericSgn << {
        ->
        def key = System.identityHashCode(delegate) + 'genericSgn'
        if(!properties.containsKey(key)) {
          def ret = (delegate.generic ? "<${delegate.generics.join(', ')}>" : '')
          properties[key] = ret
        }
        properties[key]
      }

      getSimpleGenericSgn << {
        ->
        def key = System.identityHashCode(delegate) + 'simpleGenericSgn'
        if(!properties.containsKey(key)) {
          def ret = (delegate.generic ? "${delegate.generics.join(', ')}, " : '')
          properties[key] = ret
        }
        properties[key]
      }

      getPropsForHashCode << {
        ->
        def key = System.identityHashCode(delegate) + 'propsForHashCode'
        if(!properties.containsKey(key)) {
          def ret = delegate.props.findAll{ it.hashCode }
          properties[key] = ret
        }
        properties[key]
      }

      isGeneric << {
        ->
        def key = System.identityHashCode(delegate) + 'generic'
        if(!properties.containsKey(key)) {
          def ret = false
          if(delegate.generics)
            ret = (!delegate.generics.empty ? true : false)
          properties[key] = ret
        }
        properties[key]
      }
    }

    Component.metaClass {

      getBackends << {
        ->
        def key = System.identityHashCode(delegate) + 'backends'
        if(!properties.containsKey(key)) {
          def ret = delegate.modules.findAll { m ->
            m.name.equals('backend')
          }
          properties[key] = ret
        }
        properties[key]
      }
    }



    Entity.metaClass {

      jpaMetasForEntity << { Context c ->
        def key = System.identityHashCode(delegate) + 'jpaMetasforEntity'
        if(!properties.containsKey(key)) {
          Entity entity = delegate
          ModelBuilder builder = entity.component.builder
          def jpaMetasForEntity = []
          def namedQueries = builder.meta(type: 'NamedQueries', multi: true, value: [])
          if(entity.finders) {
            namedQueries.value.addAll(entity.finders.finderNamedQuery(c))
            namedQueries.value.addAll(entity.finders.counterNamedQuery(c))
            namedQueries.value.addAll(entity.finders.existerNamedQuery(c))
          }
          if(entity.deleters) {
            namedQueries.value.addAll(entity.commands.deleterNamedQuery(c))
          }
          jpaMetasForEntity << namedQueries
          def table = builder.meta(type: 'Table', value: [:])
          table.value['name'] = c.className+'.TABLE'
          def indexes = entity.indexesForMeta(c)
          if(indexes != null) {
            table.value['indexes'] = indexes
          }
          jpaMetasForEntity << table
          properties[key] = jpaMetasForEntity
        }
        properties[key]
      }

      metasForEntity << { Context c ->
        Entity entity = delegate
        ModelBuilder builder = entity.component.builder
        def metasForEntity = []
        if(entity.metas) {
          metasForEntity.addAll(entity.metas)
        }
        if(c.className.contains('BaseEntity') && entity.base || entity.virtual) {
          metasForEntity << builder.meta(type: 'MappedSuperclass')
        } else {
          metasForEntity << builder.meta(type: 'Entity')
        }
        metasForEntity
      }

      indexesForMeta << { Context c ->
        def key = System.identityHashCode(delegate) + 'indexesForMeta'
        if(!properties.containsKey(key)) {
          ModelBuilder builder = delegate.component.builder
          String newLine = System.properties['line.separator']
          def ret = '{'+newLine
          def propIndex
          def index
          def empty = true
          def separator = ', '+newLine

          delegate.props.each {
            propIndex = it.propIndex(c)
            if(propIndex) {
              ret += separator+'    '+propIndex.annotation(c)
              empty = false
            }
          }
          delegate.indexes.each  {
            index = it.metaIndex(c)
            if(index) {
              ret += separator+'    '+index.annotation(c)
              empty = false
            }
          }
          ret += '}'
          if(!empty) {
            properties[key] = ret-separator
          }
        }
        properties[key]
      }

      jpaConstants << { Context c ->
        def key = System.identityHashCode(delegate) + 'jpaConstants'
        if(!properties.containsKey(key)) {
          String newLine = System.properties['line.separator']
          def ret = newLine
          def finder = delegate.finders
          def commands = delegate.commands

          if(!delegate.virtual) {
            ret = "public static final String TABLE = \"${delegate.sqlName}\";"+newLine
          }
          ret += newLine
          delegate.props.each { prop ->
            if(!delegate.virtual || !prop.multi) {
              ret += "  public static final String COLUMN_${prop.underscored} = \"${prop.sqlName}\";"+newLine
            }
          }
          ret += newLine
          if(finder && !delegate.virtual) {
            if(finder.finders != null) {
              finder.finders.each {
                def opName = it.operationName
                ret += "  public static final String $opName = \"${delegate.sqlName}.$opName\";"+newLine
              }
            }
            if(finder.counters != null) {
              finder.counters.each {
                def opName = it.operationName
                ret += "  public static final String $opName = \"${delegate.sqlName}.$opName\";"+newLine
              }
            }
            if(finder.exists != null) {
              finder.exists.each {
                def opName = it.operationName
                ret += "  public static final String $opName = \"${delegate.sqlName}.$opName\";"+newLine
              }
            }
          }
          if(commands && !delegate.virtual) {
            if(commands.deleters != null) {
              commands.deleters.each {
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


    BasicType.metaClass {

      metasForBasicType << { Context c ->
        BasicType basic = delegate
        ModelBuilder builder = basic.component.builder
        def metasForBasicType = []
        if(basic.metas) {
          metasForBasicType.addAll(basic.metas)
        }
        if(c.className.contains('BaseEmbeddable') && basic.base) {
          metasForBasicType << builder.meta(type: 'MappedSuperclass')
        } else if(!basic.base && !basic.virtual) {
          metasForBasicType << builder.meta(type: 'Embeddable')
        }
        metasForBasicType
      }

      jpaConstants << { Context c ->
        def key = System.identityHashCode(delegate) + 'jpaConstants'
        if(!properties.containsKey(key)) {
          ModelBuilder builder = c.item.component.builder
          String newLine = System.properties['line.separator']
          def ret = newLine
          delegate.props.each { prop ->
            if(!delegate.virtual || !prop.multi) {
              ret += "  public static final String COLUMN_${prop.underscored} = \"${prop.sqlName}\";"+newLine
            }
          }
          properties[key] = ret
        }
        properties[key]
      }
    }


    Facade.metaClass {

      metasForService << { Context c ->
        Facade service = delegate
        ModelBuilder builder = service.component.builder
        def metasForService = []
        if(service.metas) {
          metasForService.addAll(service.metas)
        }
        if(c.className.contains('ServiceBean') ||c.className.contains('ServiceBase') && !service.base ) {
          metasForService << builder.meta(type: 'Service')
          def stateless = builder.meta(type: 'Stateless', value: [:])
          stateless.value['name'] = "SERVICE_${service.underscored}"
          stateless.value['mappedName'] = "SERVICE_${service.underscored}"
          metasForService << stateless
          def remote = builder.meta(type: 'Remote', value: "${delegate.name}.class")
          c.name(delegate.name)
          metasForService << remote
          def supports = builder.meta(type: 'SupportsEnvironments', multi: true, value: [])
          def environment1 = builder.meta(type: 'Environment', value: [:])
          def environment2 = builder.meta(type: 'Environment', value: [:])
          environment1.value['runtimes'] = "{ ${c.name('SERVER')} }"
          supports.value.add(environment1)
          environment2.value['executions'] = "{ ${c.name('LOCAL')}, ${c.name('MEMORY')} }"
          environment2.value['runtimes'] = "{ ${c.name('CLIENT')} }"
          supports.value.add(environment2)
          metasForService << supports
        }
        metasForService
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



    Commands.metaClass {

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



    Finders.metaClass {

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
    }

    Operation.metaClass {

      getReturn {
        ->
        def key = System.identityHashCode(delegate) + 'return'
        if(!properties.containsKey(key)) {
          def op = delegate
          def ret = 'void'
          if(op.ret) {
            ret = op.ret.name
          } else if (!op.ret && Exist.isInstance(op)) {
            ret = 'boolean'
          }
          properties[key] = ret
        }
        properties[key]
      }

      isReturnTypeBoolean {
        ->
        def key = System.identityHashCode(delegate) + 'returnTypeBoolean'
        if(!properties.containsKey(key)) {
          def ret = false
          def op = delegate
          if (op.ret && op.ret.name == 'boolean')
            ret = true
          properties[key] = ret
        }
        properties[key]
      }

      isDelegateOp {
        ->
        def key = System.identityHashCode(delegate) + 'delegateOp'
        if(!properties.containsKey(key)) {
          def ret = false
          def op = delegate
          if(OperationRef.isInstance(op))
            ret = true
          properties[key] = ret
        }
        properties[key]
      }

      isVoid {
        ->
        def key = System.identityHashCode(delegate) + 'void'
        if(!properties.containsKey(key)) {
          def ret = false
          def op = delegate
          if(!op.ret && !Exist.isInstance(op)) {
            ret = true
          }
          properties[key] = ret
        }
        properties[key]
      }

      isResultExpression {
        ->
        def key = System.identityHashCode(delegate) + 'resultExpression'
        if(!properties.containsKey(key)) {
          def ret = false
          def op = delegate
          if(op.ret instanceof Prop)
            ret = true
          properties[key] = ret
        }
        properties[key]
      }

      isReturnTypeEjb {
        ->
        def key = System.identityHashCode(delegate) + 'returnTypeEjb'
        if(!properties.containsKey(key)) {
          def op = delegate
          def retValue = op.ret
          def ret = delegate.resultExpression ? delegate.resultExpression : (retValue instanceof Entity || retValue instanceof BasicType)
          properties[key] = ret
        }
        properties[key]
      }

      isReturnTypePrimitive {
        ->
        def key = System.identityHashCode(delegate) + 'returnTypePrimitive'
        if(!properties.containsKey(key)) {
          def op = delegate
          def ret = false
          def primitives = [
            'byte',
            'short',
            'int',
            'long',
            'float',
            'double',
            'boolean',
            'String',
            'char'
          ]
          if(op.ret && primitives.contains(op.ret.name))
            ret = true
          properties[key] = ret
        }
        properties[key]
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
          properties[key] = "${delegate.type.name.equalsIgnoreCase('Boolean')?'is':'get'}$delegate.cap()"
        }
        properties[key]
      }

      getSetter << {
        ->
        def key = System.identityHashCode(delegate) + 'setter'
        if(!properties.containsKey(key)) {
          if(delegate.multi) {
            properties[key] = "set$delegate.cap(List<${delegate.type.name}> $delegate.uncap)"
          } else {
            properties[key] = "set$delegate.cap($delegate.type.name $delegate.uncap)"
          }
        }
        properties[key]
      }

      computedType << { Context c ->
        def key = System.identityHashCode(delegate) + 'computedType'
        if(!properties.containsKey(key)) {
          def prop = delegate
          def ret
          ret = prop.multi ? "List<${prop.type.name}>" : "${prop.type.name}"
          c.name(prop.type.name)
          properties[key] = ret
        }
        properties[key]
      }

      relTypeEjb << { Context c ->
        def key = System.identityHashCode(delegate) + 'relTypeEjb'
        if(!properties.containsKey(key)) {
          def prop = delegate
          def ret
          if(Entity.isInstance(prop.type)) {
            ret = "${prop.type.n.cap.Entity}"
          } else {
            ret = "${prop.type.name}"
            //register usage of the type, in order to calculate imports, etc.
            c.name(ret)
          }
          properties[key] = ret
        }
        properties[key]
      }

      typeEjbMember << { Context c ->
        def key = System.identityHashCode(delegate) + 'typeEjbMember'
        if(!properties.containsKey(key)) {
          def prop = delegate
          def ret
          if(Entity.isInstance(prop.type)) {
            ret = "${prop.type.n.cap.Entity}"
          } else if (BasicType.isInstance(prop.type)) {
            ret = "${prop.type.n.cap.Embeddable}"
          } else {
            ret  = "${prop.type.name}"
            //register usage of the type, in order to calculate imports, etc.
            c.name(ret)
          }
          properties[key] = ret
        }
        properties[key]
      }

      getCall << {
        ->
        def key = System.identityHashCode(delegate) + 'call'
        if(!properties.containsKey(key)) {
          if (delegate.typeEntity && (delegate.manyToOne || delegate.oneToOne)) {
            def relationIdProp = delegate.type.idProp
            properties[key] = "set${delegate.cap}${relationIdProp.cap}(${delegate.uncap}${relationIdProp.cap})"
          } else {
            properties[key] = "set$delegate.cap($delegate.uncap)"
          }
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

      getManyToMany << {
        ->
        def key = System.identityHashCode(delegate) + 'manyToMany'
        if(!properties.containsKey(key)) {
          def ret = false
          def prop = delegate
          def opposite = prop.opposite
          if(opposite && prop.multi && opposite.multi) {
            ret = true
          } else if(!opposite && prop.multi && prop.mm) {
            ret = true
          }
          properties[key] = ret
        }
        properties[key]
      }

      getOneToMany << {
        ->
        def key = System.identityHashCode(delegate) + 'oneToMany'
        if(!properties.containsKey(key)) {
          def ret = false
          def prop = delegate
          def opposite = prop.opposite
          if(opposite && prop.multi && !opposite.multi) {
            ret = true
          } else if (!opposite && prop.multi && !prop.mm) {
            ret = true
          }
          properties[key] = ret
        }
        properties[key]
      }

      getManyToOne << {
        ->
        def key = System.identityHashCode(delegate) + 'manyToOne'
        if(!properties.containsKey(key)) {
          def ret = false
          def prop = delegate
          def opposite = prop.opposite
          if(opposite && opposite.multi && !prop.multi) {
            ret = true
          } else if (!opposite && !prop.multi) {
            ret = true
          }
          properties[key] = ret
        }
        properties[key]
      }

      getOneToOne << {
        ->
        def key = System.identityHashCode(delegate) + 'oneToOne'
        if(!properties.containsKey(key)) {
          def ret = false
          def prop = delegate
          def opposite = prop.opposite
          if(opposite && !opposite.multi && !prop.multi) {
            ret = true
          }
          properties[key] = ret
        }
        properties[key]
      }

      isTypeLong << {
        ->
        def key = System.identityHashCode(delegate) + 'typeLong'
        if(!properties.containsKey(key)) {
          properties[key] = (delegate.type.name == 'Long' ? true : false)
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
          } else if(c.subPkg == 'ejb' && delegate.type instanceof Entity) {
            propMapping.addAll(delegate.entityPropMapping(c))
          } else if(c.subPkg == 'ejb') {
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
          def opposite = prop.opposite
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
              metas << association
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
              joinTable.value['name'] =  "\"${currentParent.sqlName}_${prop.sqlName}\""
              if(prop.type) {
                def invJoinColumn = builder.meta(type: 'JoinColumn')
                invJoinColumn.value = ['name' : "\"${prop.type.sqlName}_ID\""]
                joinTable.value['inverseJoinColumns'] = invJoinColumn.annotation(c)
              }
              def joinColumn = builder.meta(type: 'JoinColumn')
              joinColumn.value = ['name' : "\"${currentParent.sqlName}_ID\""]
              joinTable.value['joinColumns'] = joinColumn.annotation(c)
              metas << joinTable
            } else {
              association = builder.meta(type: 'ManyToOne')
              def joinColumn = builder.meta(type: 'JoinColumn')
              joinColumn.value = ['name' : "\"COLUMN_${prop.underscored}\""]
              metas << joinColumn
            }
            metas << association
          }
          properties[key] = metas
        }
        properties[key]
      }

      jpaPropMapping << { Context c ->
        def key = System.identityHashCode(delegate) + 'jpaPropMapping'
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

      isTypeEl << {
        ->
        def key = System.identityHashCode(delegate) + 'typeEl'
        if(!properties.containsKey(key)) {
          def prop = delegate
          def ret = false
          if(Entity.isInstance(prop.type) || BasicType.isInstance(prop.type) || EnumType.isInstance(prop.type)) {
            ret = true
          }
          properties[key] = ret
        }
        properties[key]
      }

      isTypeEjb << {
        ->
        def key = System.identityHashCode(delegate) + 'typeEjb'
        if(!properties.containsKey(key)) {
          def prop = delegate
          def ret = false
          if(Entity.isInstance(prop.type) || BasicType.isInstance(prop.type)) {
            ret = true
          }
          properties[key] = ret
        }
        properties[key]
      }

      isTypeEntity << {
        ->
        def key = System.identityHashCode(delegate) +'typeEntity'
        if(!properties.containsKey(key)) {
          def prop = delegate
          def ret = false
          if(Entity.isInstance(prop.type)) {
            ret = true
          }
          properties[key] = ret
        }
        properties[key]
      }

      isTypeBasicType << {
        ->
        def key = System.identityHashCode(delegate) +'typeBasicType'
        if(!properties.containsKey(key)) {
          def prop = delegate
          def ret = false
          if(BasicType.isInstance(prop.type)) {
            ret = true
          }
          properties[key] = ret
        }
        properties[key]
      }

      isElementCollection << {
        ->
        def key = System.identityHashCode(delegate) +'elementCollection'
        if(!properties.containsKey(key)) {
          def prop = delegate
          def ret = false
          if(Entity.isInstance(prop.type) && prop.multi) {
            ret = true
          }
          properties[key] = ret
        }
        properties[key]
      }

      isPrimitive << {
        ->
        def key = System.identityHashCode(delegate) +'primitive'
        if(!properties.containsKey(key)) {
          def prop = delegate
          def ret = false
          if(prop.type.name.matches("boolean|double|int|long")) {
            ret = true
          }
          properties[key] = ret
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

      getSignatureName << {
        ->
        def key = System.identityHashCode(delegate) + 'signatureName'
        if(!properties.containsKey(key)) {
          properties[key] = delegate.paramsCustom.collect { it.name }.join(', ')
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

    Channel.metaClass {

      metasForBridge << { Context c ->
        Channel channel = delegate
        ModelBuilder builder = channel.component.builder
        def metasForBridge = []
        if(!(c.className.contains('Mdb')) && ( c.className.contains('JmsToCdi') || c.className.contains('EventToCdi') || c.className.contains('NotificationPlugin'))) {
          metasForBridge << builder.meta(type: 'ApplicationScoped' )
          def supportsEnvironments = builder.meta(type: 'SupportsEnvironments', value: [])
          def environment = builder.meta(type: 'Environment', value: [:])
          environment.value['executions'] = '{ PRODUCTIVE }'
          if(c.className.contains('EventToCdi'))
            environment.value['runtimes'] = '{ CLIENT, SERVER }'
          else if(c.className.contains('External'))
            environment.value['runtimes'] = '{ SERVER }'
          else
            environment.value['runtimes'] = '{ CLIENT }'
          supportsEnvironments.value.add(environment)
          metasForBridge << supportsEnvironments
          metasForBridge << builder.meta(type: 'Traceable')
        } else if(c.className.contains('CdiToJms')) {
          metasForBridge << builder.meta(type: 'Stateless')
          def supportsEnvironments = builder.meta(type: 'SupportsEnvironments', value: [])
          def environment = builder.meta(type: 'Environment', value: [:])
          environment.value['runtimes'] = '{ SERVER }'
          supportsEnvironments.value.add(environment)
          metasForBridge << supportsEnvironments
        } else {
          def messageDriven = builder.meta(type: 'MessageDriven', value: [:])
          messageDriven.value['messageListenerInterface'] = "${c.name('MessageListener')}.class"
          def configProps = []
          def destinationValue, destinationTypeValue
          if(c.className.contains('Import')) {
            destinationValue = 'JMS_IMPORT_QUEUE'
            destinationTypeValue = 'QUEUE'
          } else {
            destinationValue = 'JMS_NOTIFICATION_TOPIC'
            destinationTypeValue = 'TOPIC'
          }

          def connectionFactory = builder.meta(type:'ActivationConfigProperty', value: [:])
          connectionFactory.value['propertyName'] = 'CONNECTION_FACTORY_JNDI_NAME'
          connectionFactory.value['propertyValue'] = "${module.cap}Constants.JMS_CONNECTION_FACTORY"

          def destinationJndi = builder.meta(type:'ActivationConfigProperty', value: [:])
          destinationJndi.value['propertyName'] = 'DESTINATION_JNDI_NAME'
          destinationJndi.value['propertyValue'] = "${module.cap}Constants.$destinationValue"

          def destination = builder.meta(type: 'ActivationConfigProperty', value: [:])
          destination.value['propertyName'] = 'DESTINATION'
          destination.value['propertyValue'] = "${module.cap}Constants.$destinationValue"

          def destinationType = builder.meta(type: 'ActivationConfigProperty', value: [:])
          destinationType.value['propertyName'] = 'DESTINATION_TYPE'
          destinationType.value['propertyValue'] = "$destinationTypeValue"

          def topicMessages = builder.meta(type: 'ActivationConfigProperty', value : [:])
          topicMessages.value['propertyName'] = 'TOPIC_MESSAGES_DISTRIBUTION_MODE'
          topicMessages.value['propertyValue'] = 'ONE_COPY'

          def distributedDestination = builder.meta(type: 'ActivationConfigProperty', value: [:])
          distributedDestination.value['propertyName'] = 'DISTRIBUTED_DESTINATION_CONNECTION'
          distributedDestination.value['propertyValue'] = 'EVERY_MEMBER'

          def messageSelector = builder.meta(type: 'ActivationConfigProperty', value: [:])
          messageSelector.value['propertyName'] = 'MESSAGE_SELECTOR'
          if(c.className.contains('ImportData'))
            messageSelector.value['propertyValue'] = "datatype = '" + "${module.shared.names.constants}.JMS_MESSAGE_SELECTOR_${item.underscored}_DATA" + "'"
          else if(c.className.contains('Import'))
            messageSelector.value['propertyValue'] = "datatype = '" + "${module.shared.names.constants}.JMS_MESSAGE_SELECTOR_${item.underscored}" + "'"
          else
            messageSelector.value['propertyValue'] = c.messageSelectors.join(' + " OR " + ')

          configProps.add(connectionFactory.annotation(c))
          configProps.add(destinationJndi.annotation(c))
          configProps.add(destination.annotation(c))
          configProps.add(destinationType.annotation(c))
          configProps.add(topicMessages.annotation(c))
          configProps.add(distributedDestination.annotation(c))
          configProps.add(messageSelector.annotation(c))
          def activationConfigValue = "{ \n      " + configProps.join(',\n      ') + "}"
          messageDriven.value['activationConfig'] = activationConfigValue

          def supportsEnvironments = builder.meta(type: 'SupportsEnvironments', value: [])
          def environment = builder.meta(type: 'Environment', value: [:])
          environment.value['executions'] = '{ PRODUCTIVE }'
          environment.value['runtimes'] = '{ SERVER }'
          supportsEnvironments.value.add(environment)
          metasForBridge << messageDriven
          metasForBridge << supportsEnvironments
        }
        metasForBridge
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
              if(delegate.type.cap == 'NamedQuery') {
                ret += '(' + delegate.value.collect { k, v -> "$k = $v" }.join(', '+newLine+'                ') + ')'
              } else if (delegate.type.cap == 'JoinTable') {
                ret += '(' + delegate.value.collect { k, v -> "$k = $v" }.join(', '+newLine+'             ') + ')'
              } else {
                ret += '(' + delegate.value.collect { k, v -> "$k = $v" }.join(', ') + ')'
              }
            } else if (MetaAttribute.isInstance(delegate.value[0])) {
              ret += "(${delegate.value[0].annotation(c)})"
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