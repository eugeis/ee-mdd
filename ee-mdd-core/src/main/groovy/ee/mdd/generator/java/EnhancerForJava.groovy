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
import ee.mdd.model.Composite
import ee.mdd.model.DerivedName
import ee.mdd.model.Element
import ee.mdd.model.component.Attribute
import ee.mdd.model.component.BasicType
import ee.mdd.model.component.Channel
import ee.mdd.model.component.Commands
import ee.mdd.model.component.CompilationUnit
import ee.mdd.model.component.Component
import ee.mdd.model.component.Container
import ee.mdd.model.component.Controller
import ee.mdd.model.component.Count
import ee.mdd.model.component.DataTypeOperation
import ee.mdd.model.component.DataTypeProp
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
import ee.mdd.model.component.Param
import ee.mdd.model.component.Prop
import ee.mdd.model.component.StructureUnit
import ee.mdd.model.component.Type
import ee.mdd.model.ui.Button
import ee.mdd.model.ui.CheckBox
import ee.mdd.model.ui.ComboBox
import ee.mdd.model.ui.ContextMenu
import ee.mdd.model.ui.Control
import ee.mdd.model.ui.DateField
import ee.mdd.model.ui.GroupBoxHeader
import ee.mdd.model.ui.GroupContentFrame
import ee.mdd.model.ui.Header
import ee.mdd.model.ui.Label
import ee.mdd.model.ui.Listener
import ee.mdd.model.ui.OnContextMenuRequest
import ee.mdd.model.ui.OnItemEditorItemSelect
import ee.mdd.model.ui.OnSelect
import ee.mdd.model.ui.Panel
import ee.mdd.model.ui.Spinner
import ee.mdd.model.ui.Table
import ee.mdd.model.ui.TextField
import ee.mdd.model.ui.TimeField
import ee.mdd.model.ui.Widget


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

    def properties = Collections.synchronizedMap([:])
    Map<String, String> typeToTestValue = [String : '\"TestString\"', Long: 'Long.valueOf(1)', long: '1L',
      Integer: 'Integer.valueOf(1)', int: '1', Date: 'new Date()', boolean: 'true', Boolean: 'Boolean.TRUE']

    def meta = String.metaClass

    meta.duration = { String durationAsString ->
      final def millisFactors = ['min': 60 * 1000, 'ms': 1, 's': 1000, 'h': 60 * 60 * 1000]
      final def m = durationAsString =~ /([0-9]*\.?[0-9]*)([a-z]*)/
      if (!m.matches()) {
        throw new Runtime("Not possible to parse the duration '$durationAsString' to milli seconds. Supported formats are 'ms, s, min, h'. Floating point is supported.")
      }
      //    println "$durationAsString $m"
      long ret = Float.parseFloat(m[0][1]) * (m[0][2] ? millisFactors[m[0][2]] : 1)
      ret
    }


    meta = Element.metaClass

    meta.isTypeEnum = {
      ->
      def key = System.identityHashCode(delegate) + 'typeEnum'
      if (!properties.containsKey(key)) {
        def el = delegate
        def ret = false
        if (EnumType.isInstance(el)) {
          ret = true
        }
        properties[key] = ret
      }
      properties[key]
    }

    meta.nameFull = { Context c ->
      def el = delegate
      def ret
      if (c.subPkg)
        ret = "${c.item.component.parent.ns.name}.${c.item.component.ns.name}.${c.subPkg}.${el.cap}"
      else
        ret = "${c.item.component.parent.ns.name}.${c.item.component.ns.name}.${el.cap}"
    }



    meta = Type.metaClass

    meta.isTypeProp = {
      ->
      def key = System.identityHashCode(delegate) + 'typeProp'
      if (!properties.containsKey(key)) {
        def ret = false
        if (Prop.isInstance(delegate)) {
          ret = true
        }
        properties[key] = ret
      }
      properties[key]
    }


    meta = Composite.metaClass

    meta.getInstancesName = {
      ->
      delegate.name[-1] == 'y' ? "${delegate.uncap[0..-2]}ies" : "${delegate.uncap}s"
    }



    meta = CompilationUnit.metaClass

    meta.getIdProp = {
      ->
      def key = System.identityHashCode(delegate) + 'idProp'
      if (!properties.containsKey(key)) {
        def ret = delegate.props.find { it.primaryKey }
        if (!ret && delegate.superUnit) {
          ret = delegate.superUnit.idProp
        }
        properties[key] = ret
      }
      properties[key]
    }

    meta.getPropsRecursive = {
      ->
      def key = System.identityHashCode(delegate) + 'propsRecursive'
      if (!properties.containsKey(key)) {
        def ret = []
        if (delegate.superUnit) {
          def superUnit = delegate.superUnit
          ret.addAll(superUnit.propsRecursive)
        }
        delegate.props.each { meta.ret = it }
        properties[key] = ret
      }
      properties[key]
    }

    meta.getOperationRefs = {
      ->
      def key = System.identityHashCode(delegate) + 'operationRefs'
      if (!properties.containsKey(key)) {
        properties[key] = delegate.operations.findAll { OperationRef.isInstance(it) }
      }
      properties[key]
    }

    meta.signatureFullConstr = { Context c ->
      def key = System.identityHashCode(delegate) + 'signatureFullConstr'
      if (!properties.containsKey(key)) {
        properties[key] = delegate.props.collect { Prop prop -> "${prop.computedType(c)} ${prop.name}" }.join(', ')
      }
      properties[key]
    }

    meta.signatureNamesFullConstr = { Context c ->
      def key = System.identityHashCode(delegate) + 'signatureNamesFullConstr'
      if (!properties.containsKey(key)) {
        properties[key] = delegate.props.collect { Prop prop -> "${prop.name}" }.join(', ')
      }
      properties[key]
    }

    meta.getMultiSuperProps = {
      ->
      def key = System.identityHashCode(delegate) + 'multiSuperProps'
      if (!properties.containsKey(key)) {
        def superUnit = delegate.superUnit
        def ret = superUnit ? superUnit.props.findAll { it.multi } : []
      }
    }

    meta.getBeanName = {
      ->
      def key = System.identityHashCode(delegate) + 'beanName'
      if (!properties.containsKey(key)) {
        def ret = "${delegate.n.cap['']}"
        if (Entity.isInstance(delegate)) {
          if (delegate.base)
            ret = "${delegate.n.cap.baseEntity}"
          else
            ret = "${delegate.n.cap.entity}"
        } else if (BasicType.isInstance(delegate)) {
          ret = "${delegate.n.cap.embeddable}"
        }
        properties[key] = ret
      }
      properties[key]
    }

    meta.beanNameFactory = { Context c ->
      def key = System.identityHashCode(delegate) + 'beanNameFactory'
      if (!properties.containsKey(key)) {
        def ret = c.name(delegate.n.cap.factory)
        if (Entity.isInstance(delegate)) {
            ret = c.name(delegate.n.cap.entityFactory)
        } else if (BasicType.isInstance(delegate)) {
          ret = c.name(delegate.n.cap.embeddableFactory)
        }
        properties[key] = ret
      }
      properties[key]
    }

    meta.getBeanGenericName = {
      ->
      def key = System.identityHashCode(delegate) + 'beanGenericName'
      if (!properties.containsKey(key)) {
        properties[key] = delegate.beanName + delegate.genericSgn
      }
    }

    meta.getBeanTestName = {
      ->
      def key = System.identityHashCode(delegate) + 'beanTestName'
      if (!properties.containsKey(key)) {
        properties[key] = delegate.virtual ? "${delegate.n.cap.beanTestAbstract}" : "${delegate.n.cap.beanTest}"
      }
      properties[key]
    }

    meta.getBeanTestBaseName = {
      ->
      def key = System.identityHashCode(delegate) + 'beanTestBaseName'
      if (!properties.containsKey(key)) {
        properties[key] = delegate.virtual ? "${delegate.n.cap.beanTestAbstract}" : "${delegate.n.cap.beanTestBase}"
      }
      properties[key]
    }

    meta.getBeanTestGenericName = {
      ->
      def key = System.identityHashCode(delegate) + 'beanTestGenericName'
      if (!properties.containsKey(key)) {
        def ret = delegate.beanTestName + delegate.genericSgn
        properties[key] = ret
      }
    }

    meta.getBeanTestGenericBaseName = {
      ->
      def key = System.identityHashCode(delegate) + 'beanTestGenericBaseName'
      if (!properties.containsKey(key)) {
        def ret = delegate.beanTestBaseName + delegate.genericSgn
        properties[key] = ret
      }
    }

    meta.getBaseGenericName = {
      ->
      def key = System.identityHashCode(delegate) + 'baseGenericName'
      if (!properties.containsKey(key)) {
        def ret = "${delegate.cap}"
        if (delegate.base) {
          ret = "${delegate.n.cap.base}"
        }
        ret += delegate.genericSgn
        properties[key] = ret
      }
      properties[key]
    }

    meta.getGenericsName = {
      ->
      def key = System.identityHashCode(delegate) + 'genericsName'
      if (!properties.containsKey(key)) {
        def ret = delegate.beanName
        if (delegate.generic) {
          def suffix = "<${delegate.generics.join(', ')}>"
          ret += suffix
        }
        properties[key] = ret
      }
      properties[key]
    }

    meta.getGenericSgn = {
      ->
      def key = System.identityHashCode(delegate) + 'genericSgn'
      if (!properties.containsKey(key)) {
        def ret = (delegate.generic ? "<${delegate.generics.join(', ')}>" : '')
        properties[key] = ret
      }
      properties[key]
    }

    meta.getGenericWildcardSgn = {
      ->
      def key = System.identityHashCode(delegate) + 'genericWildcardSgn'
      if (!properties.containsKey(key)) {
        def ret = (delegate.generic ? '<' + '?, ' * (generics.size() - 1) + '?>' : '')
        properties[key] = ret
      }
      properties[key]
    }

    meta.getSimpleGenericSgn = {
      ->
      def key = System.identityHashCode(delegate) + 'simpleGenericSgn'
      if (!properties.containsKey(key)) {
        def ret = (delegate.generic ? "${delegate.generics.join(', ')}, " : '')
        properties[key] = ret
      }
      properties[key]
    }

    meta.getSuperGenericSgn = {
      ->
      def key = System.identityHashCode(delegate) + 'superGenericSgn'
      if (!properties.containsKey(key)) {
        def ret = ''
        if (delegate.superGeneric) {
          ret = "<${delegate.superGenericRefs.join(', ')}>"
        }
        properties[key] = ret
      }
      properties[key]
    }

    meta.getSimpleSuperGenericSgn = {
      ->
      def key = System.identityHashCode(delegate) + 'simpleSuperGenericSgn'
      if (!properties.containsKey(key)) {
        def ret = ''
        if (delegate.superGeneric)
          ret = "${delegate.superGenericRefs.join(', ')}, "
        properties[key] = ret
      }
      properties[key]
    }

    meta.getPropsForHashCode = {
      ->
      def key = System.identityHashCode(delegate) + 'propsForHashCode'
      if (!properties.containsKey(key)) {
        def ret = delegate.props.findAll { it.hashCode }
        properties[key] = ret
      }
      properties[key]
    }

    meta.getOperationsNotManager = {
      ->
      def key = System.identityHashCode(delegate) + 'operationsNotManager'
      if (!properties.containsKey(key)) {
        properties[key] = delegate.operations.findAll { !(it instanceof DataTypeOperation) }
      }
      properties[key]
    }

    meta.isGeneric = {
      ->
      def key = System.identityHashCode(delegate) + 'generic'
      if (!properties.containsKey(key)) {
        def ret = false
        if (delegate.generics)
          ret = (!delegate.generics.empty ? true : false)
        properties[key] = ret
      }
      properties[key]
    }

    meta.isSuperGeneric = {
      ->
      def key = System.identityHashCode(delegate) + 'superGeneric'
      if (!properties.containsKey(key)) {
        def ret = false
        if (delegate.superGenericRefs && !delegate.superGenericRefs.empty)
          ret = true
        properties[key] = ret
      }
      properties[key]
    }

    meta.isSuperHierarchyComplete = {
      ->
      def key = System.identityHashCode(delegate) + 'superHierarchyComplete'
      if (!properties.containsKey(key)) {
        def ret = false
        if (!delegate.superUnit || delegate.superUnit.superHierarchyComplete) {
          ret = true
        }
        properties[key] = ret
      }
      properties[key]
    }



    meta = StructureUnit.metaClass

    meta.getInitializerName = {
      ->
      def key = System.identityHashCode(delegate) + 'initializerName'
      if (!properties.containsKey(key)) {
        def ret
        if (delegate instanceof Component) {
          ret = "${delegate.component.capShortName}Initializer"
        } else if (delegate.key != delegate.component.key) {
          ret = "${delegate.component.capShortName}${delegate.capShortName}Initializer"
        } else {
          ret = "${delegate.component.capShortName}${delegate.cap}Initializer"
        }
        properties[key] = ret
      }
      properties[key]
    }


    meta = Component.metaClass

    meta.getBackends = {
      ->
      def key = System.identityHashCode(delegate) + 'backends'
      if (!properties.containsKey(key)) {
        def ret = delegate.modules.findAll { m ->
          m.name.equals('backend')
        }
        properties[key] = ret
      }
      properties[key]
    }




    meta = Entity.metaClass


    meta.getMlKeyConstant = { -> delegate.underscored }

    meta.getMlKey = {
      ->
      delegate.underscored.toLowerCase()
    }

    meta.jpaMetasForEntity = { Context c ->
      def key = System.identityHashCode(delegate) + 'jpaMetasforEntity'
      if (!properties.containsKey(key)) {
        Entity entity = delegate
        ModelBuilder builder = entity.component.builder
        def className = DerivedName.isInstance(c.className) ? c.className.name : c.className
        def jpaMetasForEntity = []
        def namedQueries = builder.meta(type: 'NamedQueries', multi: true, value: [])
        if (entity.finders) {
          namedQueries.value.addAll(entity.finders.finderNamedQuery(c))
          namedQueries.value.addAll(entity.finders.counterNamedQuery(c))
          namedQueries.value.addAll(entity.finders.existerNamedQuery(c))
        }
        if (entity.deleters) {
          namedQueries.value.addAll(entity.commands.deleterNamedQuery(c))
        }
        meta.jpaMetasForEntity = namedQueries
        def table = builder.meta(type: 'Table', value: [:])
        table.value['name'] = className + '.TABLE'
        def indexes = entity.indexesForMeta(c)
        if (indexes != null) {
          table.value['indexes'] = indexes
        }
        meta.jpaMetasForEntity = table
        properties[key] = jpaMetasForEntity
      }
      properties[key]
    }

    meta.metasForEntity = { Context c ->
      Entity entity = delegate
      ModelBuilder builder = entity.component.builder
      def className = DerivedName.isInstance(c.className) ? c.className.name : c.className
      def metasForEntity = []
      if (entity.metas) {
        metasForEntity.addAll(entity.metas)
      }
      if (className.contains('BaseEntity') && entity.base || entity.virtual) {
        meta.metasForEntity = builder.meta(type: 'MappedSuperclass')
      } else {
        meta.metasForEntity = builder.meta(type: 'Entity')
      }
      metasForEntity
    }

    meta.indexesForMeta = { Context c ->
      def key = System.identityHashCode(delegate) + 'indexesForMeta'
      if (!properties.containsKey(key)) {
        ModelBuilder builder = delegate.component.builder
        String newLine = System.properties['line.separator']
        def ret = '{' + newLine
        def propIndex
        def index
        def empty = true
        def separator = ', ' + newLine

        delegate.props.each {
          propIndex = it.propIndex(c)
          if (propIndex) {
            ret += separator + '    ' + propIndex.annotation(c)
            empty = false
          }
        }
        delegate.indexes.each {
          index = it.metaIndex(c)
          if (index) {
            ret += separator + '    ' + index.annotation(c)
            empty = false
          }
        }
        ret += '}'
        if (!empty) {
          properties[key] = ret - separator
        }
      }
      properties[key]
    }

    meta.jpaConstants = { Context c ->
      def key = System.identityHashCode(delegate) + 'jpaConstants'
      if (!properties.containsKey(key)) {
        String newLine = System.properties['line.separator']
        def ret = ''
        def finder = delegate.finders
        def commands = delegate.commands

        if (!delegate.virtual) {
          ret = "public static final String TABLE = \"${delegate.sqlName}\";" + newLine
        }
        ret += newLine
        delegate.props.each { prop ->
          if (!delegate.virtual || !prop.multi) {
            ret += "  public static final String COLUMN_${prop.underscored} = \"${prop.sqlName}\";" + newLine
          }
        }
        ret += newLine
        if (finder && !delegate.virtual) {
          if (finder.finders != null) {
            finder.finders.each {
              def opName = it.operationName
              ret += "  public static final String $opName = \"${delegate.sqlName}.$opName\";" + newLine
            }
          }
          if (finder.counters != null) {
            finder.counters.each {
              def opName = it.operationName
              ret += "  public static final String $opName = \"${delegate.sqlName}.$opName\";" + newLine
            }
          }
          if (finder.exists != null) {
            finder.exists.each {
              def opName = it.operationName
              ret += "  public static final String $opName = \"${delegate.sqlName}.$opName\";" + newLine
            }
          }
        }
        if (commands && !delegate.virtual) {
          if (commands.deleters != null) {
            commands.deleters.each {
              def opName = it.operationName
              ret += "  public static final String $opName = \"${delegate.sqlName}.$opName\";" + newLine
            }
          }
        }
        properties[key] = ret
      }
      properties[key]
    }



    meta = BasicType.metaClass

    meta.metasForBasicType = { Context c ->
      BasicType basic = delegate
      ModelBuilder builder = basic.component.builder
      def className = DerivedName.isInstance(c.className) ? c.className.name : c.className
      def metasForBasicType = []
      if (basic.metas) {
        metasForBasicType.addAll(basic.metas)
      }
      if (className.contains('BaseEmbeddable') && basic.base) {
        meta.metasForBasicType = builder.meta(type: 'MappedSuperclass')
      } else if (!basic.base && !basic.virtual) {
        meta.metasForBasicType = builder.meta(type: 'Embeddable')
      }
      metasForBasicType
    }

    meta.jpaConstants = { Context c ->
      def key = System.identityHashCode(delegate) + 'jpaConstants'
      if (!properties.containsKey(key)) {
        ModelBuilder builder = c.item.component.builder
        String newLine = System.properties['line.separator']
        def ret = newLine
        delegate.props.each { prop ->
          if (!delegate.virtual || !prop.multi) {
            ret += "  public static final String COLUMN_${prop.underscored} = \"${prop.sqlName}\";" + newLine
          }
        }
        properties[key] = ret
      }
      properties[key]
    }



    meta = Facade.metaClass

    meta.metasForService = { Context c ->
      Facade service = delegate
      ModelBuilder builder = service.component.builder
      def className = DerivedName.isInstance(c.className) ? c.className.name : c.className
      def metasForService = []
      if (service.metas) {
        metasForService.addAll(service.metas)
      }
      if (className.contains('ServiceBean') || className.contains('ServiceBase') && !service.base) {
        metasForService << builder.meta(type: 'Service')
        def stateless = builder.meta(type: 'Stateless', value: [:])
        stateless.value['name'] = "SERVICE_${service.underscored}"
        stateless.value['mappedName'] = "SERVICE_${service.underscored}"
        metasForService << stateless
        def remote = builder.meta(type: 'Remote', value: "${delegate.name}.class")
        c.name(delegate)
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

    meta.getLogicUnits = {
      ->
      def key = System.identityHashCode(delegate) + 'logicUnits'
      if (!properties.containsKey(key)) {
        properties[key] = delegate.children.findAll { LogicUnit.isInstance(it) }
      }
      properties[key]
    }




    meta = Index.metaClass

    meta.metaIndex = { Context c ->
      ModelBuilder builder = c.item.component.builder
      def index = delegate
      def metaIndex = builder.meta(type: 'Index', value: [:])
      def sqlNames = []
      index.props.each { meta.sqlNames = it.sqlName }
      def columns = sqlNames.join(', ')
      def indexName = index.props.collect { it.sqlName }.join('_')
      metaIndex.value['name'] = "\"$indexName\""
      metaIndex.value['columnList'] = "\"$columns\""
      if (index.unique) {
        metaIndex['unique'] = true
      }
      metaIndex
    }



    meta = Commands.metaClass

    meta.deleterNamedQuery = { Context c ->
      if (delegate.deleters != null) {
        def className = DerivedName.isInstance(c.className) ? c.className.name : c.className
        def deleterQueries = []
        ModelBuilder builder = c.item.component.builder
        delegate.deleters.each { deleter ->
          def namedQuery = builder.meta(type: 'NamedQuery', value: [:])
          namedQuery.value['name'] = className + '.' + deleter.operationName
          namedQuery.value['query'] = "\"DELETE FROM ${c.item.n.cap.entity} e WHERE ( ${deleter.propWhere} )\""
          meta.deleterQueries = namedQuery
        }
        deleterQueries
      }
    }




    meta = Finders.metaClass

    meta.finderNamedQuery = { Context c ->
      if (delegate.finders != null) {
        def className = DerivedName.isInstance(c.className) ? c.className.name : c.className
        def finderQueries = []
        ModelBuilder builder = c.item.component.builder
        delegate.finders.each { finder ->
          def namedQuery = builder.meta(type: 'NamedQuery', value: [:])
          namedQuery.value['name'] = className + '.' + finder.operationName
          namedQuery.value['query'] = "\"SELECT e FROM ${c.item.n.cap.entity} e WHERE ( ${finder.propWhere} )\""
          meta.finderQueries = namedQuery
        }
        finderQueries
      }
    }

    meta.counterNamedQuery = { Context c ->
      if (delegate.counters != null) {
        def className = DerivedName.isInstance(c.className) ? c.className.name : c.className
        def counterQueries = []
        ModelBuilder builder = c.item.component.builder
        delegate.counters.each { counter ->
          def namedQuery = builder.meta(type: 'NamedQuery', value: [:])
          namedQuery.value['name'] = className + '.' + counter.operationName
          namedQuery.value['query'] = "\"SELECT COUNT(e) FROM ${c.item.n.cap.entity} e WHERE ( ${counter.propWhere} )\""
          meta.counterQueries = namedQuery
        }
        counterQueries
      }
    }

    meta.existerNamedQuery = { Context c ->
      if (delegate.exists != null) {
        def className = DerivedName.isInstance(c.className) ? c.className.name : c.className
        def existsQueries = []
        ModelBuilder builder = c.item.component.builder
        delegate.exists.each { exist ->
          def namedQuery = builder.meta(type: 'NamedQuery', value: [:])
          namedQuery.value['name'] = className + '.' + exist.operationName
          namedQuery.value['query'] = "\"SELECT COUNT(e) FROM ${c.item.n.cap.entity} e WHERE ( ${exist.propWhere} )\""
          meta.existsQueries = namedQuery
        }
        existsQueries
      }
    }


    meta = Operation.metaClass

    meta.getReturn = {
      ->
      def key = System.identityHashCode(delegate) + 'return'
      if (!properties.containsKey(key)) {
        def op = delegate
        def ret = 'void'
        if (op.ret) {
          ret = op.ret.name
        } else if (!op.ret && Exist.isInstance(op)) {
          ret = 'boolean'
        }
        properties[key] = ret
      }
      properties[key]
    }

    meta.returnTypeRaw = { Context c -> delegate.returnTypeExternal }

    meta.getReturnTypeRaw = { -> delegate.returnTypeExternal }

    meta.getReturnTypeExternal = { -> delegate.return }

    meta.returnTypeExternal = { Context c -> delegate.returnTypeExternal }

    meta.isReturnTypeBoolean = {
      ->
      def key = System.identityHashCode(delegate) + 'returnTypeBoolean'
      if (!properties.containsKey(key)) {
        def ret = false
        def op = delegate
        if (op.ret && (op.ret.name == 'boolean' || op.ret.name == 'Boolean'))
          ret = true
        properties[key] = ret
      }
      properties[key]
    }

    meta.isDelegateOp = {
      ->
      def key = System.identityHashCode(delegate) + 'delegateOp'
      if (!properties.containsKey(key)) {
        def ret = false
        def op = delegate
        if (OperationRef.isInstance(op))
          ret = true
        properties[key] = ret
      }
      properties[key]
    }

    meta.isVoid = {
      ->
      def key = System.identityHashCode(delegate) + 'void'
      if (!properties.containsKey(key)) {
        def ret = false
        def op = delegate
        if (!op.ret && !Exist.isInstance(op)) {
          ret = true
        }
        properties[key] = ret
      }
      properties[key]
    }

    meta.isResultExpression = {
      ->
      def key = System.identityHashCode(delegate) + 'resultExpression'
      if (!properties.containsKey(key)) {
        def ret = false
        def op = delegate
        if (op.ret instanceof Prop)
          ret = true
        properties[key] = ret
      }
      properties[key]
    }

    meta.isReturnTypeEjb = {
      ->
      def key = System.identityHashCode(delegate) + 'returnTypeEjb'
      if (!properties.containsKey(key)) {
        def op = delegate
        def retValue = op.ret
        def ret = delegate.resultExpression ? delegate.resultExpression : (retValue instanceof Entity || retValue instanceof BasicType)
        properties[key] = ret
      }
      properties[key]
    }

    meta.isReturnTypePrimitive = {
      ->
      def key = System.identityHashCode(delegate) + 'returnTypePrimitive'
      if (!properties.containsKey(key)) {
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
        if (op.ret && primitives.contains(op.ret.name))
          ret = true
        properties[key] = ret
      }
      properties[key]
    }


    meta.originalParent = { Context c ->
      def ret = true
      def op = delegate
      if (op.entity.name == c.item.name) {
        ret = false
      }
      ret
    }



    meta = OperationRef.metaClass

    meta.getNameTest = {
      delegate.resultExpression ? "${delegate.ref.cap}Of" : delegate.ref.name
    }

    meta = DataTypeProp.metaClass

    meta.getRelation = { delegate.typeEntity }
    
    meta.getManyToMany = {
      ->
      def key = System.identityHashCode(delegate) + 'manyToMany'
      if (!properties.containsKey(key)) {
        def ret = false
        def prop = delegate
        def opposite = prop.opposite
        if (opposite && prop.multi && opposite.multi) {
          ret = true
        } else if (!opposite && prop.multi && prop.mm) {
          ret = true
        }
        properties[key] = ret
      }
      properties[key]
    }

    meta.getOneToMany = {
      ->
      def key = System.identityHashCode(delegate) + 'oneToMany'
      if (!properties.containsKey(key)) {
        def ret = false
        def prop = delegate
        def opposite = prop.opposite
        if (opposite && prop.multi && !opposite.multi) {
          ret = true
        } else if (!opposite && prop.multi && !prop.mm) {
          ret = true
        }
        properties[key] = ret
      }
      properties[key]
    }

    meta.getManyToOne = {
      ->
      def key = System.identityHashCode(delegate) + 'manyToOne'
      if (!properties.containsKey(key)) {
        def ret = false
        def prop = delegate
        def opposite = prop.opposite
        if (prop.typeEntity) {
          if (opposite && opposite.multi && !prop.multi) {
            ret = true
          } else if (!opposite && !prop.multi) {
            ret = true
          }
        }
        properties[key] = ret
      }
      properties[key]
    }

    meta.getOneToOne = {
      ->
      def key = System.identityHashCode(delegate) + 'oneToOne'
      if (!properties.containsKey(key)) {
        def ret = false
        def prop = delegate
        def opposite = prop.opposite
        if (opposite && !opposite.multi && !prop.multi) {
          ret = true
        }
        properties[key] = ret
      }
      properties[key]
    }

    
    
    meta = Find.metaClass
    
    meta.returnTypeExternal = { Context c ->
      c.name(delegate.parent.entity.n.cap[''])
      delegate.unique ? "${delegate.parent.entity.n.cap['']}" : "${c.name('List')}<${delegate.parent.entity.n.cap['']}>"
    }
    
    
    
    meta = Delete.metaClass
    
    meta.returnTypeExternal = { Context c ->
        'void'
    }
    

    meta = DataTypeOperation.metaClass

    meta.returnTypeExternal = { Context c -> delegate.return }

    meta.returnTypeRaw = { Context c ->
      def ret = ''
      if (Find.isInstance(delegate) && !delegate.unique) {
        ret = c.name('List')
      } else {
        ret = c.name(delegate.parent.entity.cap)
      }
      ret
    }

    meta.getPropWhere = {
      ->
      String separator = ' AND '
      def ret = delegate.params.collect { param ->
        param.prop.multi ? "e.$param.prop.name IN :${param.name}s" : "e.$param.prop.name = :$param.name"
      }.join(separator)
      ret
    }

    meta.getOperationName = {
      ->
      def ret = ''
      def separator = '_AND_'
      if (Find.isInstance(delegate)) {
        ret = 'FIND_BY_'
      } else if (Count.isInstance(delegate)) {
        ret = 'COUNT_BY_'
      } else if (Delete.isInstance(delegate)) {
        ret = 'DELETE_BY_'
      } else if (Exist.isInstance(delegate)) {
        ret = 'EXISTS_BY_'
      }
      delegate.params.each { param ->
        ret += separator + param.prop.underscored
      }
      ret - separator
    }

    meta.getPropGetters = {
      delegate.paramsWithoutDefaults.collect {
        Prop prop = it.prop;
        prop.multi ? "CollectionUtils.asList(entity.${prop.getterWithIdIfRelation})" : "entity.${prop.getterWithIdIfRelation}"
      }.join(", ")
    }

    meta.getParamsWithoutDefaults = {
      ->
      def key = System.identityHashCode(delegate) + 'paramsWithoutDefaults'
      if (!properties.containsKey(key)) {
        properties[key] = delegate.params.findAll { it.prop && !it.prop.defaultValue }
      }
      properties[key]
    }

    meta.propLinks = { Context c ->
      def key = System.identityHashCode(delegate) + 'propLinks'
      if (!properties.containsKey(key)) {
        properties[key] = delegate.params.collect {
          it.multi ? "new ${c.name('StringLink')}<List<${it.prop.computedTypeForIdIfRelation}>>(\"${it.name}s\", ${it.name}s)" : "new ${c.name('StringLink')}<${it.prop.computedTypeForIdIfRelation}>(\"${it.name}\", ${it.name})"
        }.join(', ')
      }
      properties[key]
    }

    meta.propCompare = { Context c ->
      def key = System.identityHashCode(delegate) + 'propCompare'
      if (!properties.containsKey(key)) {
        properties[key] = paramsWithoutDefaults.collect { param ->
          def prop = param.prop; param.multi ? "${param.paramName}s.contains(entity.${prop.getterWithIdIfRelation})" : "${param.compareMethod(c)}(entity.${prop.getterWithIdIfRelation}, $param.paramName)"
        }.join(' && ')
      }
      properties[key]
    }

    meta.getMlKeyName = {
      ->
      def key = System.identityHashCode(delegate) + 'mlKeyName'
      if (!properties.containsKey(key)) {
        properties[key] = "${delegate.entity.name}_$delegate.name".replaceAll(/(\B[A-Z])/, '_$1').toLowerCase()
      }
      properties[key]
    }

    meta.getMlKeyConstant = {
      ->
      def key = System.identityHashCode(delegate) + 'mlKeyConstant'
      if (!properties.containsKey(key)) {
        properties[key] = delegate.mlKeyName.toUpperCase()
      }
      properties[key]
    }

    meta.isOneOfPropsRelationId = {
      ->
      def key = System.identityHashCode(delegate) + 'oneOfPropsRelationId'
      if (!properties.containsKey(key)) {
        def op = delegate
        def ret = false
        op.paramsWithoutDefaults.each { param ->
          if (param.prop.relationIdProp != null) {
            ret = true
          }
          properties[key] = ret
        }
      }
      properties[key]
    }
    


    meta = Literal.metaClass

    meta.getIs = {
      ->
      def key = System.identityHashCode(delegate) + 'is'
      if (!properties.containsKey(key)) {
        properties[key] = "is$delegate.cap()"
      }
      properties[key]
    }




    meta = Prop.metaClass

    meta.getUncapFullName = {
      ->
      def key = System.identityHashCode(delegate) + 'uncapFullName'
      if (!properties.containsKey(key)) {
        properties[key] = "${delegate.parent.uncap}$delegate.cap"
      }
      properties[key]
    }

    meta.getCapFullName = {
      ->
      def key = System.identityHashCode(delegate) + 'capFullName'
      if (!properties.containsKey(key)) {
        properties[key] = "${delegate.parent.cap}$delegate.cap"
      }
      properties[key]
    }

    meta.getGetter = {
      ->
      def key = System.identityHashCode(delegate) + 'getter'
      if (!properties.containsKey(key)) {
        if(delegate.type) {
          properties[key] = "${delegate.type.name.equalsIgnoreCase('Boolean') ? 'is' : 'get'}${delegate.cap}()"
        } else {
          properties[key] = "get${delegate.cap}"
        }
      }
      properties[key]
    }

    meta.getSetter = {
      ->
      def key = System.identityHashCode(delegate) + 'setter'
      if (!properties.containsKey(key)) {
        if (delegate.multi) {
          properties[key] = "set$delegate.cap(List<${delegate.type.name}> $delegate.uncap)"
        } else {
          properties[key] = "set$delegate.cap($delegate.type.name $delegate.uncap)"
        }
      }
      properties[key]
    }

    meta.getSetterCall = { "set${delegate.cap}(${delegate.uncap})" }

    meta.getSetterMethodName = {
      ->
      def key = System.identityHashCode(delegate) + 'setterMethodName'
      if (!properties.containsKey(key)) {
        properties[key] = "set${delegate.cap}"
      }
      properties[key]
    }

    meta.getTestValue = {
      ->
      def key = System.identityHashCode(delegate) + 'testValue'
      if (!properties.containsKey(key)) {
        def prop = delegate
        def ret
        if (prop.defaultValue) {
          ret = prop.defaultValue
        } else if (prop.multi) {
          ret = "new ArrayList<>()"
        } else {
          ret = "String"
        }
        properties[key] = ret
      }
      properties[key]
    }

    meta.getNameWithIdIfRelation = {
      ->
      def key = System.identityHashCode(delegate) + 'nameWithIdIfRelation'
      if (!properties.containsKey(key)) {
        def prop = delegate
        properties[key] = prop.relationIdProp == null ? "${prop.name}" : "${prop.name}${prop.relationIdProp.cap}"
      }
      properties[key]
    }

    meta.getGetterWithIdIfRelation = {
      ->
      def key = System.identityHashCode(delegate) + 'getterWithIdIfRelation'
      if (!properties.containsKey(key)) {
        def prop = delegate
        properties[key] = prop.relationIdProp == null ? prop.getter : "${prop.name}${prop.relationIdProp.cap}"
      }
      properties[key]
    }

    meta.computedType = { Context c ->
      def key = System.identityHashCode(delegate) + 'computedType'
      if (!properties.containsKey(key)) {
        def prop = delegate
        def ret
        ret = prop.multi ? "List<${prop.type.name}>" : "${prop.type.name}"
        c.name(prop.type)
        properties[key] = ret
      }
      properties[key]
    }

    meta.computedTypeEjb = { Context c ->
      def key = System.identityHashCode(delegate) + 'computedTypeEjb'
      if (!properties.containsKey(key)) {
        def prop = delegate
        def ret
        ret = prop.multi ? "List<${prop.relTypeEjb(c)}>" : prop.relTypeEjb(c)
        properties[key] = ret
      }
      properties[key]
    }

    meta.getComputedTypeForIdIfRelation = {
      ->
      delegate.relationIdProp == null ? delegate.computedBoxedType : delegate.relationIdProp.computedBoxedType
    }

    meta.relTypeEjb = { Context c ->
      def prop = delegate
      def ret
      if (Entity.isInstance(prop.type)) {
        ret = "${prop.type.n.cap.Entity}"
      } else {
        ret = "${prop.type.name}"
        //register usage of the type, in order to calculate imports, etc.
        c.name(ret)
      }
    }

    meta.typeEjbMember = { Context c ->
      def key = System.identityHashCode(delegate) + 'typeEjbMember'
      if (!properties.containsKey(key)) {
        def prop = delegate
        def ret
        if (Entity.isInstance(prop.type)) {
          ret = "${prop.type.n.cap.Entity}"
        } else if (BasicType.isInstance(prop.type)) {
          ret = "${prop.type.n.cap.Embeddable}"
        } else {
          ret = "${prop.type.name}"
          //register usage of the type, in order to calculate imports, etc.
          c.name(ret)
        }
        properties[key] = ret
      }
      properties[key]
    }

    meta.getCall = {
      ->
      def key = System.identityHashCode(delegate) + 'call'
      if (!properties.containsKey(key)) {
        if (delegate.typeEntity && (delegate.manyToOne || delegate.oneToOne)) {
          def relationIdProp = delegate.type.idProp
          properties[key] = "set${delegate.cap}${relationIdProp.cap}(${delegate.uncap}${relationIdProp.cap})"
        } else {
          properties[key] = "set$delegate.cap($delegate.uncap)"
        }
      }
      properties[key]
    }

    meta.getTestable = {
      ->
      def key = System.identityHashCode(delegate) + 'testable'
      if (!properties.containsKey(key)) {
        properties[key] = typeToTestValue.containsKey(delegate.type.name)
      }
      properties[key]
    }

    meta.getTestValue = {
      ->
      def key = System.identityHashCode(delegate) + 'testValue'
      if (!properties.containsKey(key)) {
        properties[key] = typeToTestValue.get(delegate.type.name)
      }
      properties[key]
    }

    meta.getRelationIdProp = {
      ->
      def key = System.identityHashCode(delegate) + 'relationIdProp'
      if (!properties.containsKey(key)) {
        if(DataTypeProp.isInstance(delegate)) {
          properties[key] = (delegate.manyToOne || delegate.oneToOne) ? delegate.type.idProp : null
        }
      }
      properties[key]
    }

    meta.getComputedBoxedType = {
      def key = System.identityHashCode(delegate) + 'computedBoxedType'
      if (!properties.containsKey(key)) {
        properties[key] = delegate.multi ? "List<${delegate.boxedType}>" : delegate.boxedType
      }
      properties[key]
    }

    meta.getBoxedType = {
      def key = System.identityHashCode(delegate) + 'boxedType'
      if (!properties.containsKey(key)) {
        properties[key] = delegate.type.name == 'boolean' ? 'Boolean' : delegate.type.name == 'int' ? 'Integer' : delegate.type.name == 'long' ? 'Long' : delegate.type.name
      }
      properties[key]
    }

    meta.isTypeLong = {
      ->
      def key = System.identityHashCode(delegate) + 'typeLong'
      if (!properties.containsKey(key)) {
        properties[key] = (delegate.type.name == 'Long' ? true : false)
      }
      properties[key]
    }

    meta.isTypeString = {
      ->
      def key = System.identityHashCode(delegate) + 'typeString'
      if (!properties.containsKey(key)) {
        properties[key] = (delegate.type.name == 'String' ? true : false)
      }
      properties[key]
    }

    meta.isTypeInteger = {
      ->
      def key = System.identityHashCode(delegate) + 'typeInteger'
      if (!properties.containsKey(key)) {
        properties[key] = (delegate.type.name == 'Integer' ? true : false)
      }
      properties[key]
    }

    meta.propMapping = { Context c ->
      def key = System.identityHashCode(delegate) + 'propMapping'
      ModelBuilder builder = c.item.component.builder
      def prop = delegate
      def propMapping = []
      c.propMeta = true
      if (!c.item.virtual && delegate.primaryKey) {
        meta.propMapping = builder.meta(type: 'Column', value: ['name': "\"$prop.sqlName\""])
        meta.propMapping = builder.meta(type: 'Id')
        if (!c.item.manualId) {
          def generator = c.item.idGeneratorName
          if (!generator) {
            generator = "${c.item.model.key.toUpperCase()}_${c.item.sqlName}_SEQ"
          }
          meta.propMapping = builder.meta(type: 'GeneratedValue', value: ['strategy': "${c.name('GenerationType')}" + '.TABLE', 'generator': "\"$generator\""])
          meta.propMapping = builder.meta(type: 'TableGenerator', value: ['name': "\"$generator\"", 'table': "\"SEQUENCER\""])
        }
      } else if (c.subPkg == 'ejb' && delegate.type instanceof Entity) {
        propMapping.addAll(delegate.entityPropMapping(c))
      } else if (c.subPkg == 'ejb') {
        propMapping.addAll(delegate.jpaPropMapping(c))
      }
      propMapping
    }

    meta.entityPropMapping = { Context c ->
      def key = System.identityHashCode(delegate) + 'entityPropMapping'
      if (!properties.containsKey(key)) {
        ModelBuilder builder = c.item.component.builder
        String newLine = System.properties['line.separator']
        def prop = delegate
        def opposite = prop.opposite
        def currentParent = prop.parent
        def metas = []
        def association
        if (opposite) {
          if (prop.multi) {
            if (opposite.multi) {
              association = builder.meta(type: 'ManyToMany')
              association.value = ['mappedBy': "\"$prop.opposite\""]
            } else {
              association = builder.meta(type: 'OneToMany')
              association.value = ['cascade': "${c.name('CascadeType')}" + '.ALL', 'mappedBy': "\"$prop.opposite.name\"", 'orphanRemoval': true]
            }
            meta.metas = association
          } else {
            if (opposite.multi) {
              association = builder.meta(type: 'ManyToOne')
            } else if (prop.owner) {
              association = builder.meta(type: 'OneToOne')
              association.value = ['cascade': "${c.name('CascadeType')}" + '.PERSIST', 'mappedBy': "\"$prop.opposite\""]
            } else {
              association = builder.meta(type: 'OneToOne')
              association.value = ['fetch': "${c.name('FetchType')}" + '.LAZY']
            }
            meta.metas = association
            if (opposite.multi || !prop.owner) {
              def joinColumn = builder.meta(type: 'JoinColumn')
              joinColumn.value = ['name': "COLUMN_$prop.underscored"]
              meta.metas = joinColumn
            }
          }
        } else {
          if (prop.multi) {
            if (prop.mm) {
              association = builder.meta(type: 'ManyToMany')
              association.value = ['cascade': "${c.name('CascadeType')}" + '.ALL']
            } else {
              association = builder.meta(type: 'OneToMany')
              association.value = ['cascade': "${c.name('CascadeType')}" + '.ALL']
            }
            def joinTable = builder.meta(type: 'JoinTable', value: [:])
            joinTable.value['name'] = "\"${currentParent.sqlName}_${prop.sqlName}\""
            if (prop.type) {
              def invJoinColumn = builder.meta(type: 'JoinColumn')
              invJoinColumn.value = ['name': "\"${prop.type.sqlName}_ID\""]
              joinTable.value['inverseJoinColumns'] = invJoinColumn.annotation(c)
            }
            def joinColumn = builder.meta(type: 'JoinColumn')
            joinColumn.value = ['name': "\"${currentParent.sqlName}_ID\""]
            joinTable.value['joinColumns'] = joinColumn.annotation(c)
            meta.metas = joinTable
          } else {
            association = builder.meta(type: 'ManyToOne')
            def joinColumn = builder.meta(type: 'JoinColumn')
            joinColumn.value = ['name': "\"COLUMN_${prop.underscored}\""]
            meta.metas = joinColumn
          }
          meta.metas = association
        }
        properties[key] = metas
      }
      properties[key]
    }

    meta.jpaPropMapping = { Context c ->
      def key = System.identityHashCode(delegate) + 'jpaPropMapping'
      if (!properties.containsKey(key)) {
        ModelBuilder builder = c.item.component.builder
        String newLine = System.properties['line.separator']
        def prop = delegate
        def currentParent = prop.parent
        def metas = []
        if (prop.type.name.equals('Date')) {
          meta.metas = builder.meta(type: 'Temporal', value: "${c.name('TemporalType')}" + '.TIMESTAMP')
        } else if (prop.type instanceof Enum) {
          meta.metas = builder.meta(type: 'Enumerated', value: "${c.name('EnumType')}" + '.STRING')
        } else if (prop.type instanceof BasicType) {
          if (!prop.multi) {
            meta.metas = builder.meta(type: 'Embedded')
            def attrOverrides = builder.meta(type: 'AttributeOverrides', multi: true, value: [])
            prop.type.props.each {
              def attrOverride = builder.meta(type: 'AttributeOverride')
              attrOverride.value = ['name': "$it.underscored"]
              attrOverrides.value << attrOverride
            }
            attrOverrides.value.addAll()
          } else {
            meta.metas = builder.meta(type: 'Embedded')
          }
        } else if (prop.lob) {
          meta.metas = builder.meta(type: 'Lob')
        }
        if (prop.multi) {
          meta.metas = builder.meta(type: 'ElementCollection', value: ['fetch': "${c.name('FetchType')}" + '.EAGER'])
          def joinColum = builder.meta(type: 'JoinColumn', value: ['name': "\"${currentParent.sqlName}_ID\""])
          meta.metas = builder.meta(type: 'CollectionTable', value: ['name': "\"${currentParent.sqlName}_${prop.sqlName}\"", 'joinColumns': "${joinColum.annotation(c)}"])
        } else if (!(prop.type instanceof BasicType)) {
          meta.metas = builder.meta(type: 'Column', value: ['name': "COLUMN_${prop.underscored}"])
        }
        properties[key] = metas
      }
      properties[key]
    }

    meta.propIndex = { Context c ->
      ModelBuilder builder = c.item.component.builder
      def prop = delegate
      def index
      Boolean manyToOne
      if (prop.type instanceof Entity && !prop.multi && !prop.opposite)
        manyToOne = true
      if (!prop.primaryKey && (prop.index || prop.unique || manyToOne)) {
        index = builder.meta(type: 'Index', value: [:])
        index.value['name'] = "\"${c.item.sqlName}_$prop.sqlName\""
        if (prop.unique) {
          index.value['unique'] = true
        }
        index.value['columnList'] = "\"$prop.sqlName\""
      }
      index
    }

    meta.isTypeEl = {
      ->
      def key = System.identityHashCode(delegate) + 'typeEl'
      if (!properties.containsKey(key)) {
        def prop = delegate
        def ret = false
        if (Entity.isInstance(prop.type) || BasicType.isInstance(prop.type) || EnumType.isInstance(prop.type)) {
          ret = true
        }
        properties[key] = ret
      }
      properties[key]
    }

    meta.isTypeEjb = {
      ->
      def key = System.identityHashCode(delegate) + 'typeEjb'
      if (!properties.containsKey(key)) {
        def prop = delegate
        def ret = false
        if (Entity.isInstance(prop.type) || BasicType.isInstance(prop.type)) {
          ret = true
        }
        properties[key] = ret
      }
      properties[key]
    }

    meta.isTypeEntity = {
      ->
      def key = System.identityHashCode(delegate) + 'typeEntity'
      if (!properties.containsKey(key)) {
        def prop = delegate
        def ret = false
        if (Entity.isInstance(prop.type)) {
          ret = true
        }
        properties[key] = ret
      }
      properties[key]
    }

    meta.isTypeContainer = {
      ->
      def key = System.identityHashCode(delegate) + 'typeContainer'
      if (!properties.containsKey(key)) {
        def prop = delegate
        def ret = false
        if (Container.isInstance(prop.type)) {
          ret = true
        }
        properties[key] = ret
      }
      properties[key]
    }

    meta.isTypeController = {
      ->
      def key = System.identityHashCode(delegate) + 'typeController'
      if (!properties.containsKey(key)) {
        def prop = delegate
        def ret = false
        if (Controller.isInstance(prop.type)) {
          ret = true
        }
        properties[key] = ret
      }
      properties[key]
    }

    meta.isTypeFacade = {
      ->
      def key = System.identityHashCode(delegate) + 'typeFacade'
      if (!properties.containsKey(key)) {
        def prop = delegate
        def ret = false
        if (Facade.isInstance(prop.type)) {
          ret = true
        }
        properties[key] = ret
      }
      properties[key]
    }

    meta.isTypeCompilationUnit = {
      ->
      def key = System.identityHashCode(delegate) + 'typeCompilationUnit'
      if (!properties.containsKey(key)) {
        def prop = delegate
        def ret = false
        if (CompilationUnit.isInstance(prop.type)) {
          ret = true
        }
        properties[key] = ret
      }
      properties[key]
    }

    meta.isTypeBasicType = {
      ->
      def key = System.identityHashCode(delegate) + 'typeBasicType'
      if (!properties.containsKey(key)) {
        def prop = delegate
        def ret = false
        if (BasicType.isInstance(prop.type)) {
          ret = true
        }
        properties[key] = ret
      }
      properties[key]
    }

    meta.isTypeDate = {
      ->
      def key = System.identityHashCode(delegate) + 'typeDate'
      if (!properties.containsKey(key)) {
        def prop = delegate
        def ret = false
        if (Date.isInstance(prop.type))
          ret = true
        properties[key] = ret
      }
      properties[key]
    }

    meta.isElementCollection = {
      ->
      def key = System.identityHashCode(delegate) + 'elementCollection'
      if (!properties.containsKey(key)) {
        def prop = delegate
        def ret = false
        if (!Entity.isInstance(prop.type) && prop.multi) {
          ret = true
        }
        properties[key] = ret
      }
      properties[key]
    }

    meta.isPrimitive = {
      ->
      def key = System.identityHashCode(delegate) + 'primitive'
      if (!properties.containsKey(key)) {
        def prop = delegate
        def ret = false
        if (prop.type.name.matches("boolean|double|float|int|long|short|char")) {
          ret = true
        }
        properties[key] = ret
      }
      properties[key]
    }




    meta = LogicUnit.metaClass

    meta.getCall = {
      ->
      def key = System.identityHashCode(delegate) + 'call'
      if (!properties.containsKey(key)) {
        properties[key] = delegate.paramsCustom.collect { it.uncap }.join(', ')
      }
      properties[key]
    }

    meta.getSignature = {
      ->
      def key = System.identityHashCode(delegate) + 'signature'
      if (!properties.containsKey(key)) {
        properties[key] = delegate.paramsCustom.collect { it.signature }.join(', ')
      }
      properties[key]
    }

    meta.getSignatureName = {
      ->
      def key = System.identityHashCode(delegate) + 'signatureName'
      if (!properties.containsKey(key)) {
        properties[key] = delegate.paramsCustom.collect { it.name }.join(', ')
      }
      properties[key]
    }

    meta.signature = { Context c ->
      //register usage of the type, in order to calculate imports, etc.

      delegate.params.each {
        if (it.type && !(it.type.name).equals(c.item.name)) {
          c.name(it.type)
        }
      }

      delegate.signature
    }

    meta.getParamsName = {
      ->
      def key = System.identityHashCode(delegate) + 'paramsName'
      if (!properties.containsKey(key)) {
        properties[key] = delegate.paramsCustom.collect { it.cap }.join('And')
      }
      properties[key]
    }




    meta = Attribute.metaClass

    meta.getSignature = {
      ->
      def key = System.identityHashCode(delegate) + 'signature'
      if (!properties.containsKey(key)) {
        //use 'name', because of primitive types
        properties[key] = "$delegate.type.name $delegate.uncap"
      }
      properties[key]
    }

    meta.signature = { Context c ->
      //register usage of the type, in order to calculate imports, etc.
      c.name(delegate.type)
      delegate.signature
    }

    meta.resolveValue = { Context c ->
      def ret
      def value = "$delegate.value"
      if (delegate.prop == null) {
        ret = value
      } else {
        if (delegate.value[0].equals('#')) {
          ret = "this.$delegate.prop.uncap = " + resolveMacro(c, value)
        } else {
          ret = "this.$delegate.prop.uncap = " + value
        }
      }
    }



    meta = Param.metaClass

    meta.compareMethod = { Context c ->
      def key = System.identityHashCode(delegate) + 'compareMethod'
      if (!properties.containsKey(key)) {
        def ret = "${c.name('areEquals')}"
        switch (delegate.compare) {
          case '<=':
            ret = "${c.name('lessOrEqual')}"
            break
          case '<':
            ret = "${c.name('less')}"
            break
          case '=>':
            ret = "${c.name('greaterOrEqual')}"
            break
          case '>':
            ret = "${c.name('greater')}"
            break
        }
        properties[key] = ret
      }
      properties[key]
    }

    meta.getParamName = {
      def prop = delegate.prop
      prop.typeEntity ? "${prop.nameWithIdIfRelation}" : "${prop.name}"
    }





    meta = Body.metaClass

    meta.resolveBody = { Context c ->
      def ret
      def body = "$delegate.body"
      if (delegate.body == null) {
        ret = ''
      } else {
        if (delegate.body[0].equals('#')) {
          ret = resolveMacro(c, body)
        } else {
          ret = body
        }
      }
      ret
    }


    meta = Widget.metaClass

    meta.getMlKeyName = {
      ->
      def key = System.identityHashCode(delegate) + 'mlKeyName'
      if (!properties.containsKey(key)) {
        def parent = delegate.parent
        properties[key] = "${parent.underscored}_$delegate.underscored_${widgetTypeShort}"
      }
      properties[key]
    }

    meta.getMlKeyConstant = {
      ->
      def key = System.identityHashCode(delegate) + 'mlKeyConstant'
      if (!properties.containsKey(key)) {
        properties[key] = delegate.mlKeyName.toUpperCase()
      }
      properties[key]
    }

    meta = Control.metaClass

    meta.getGuidWidget = {
      ->
      def key = System.identityHashCode(delegate) + 'guidWidget'
      if (!properties.containsKey(key)) {
        def ret = ''
        def widget = delegate
        if (Button.isInstance(widget)) {
          ret = 'PushButton'
        } else if (ComboBox.isInstance(widget)) {
          ret = 'TSRAStyleComboBoy'
        } else if (ContextMenu.isInstance(widget)) {
          ret = 'PopupMenu'
        } else if (GroupContentFrame.isInstance(widget)) {
          ret = 'TSRAStyleGroupBoxContentFrame'
        } else if (CheckBox.isInstance(widget)) {
          ret = 'CheckBox'
        } else if (DateField.isInstance(widget)) {
          ret = 'TSRAStyleCalendarDatePickerWidget'
        } else if (Header.isInstance(widget)) {
          ret = 'TSRAStyleHeader'
        } else if (Label.isInstance(widget)) {
          ret = 'Label'
        } else if (Panel.isInstance(widget)) {
          ret = 'StackedWidget'
        } else if (Spinner.isInstance(widget)) {
          ret = 'SpinBox'
        } else if (Table.isInstance(widget)) {
          ret = 'TSRAStyleTable'
        } else if (TextField.isInstance(widget)) {
          ret = 'TSRAStyleLineEdit'
        } else if (TimeField.isInstance(widget)) {
          ret = 'TSRAStyleTimeEdit'
        } else if (GroupBoxHeader.isInstance(widget)) {
          ret = 'TSRAStyleGroupBoxHeader'
        }
        properties[key] = ret
      }
      properties[key]
    }

    meta.getListener = {
      ->
      def key = System.identityHashCode(delegate) + 'listener'
      if (!properties.containsKey(key)) {
        def ret = []
        delegate.children.each {
          if (Listener.isInstance(it))
            ret.add(it)
        }
        properties[key] = ret
      }
    }



    meta = Listener.metaClass

    meta.getGuidoEvent = {
      ->
      def key = System.identityHashCode(delegate) + 'guidoEvent'
      if (!properties.containsKey(key)) {
        def ret = ''
        def listener = delegate
        def widget = delegate.parent
        if (Button.isInstance(widget))
          ret = 'Clicked'
        else if (ComboBox.isInstance(widget))
          ret = 'ActivatedIndex'
        else if (CheckBox.isInstance(widget))
          ret = 'StateChanged'
        else if (ContextMenu.isInstance(widget))
          ret = 'ActivatedPopupMenuItem'
        else if (DateField.isInstance(widget))
          ret = 'DateChanged'
        else if (Spinner.isInstance(widget))
          ret = 'ValueChanged'
        else if (Table.isInstance(widget)) {
          if (OnContextMenuRequest.isInstance(listener))
            ret = 'ContextMenuRequested'
          else if (OnSelect.isInstance(listener))
            ret = 'SelectionChanged'
          else if (OnItemEditorItemSelect.isInstance(listener))
            ret = 'ItemEditorItemSelected'
        } else if (TextField.isInstance(widget))
          ret = 'TextChanged'
        else if (TimeField.isInstance(widget))
          ret = 'TimeChanged'
        properties[key] = ret
      }
      properties[key]
    }




    meta = Channel.metaClass

    meta.metasForBridge = { Context c ->
      Channel channel = delegate
      ModelBuilder builder = channel.component.builder
      def className = DerivedName.isInstance(c.className) ? c.className.name : c.className
      def metasForBridge = []
      if (!(className.contains('Mdb')) && (className.contains('JmsToCdi') || className.contains('EventToCdi') || className.contains('NotificationPlugin'))) {
        meta.metasForBridge = builder.meta(type: 'ApplicationScoped')
        def supportsEnvironments = builder.meta(type: 'SupportsEnvironments', value: [])
        def environment = builder.meta(type: 'Environment', value: [:])
        environment.value['executions'] = "{ ${c.name('PRODUCTIVE')} }"
        if (className.contains('EventToCdi'))
          environment.value['runtimes'] = "{ ${c.name('CLIENT')}, SERVER }"
        else if (className.contains('External'))
          environment.value['runtimes'] = "{ ${c.name('SERVER')} }"
        else
          environment.value['runtimes'] = "{ ${c.name('CLIENT')} }"
        supportsEnvironments.value.add(environment)
        meta.metasForBridge = supportsEnvironments
        meta.metasForBridge = builder.meta(type: 'Traceable')
      } else if (className.contains('CdiToJms')) {
        meta.metasForBridge = builder.meta(type: 'Stateless')
        def supportsEnvironments = builder.meta(type: 'SupportsEnvironments', value: [])
        def environment = builder.meta(type: 'Environment', value: [:])
        environment.value['runtimes'] = '{ SERVER }'
        supportsEnvironments.value.add(environment)
        meta.metasForBridge = supportsEnvironments
      } else {
        def messageDriven = builder.meta(type: 'MessageDriven', value: [:])
        messageDriven.value['messageListenerInterface'] = "${c.name('MessageListener')}.class"
        def configProps = []
        def destinationValue, destinationTypeValue
        if (className.contains('Import')) {
          destinationValue = 'JMS_IMPORT_QUEUE'
          destinationTypeValue = 'QUEUE'
        } else {
          destinationValue = 'JMS_NOTIFICATION_TOPIC'
          destinationTypeValue = 'TOPIC'
        }

        def connectionFactory = builder.meta(type: 'ActivationConfigProperty', value: [:])
        connectionFactory.value['propertyName'] = 'CONNECTION_FACTORY_JNDI_NAME'
        connectionFactory.value['propertyValue'] = "${module.cap}Constants.JMS_CONNECTION_FACTORY"

        def destinationJndi = builder.meta(type: 'ActivationConfigProperty', value: [:])
        destinationJndi.value['propertyName'] = 'DESTINATION_JNDI_NAME'
        destinationJndi.value['propertyValue'] = "${module.cap}Constants.$destinationValue"

        def destination = builder.meta(type: 'ActivationConfigProperty', value: [:])
        destination.value['propertyName'] = 'DESTINATION'
        destination.value['propertyValue'] = "${module.cap}Constants.$destinationValue"

        def destinationType = builder.meta(type: 'ActivationConfigProperty', value: [:])
        destinationType.value['propertyName'] = 'DESTINATION_TYPE'
        destinationType.value['propertyValue'] = "$destinationTypeValue"

        def topicMessages = builder.meta(type: 'ActivationConfigProperty', value: [:])
        topicMessages.value['propertyName'] = 'TOPIC_MESSAGES_DISTRIBUTION_MODE'
        topicMessages.value['propertyValue'] = 'ONE_COPY'

        def distributedDestination = builder.meta(type: 'ActivationConfigProperty', value: [:])
        distributedDestination.value['propertyName'] = 'DISTRIBUTED_DESTINATION_CONNECTION'
        distributedDestination.value['propertyValue'] = 'EVERY_MEMBER'

        def messageSelector = builder.meta(type: 'ActivationConfigProperty', value: [:])
        messageSelector.value['propertyName'] = 'MESSAGE_SELECTOR'
        if (className.contains('ImportData'))
          messageSelector.value['propertyValue'] = "datatype = '" + "${module.shared.names.constants}.JMS_MESSAGE_SELECTOR_${item.underscored}_DATA" + "'"
        else if (className.contains('Import'))
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
        meta.metasForBridge = messageDriven
        meta.metasForBridge = supportsEnvironments
      }
      metasForBridge
    }




    meta = MetaAttribute.metaClass

    meta.annotation = { Context c ->
      def key = System.identityHashCode(delegate) + 'annotation'
      if (!properties.containsKey(key)) {
        String newLine = System.properties['line.separator']
        def ret = ''
        if (c.propMeta) {
          ret = "  @${c.name(delegate.type)}"
        } else {
          ret = "@${c.name(delegate.type)}"
        }
        if (delegate.multi && delegate.value) {
          ret += '({'
          ret += delegate.value.collect { '\n    ' + it.annotation(c) }.join(', ')
          ret += '})'
        } else if (delegate.value) {
          if (Map.isInstance(delegate.value)) {
            if (delegate.type.cap == 'NamedQuery') {
              ret += '(' + delegate.value.collect { k, v -> "$k = $v" }.join(', ' + newLine + '                ') + ')'
            } else if (delegate.type.cap == 'JoinTable') {
              ret += '(' + delegate.value.collect { k, v -> "$k = $v" }.join(', ' + newLine + '             ') + ')'
            } else {
              ret += '(' + delegate.value.collect { k, v -> "$k = $v" }.join(', ') + ')'
            }
          } else if (MetaAttribute.isInstance(delegate.value[0])) {
            ret += "(${delegate.value[0].annotation(c)})"
          } else {
            ret += "($delegate.value)"
          }
        } else if (delegate.multi) {
          ret += '({' + newLine + '})'
        }
        properties[key] = ret
      }
      properties[key]
    }
  }
}