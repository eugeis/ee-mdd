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
package ee.mdd.builder

import java.beans.Introspector

import ee.mdd.factory.*
import ee.mdd.model.*
import ee.mdd.model.component.Attribute
import ee.mdd.model.component.BasicType
import ee.mdd.model.component.Body
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
import ee.mdd.model.component.Delegate
import ee.mdd.model.component.Delete
import ee.mdd.model.component.Entity
import ee.mdd.model.component.EnumType
import ee.mdd.model.component.Exist
import ee.mdd.model.component.ExternalType
import ee.mdd.model.component.Facet
import ee.mdd.model.component.Find
import ee.mdd.model.component.Index
import ee.mdd.model.component.Initializer
import ee.mdd.model.component.Literal
import ee.mdd.model.component.LogicUnit
import ee.mdd.model.component.Manager
import ee.mdd.model.component.MetaAttribute
import ee.mdd.model.component.Model
import ee.mdd.model.component.Module
import ee.mdd.model.component.Operation
import ee.mdd.model.component.Param
import ee.mdd.model.component.Pojo
import ee.mdd.model.component.Prop
import ee.mdd.model.component.Service
import ee.mdd.model.component.StructureUnit
import ee.mdd.model.component.Type
import ee.mdd.model.component.Update

/**
 *
 * @author Eugen Eisler
 */
class ModelBuilder extends AbstractFactoryBuilder {

	ModelBuilder(Closure postInstantiateDelegate = null) {
		super(postInstantiateDelegate)

		refAttrResolver.addGlobalResolver('type', Type)
		refAttrResolver.addGlobalResolver('ret', Type)
		refAttrResolver.addGlobalResolver('ref', Element)
		refAttrResolver.addParentResolver('prop', Prop)
		refAttrResolver.addGlobalResolver('module', Module)
		refAttrResolver.addGlobalResolver('superUnit', CompilationUnit)

		MetaAttributeHolder metaAttributeHolder = new MetaAttributeHolder()
		refAttrResolver.addGlobalResolver('meta', Type, metaAttributeHolder.&forType, true)

		refAttrResolver.addGlobalTypes([Model, Module, Component, Type, CompilationUnit])
	}

	void registerAll() {
		def type = new CompositeFactory(beanClass: Type, childFactories: ['meta'])
		def cu = new CompositeFactory(beanClass: CompilationUnit, childFactories: ['constr', 'prop', 'op', 'delegate'], parent: type)
		def dataType = new CompositeFactory(beanClass: DataType, childFactories: ['manager', 'index'], parent: cu)
		def basicType = new CompositeFactory(beanClass: BasicType, parent: dataType)
		def body = new CompositeFactory(beanClass: Body, childFactories: ['type'])
		def pojo = new CompositeFactory(beanClass: Pojo, parent: cu)
		def lu = new CompositeFactory(beanClass: LogicUnit, childFactories: ['meta', 'param'], parent: body)
		def attr = new CompositeFactory(beanClass: Attribute, childFactories: ['meta'])
		def param = new CompositeFactory(beanClass: Param, parent: attr)
		def operation = new CompositeFactory(beanClass: Operation, parent: lu)
		def dataTypeOperation = new CompositeFactory(beanClass: DataTypeOperation, parent: operation)
		def facet = new CompositeFactory(beanClass: Facet, childFactories: ['extType'])
		def su = new CompositeFactory(beanClass: StructureUnit, childFactories: ['facet', 'extType', 'namespace'])
		def component = new CompositeFactory(beanClass: Component, childFactories: ['module'], parent: su)
		def condition = new CompositeFactory(beanClass: ConditionParam, parent: param)
		def config = new CompositeFactory(beanClass: Config, parent: dataType)
		def constructor = new CompositeFactory(beanClass: Constructor, parent: lu)
		def container = new CompositeFactory(beanClass: Container, parent: dataType)
		def controller = new CompositeFactory(beanClass: Controller, parent: cu)
		def index = new CompositeFactory(beanClass: Index)
		def initializer = new CompositeFactory(beanClass: Initializer, parent: controller)
		def counter = new CompositeFactory(beanClass: Count, parent: dataTypeOperation)
		def create = new CompositeFactory(beanClass: Create, parent: dataTypeOperation)
		def delete = new CompositeFactory(beanClass: Delete, parent: dataTypeOperation)
		def entity = new CompositeFactory(beanClass: Entity, parent: dataType)
		def enumType = new EnumTypeFactory(beanClass: EnumType, childFactories: ['lit'], parent: dataType)
		def exist = new CompositeFactory(beanClass: Exist, parent: dataTypeOperation)
		def externalType = new CompositeFactory(beanClass: ExternalType, parent: type)
		def finder = new CompositeFactory(beanClass: Find, parent: dataTypeOperation)
		def model = new ModelFactory(childFactories: ['model', 'component'], parent: su)
		def manager = new CompositeFactory(beanClass: Manager, childFactories: ['create', 'delete', 'exist', 'count', 'find'], parent: controller)
		def metaAttribute = new CompositeFactory(beanClass: MetaAttribute, parent: attr)
		def module = new CompositeFactory(beanClass: Module, childFactories: ['entity', 'basicType', 'enumType', 'pojo', 'config', 'controller', 'service'], parent: su)
		def delegateOp = new CompositeFactory(beanClass: Delegate, valueProperty: 'ref', parent: operation)
		def prop = new PropFactory(parent: attr)
		def literal = new CompositeFactory(beanClass: Literal)
		def service = new CompositeFactory(beanClass: Service, parent: cu)
		def update = new CompositeFactory(beanClass: Update, parent: dataTypeOperation)

		registerFactory 'basicType', basicType
		registerFactory 'body', body
		registerFactory 'cond', condition
		registerFactory 'component', component
		registerFactory 'config', config
		registerFactory 'constr', constructor
		registerFactory 'container', container
		registerFactory 'controller', controller
		registerFactory 'delegate', delegateOp
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
		registerFactory 'find', finder
		registerFactory 'meta', metaAttribute
		registerFactory 'model', model
		registerFactory 'manager', manager
		registerFactory 'module', module
		registerFactory 'op', operation
		registerFactory 'param', param
		registerFactory 'pojo', pojo
		registerFactory 'prop', prop
		registerFactory 'lit', literal
		registerFactory 'service', service
		registerFactory 'update', update
	}

	void registerFacet(Class beanClass, String name = beanClass.simpleName) {
		registerFacet Introspector.decapitalize(name), beanClass
	}

	void registerFacet(String name, Class beanClass) {
		def newFacet = new CompositeFactory(beanClass: beanClass, parent: getProxyBuilder().factories.get('facet'))
		CompositeFactory model = getProxyBuilder().factories.get('model')
		model.childFactories << name
		CompositeFactory module = getProxyBuilder().factories.get('module')
		module.childFactories << name
		registerFactory name, newFacet
	}
}

