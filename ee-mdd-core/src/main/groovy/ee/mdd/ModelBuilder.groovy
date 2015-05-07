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

import ee.mdd.builder.AbstractFactoryBuilder
import ee.mdd.builder.CompositeFactory
import ee.mdd.builder.EnumTypeFactory
import ee.mdd.builder.FacetAwareFactory
import ee.mdd.builder.FacetFactory
import ee.mdd.builder.Facets
import ee.mdd.builder.MddFactory
import ee.mdd.builder.MetaAttributeHolder
import ee.mdd.builder.ModelFactory
import ee.mdd.builder.OppositeResolveHandler
import ee.mdd.builder.PropFactory
import ee.mdd.model.Body
import ee.mdd.model.Element
import ee.mdd.model.component.Attribute
import ee.mdd.model.component.BasicType
import ee.mdd.model.component.Channel
import ee.mdd.model.component.Commands
import ee.mdd.model.component.CompilationUnit
import ee.mdd.model.component.Component
import ee.mdd.model.component.ConditionParam
import ee.mdd.model.component.Config
import ee.mdd.model.component.Constructor
import ee.mdd.model.component.Container
import ee.mdd.model.component.Controller
import ee.mdd.model.component.Count
import ee.mdd.model.component.Create
import ee.mdd.model.component.DataType
import ee.mdd.model.component.DataTypeOperation
import ee.mdd.model.component.DataTypeProp
import ee.mdd.model.component.Delete
import ee.mdd.model.component.Entity
import ee.mdd.model.component.EnumType
import ee.mdd.model.component.Exist
import ee.mdd.model.component.ExternalModule
import ee.mdd.model.component.ExternalType
import ee.mdd.model.component.Facet
import ee.mdd.model.component.Find
import ee.mdd.model.component.Finders
import ee.mdd.model.component.Index
import ee.mdd.model.component.Initializer
import ee.mdd.model.component.Literal
import ee.mdd.model.component.LogicUnit
import ee.mdd.model.component.Message
import ee.mdd.model.component.MetaAttribute
import ee.mdd.model.component.Model
import ee.mdd.model.component.Module
import ee.mdd.model.component.Namespace
import ee.mdd.model.component.Operation
import ee.mdd.model.component.OperationRef
import ee.mdd.model.component.Param
import ee.mdd.model.component.Pojo
import ee.mdd.model.component.Prop
import ee.mdd.model.component.Service
import ee.mdd.model.component.StructureUnit
import ee.mdd.model.component.Type
import ee.mdd.model.component.TypeRef
import ee.mdd.model.component.Update

/**
 *
 * @author Eugen Eisler
 */
class ModelBuilder extends AbstractFactoryBuilder {
  private Facets facets = new Facets()
  private def type = new CompositeFactory(beanClass: Type, childFactories: ['meta'])
  private def channel = new CompositeFactory(beanClass: Channel, childFactories: ['meta', 'message'])
  private def cu = new CompositeFactory(beanClass: CompilationUnit, childFactories: ['constr', 'prop', 'op', 'delegate'], parent: type)
  private def dataType = new CompositeFactory(beanClass: DataType, childFactories: ['finder', 'commands', 'index'], parent: cu)
  private def typeRef = new CompositeFactory(beanClass: TypeRef)
  private def message = new CompositeFactory(beanClass: Message,  childFactories: ['meta'], parent: typeRef)
  private def basicType = new CompositeFactory(beanClass: BasicType, parent: dataType)
  private def body = new CompositeFactory(beanClass: Body, childFactories: ['type'])
  private def pojo = new CompositeFactory(beanClass: Pojo, parent: cu)
  private def lu = new CompositeFactory(beanClass: LogicUnit, childFactories: ['meta', 'param'], parent: body)
  private def attr = new CompositeFactory(beanClass: Attribute, childFactories: ['meta'])
  private def param = new CompositeFactory(beanClass: Param, parent: attr)
  private def operation = new CompositeFactory(beanClass: Operation, parent: lu)
  private def operationRef = new CompositeFactory(beanClass: OperationRef, valueProperty: 'ref', parent: operation)
  private def facet = new FacetFactory(beanClass: Facet, childFactories: ['extModule', 'facet'], facets: facets)
  private def su = new FacetAwareFactory(beanClass: StructureUnit, childFactories: ['facet', 'namespace'], facets: facets)
  private def commands = new CompositeFactory(beanClass: Commands, childFactories: ['create', 'delete', 'update'], parent: controller)
  private def component = new CompositeFactory(beanClass: Component, childFactories: ['module'], parent: su)
  private def condition = new CompositeFactory(beanClass: ConditionParam, parent: param)
  private def config = new CompositeFactory(beanClass: Config, parent: dataType)
  private def constructor = new CompositeFactory(beanClass: Constructor, parent: lu)
  private def container = new CompositeFactory(beanClass: Container, childFactories: ['controller'], parent: dataType)
  private def controller = new CompositeFactory(beanClass: Controller, parent: cu)
  private def index = new CompositeFactory(beanClass: Index)
  private def initializer = new CompositeFactory(beanClass: Initializer, parent: controller)
  private def dataTypeOperation = new CompositeFactory(beanClass: DataTypeOperation, parent: operation)
  private def counter = new CompositeFactory(beanClass: Count, parent: dataTypeOperation)
  private def create = new CompositeFactory(beanClass: Create, parent: dataTypeOperation)
  private def delete = new CompositeFactory(beanClass: Delete, parent: dataTypeOperation)
  private def update = new CompositeFactory(beanClass: Update, parent: dataTypeOperation)
  private def entity = new CompositeFactory(beanClass: Entity, parent: dataType)
  private def enumType = new EnumTypeFactory(beanClass: EnumType, childFactories: ['lit'], parent: dataType)
  private def exist = new CompositeFactory(beanClass: Exist, parent: dataTypeOperation)
  private def externalType = new CompositeFactory(beanClass: ExternalType, parent: type)
  private def find = new CompositeFactory(beanClass: Find, parent: dataTypeOperation)
  private def finder = new CompositeFactory(beanClass: Finders, childFactories: ['exist', 'count', 'findBy'], parent: controller)
  private ModelFactory model = new ModelFactory(childFactories: ['model', 'component'], parent: su)
  private def metaAttribute = new CompositeFactory(beanClass: MetaAttribute, parent: attr)
  private CompositeFactory module = new CompositeFactory(beanClass: Module, childFactories: ['entity', 'basicType', 'enumType', 'pojo', 'config', 'controller', 'service', 'container', 'channel', 'dependency'], parent: su)
  private def externalModule = new CompositeFactory(beanClass: ExternalModule, childFactories: ['extType'], parent: module)
  private def prop = new PropFactory(parent: attr)
  private def literal = new CompositeFactory(beanClass: Literal)
  private def service = new CompositeFactory(beanClass: Service, parent: cu)
  private def namespace = new MddFactory(beanClass: Namespace)

  ModelBuilder(Closure postInstantiateDelegate = null) {
    super(postInstantiateDelegate)

    OppositeResolveHandler oppositeResolver = refAttrResolver.addResolver(new OppositeResolveHandler(name: 'opposite'))
    refAttrResolver.addResolver(oppositeResolver)

    refAttrResolver.addGlobalResolver('type', Type, null, false, { prop, resolved ->
      if(DataTypeProp.isInstance(prop) && DataType.isInstance(resolved) && !prop.opposite) {
        oppositeResolver.onDataTypeProp(prop)
      }
    })
    refAttrResolver.addGlobalResolver('ret', Type)
    refAttrResolver.addGlobalResolver('ref', Element)

    refAttrResolver.addParentResolver('prop', Prop, 2)
    refAttrResolver.addGlobalResolver('module', Module)
    refAttrResolver.addGlobalResolver('superUnit', CompilationUnit)

    MetaAttributeHolder metaAttributeHolder = new MetaAttributeHolder()
    refAttrResolver.addGlobalResolver('meta', Type, metaAttributeHolder.&forType, true)
    refAttrResolver.addParentResolver('props', Prop, 2, null, true)

    refAttrResolver.addGlobalTypes([Model, Module, Component, Type, CompilationUnit])

    facets.names.each { facetName -> registerFactory facetName, new FacetFactory(facetName: facetName, facets: facets, parent: facet) }
    
    reg()
  }

  void reg() {

    registerFactory 'basicType', basicType
    registerFactory 'body', body
    registerFactory 'cond', condition
    registerFactory 'component', component
    registerFactory 'config', config
    registerFactory 'constr', constructor
    registerFactory 'container', container
    registerFactory 'controller', controller
    registerFactory 'delegate', operationRef
    registerFactory 'index', index
    registerFactory 'initializer', initializer
    registerFactory 'count', counter
    registerFactory 'create', create
    registerFactory 'delete', delete
    registerFactory 'entity', entity
    registerFactory 'enumType', enumType
    registerFactory 'exist', exist
    registerFactory 'extType', externalType
    registerFactory 'facet', facet
    registerFactory 'findBy', find
    registerFactory 'meta', metaAttribute
    registerFactory 'model', model
    registerFactory 'finder', finder
    registerFactory 'commands', commands
    registerFactory 'module', module
    registerFactory 'extModule', externalModule
    registerFactory 'op', operation
    registerFactory 'param', param
    registerFactory 'pojo', pojo
    registerFactory 'prop', prop
    registerFactory 'lit', literal
    registerFactory 'service', service
    registerFactory 'update', update
    registerFactory 'namespace', namespace
    registerFactory 'channel', channel
    registerFactory 'message', message
  }
}
