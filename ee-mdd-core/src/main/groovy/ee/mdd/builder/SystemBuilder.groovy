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

import groovy.lang.Closure;

/**
 *
 * @author Eugen Eisler
 */
class SystemBuilder extends AbstractFactoryBuilder {

  SystemBuilder(Closure postInstantiateDelegate = null) {
    super(['system'] as Set)

    refAttrResolver.addGlobalResolver('machine', Machine)
    refAttrResolver.addGlobalResolver('workspace', Workspace)
    refAttrResolver.addGlobalResolver('service', Service)

    refAttrResolver.addGlobalTypes([
      Machine,
      Workspace,
      Service
    ])

    if(postInstantiateDelegate) {
      super.addPostInstantiateDelegate(postInstantiateDelegate)
    }
  }

  void registerAll() {
    def factoryBasicType = new CompositeFactory(beanClass: BasicType, childFactories: [
      'meta',
      'constr',
      'prop',
      'op'
    ])
    def factoryBody = new CompositeFactory(beanClass: Body, childFactories: ['type'])
    def factoryComponent = new CompositeFactory(beanClass: Component, childFactories: ['module', 'facet'])
    def factoryCondition = new CompositeFactory(beanClass: Condition, childFactories: ['type', 'op', 'controller'])
    def factoryConfig = new CompositeFactory(beanClass: Config, childFactories: [
      'meta',
      'constr',
      'prop',
      'op',
      'controller',
      'delegate'
    ])
    def factoryConstructor = new CompositeFactory(beanClass: Constructor, childFactories: ['param', 'body',])
    def factoryContainer = new CompositeFactory(beanClass: Container, childFactories: [
      'meta',
      'constr',
      'prop',
      'op',
      'controller',
      'delegate'
    ])
    def factoryController = new CompositeFactory(beanClass: Controller, childFactories: [
      'meta',
      'constr',
      'prop',
      'op',
      'delegate'
    ])

    def factoryInitializer = new CompositeFactory(beanClass: Controller, childFactories: [
      'meta',
      'constr',
      'prop',
      'op',
      'delegate'
    ])
    def factoryCount = new CompositeFactory(beanClass: Count, childFactories: ['meta', 'cond'])
    def factoryCreate = new CompositeFactory(beanClass: Create, childFactories: ['meta', 'param', 'cond'])
    def factoryDelete = new CompositeFactory(beanClass: Delete, childFactories: ['meta', 'cond'])
    def factoryEntity = new CompositeFactory(beanClass: Entity, childFactories: [
      'meta',
      'constr',
      'prop',
      'op',
      'manager'
    ])
    def factoryEnumType = new EnumTypeFactory(beanClass: EnumType, childFactories: [
      'meta',
      'constr',
      'lit',
      'prop',
      'op'
    ])
    def factoryExist = new CompositeFactory(beanClass: Exist, childFactories: ['meta', 'cond'])
    def factoryExternalType = new CompositeFactory(beanClass: ExternalType, childFactories: ['prop', 'op'])
    def factoryFacet = new CompositeFactory(beanClass: Facet)
    def factoryFind = new CompositeFactory(beanClass: Find, childFactories: ['meta', 'cond'])
    def factoryModel = new CompositeFactory(beanClass: Model, childFactories: [
      'model',
      'component',
      'facet',
      'extType'
    ])
    def factoryManager = new CompositeFactory(beanClass: Manager, childFactories: [
      'meta',
      'prop',
      'op',
      'count',
      'create',
      'delete',
      'exist',
      'find',
      'delegate'
    ])
    def factoryMetaAttribute = new CompositeFactory(beanClass: MetaAttribute, childFactories: [])
    def factoryModule = new CompositeFactory(beanClass: Module, childFactories: [
      'facet',
      'entity',
      'basicType',
      'enumType',
      'pojo',
      'config',
      'extType',
      'controller',
      'service'
    ])
    def factoryOperation = new CompositeFactory(beanClass: Operation, childFactories: ['meta', 'param', 'body'])
    def factoryDelegate = new CompositeFactory(beanClass: Delegate, valueProperty: 'ref', childFactories: ['meta', 'param', 'body'])
    def factoryParam = new CompositeFactory(beanClass: Param, childFactories: ['meta'])
    def factoryPojo = new CompositeFactory(beanClass: Pojo, childFactories: ['meta', 'prop', 'op'])
    def factoryProp = new CompositeFactory(beanClass: Prop, childFactories: ['meta'])
    def factoryLiteral = new CompositeFactory(beanClass: Literal, childFactories: [])
    def factoryService = new CompositeFactory(beanClass: Service, childFactories: [
      'meta',
      'prop',
      'op',
      'delegate'
    ])
    def factoryUpdate = new CompositeFactory(beanClass: Update, childFactories: ['meta', 'param', 'cond'])


    registerFactory 'basicType', factoryBasicType
    registerFactory 'body', factoryBody
    registerFactory 'component', factoryComponent
    registerFactory 'cond', factoryCondition
    registerFactory 'config', factoryConfig
    registerFactory 'constr', factoryConstructor
    registerFactory 'container', factoryContainer
    registerFactory 'controller', factoryController
    registerFactory 'delegate', factoryDelegate
    registerFactory 'initializer', factoryInitializer
    registerFactory 'counter', factoryCount
    registerFactory 'creator', factoryCreate
    registerFactory 'delete', factoryDelete
    registerFactory 'entity', factoryEntity
    registerFactory 'enumType', factoryEnumType
    registerFactory 'exist', factoryExist
    registerFactory 'extType', factoryExternalType
    registerFactory 'facet', factoryFacet
    registerFactory 'find', factoryFind
    registerFactory 'meta', factoryMetaAttribute
    registerFactory 'model', factoryModel
    registerFactory 'manager', factoryManager
    registerFactory 'module', factoryModule
    registerFactory 'op', factoryOperation
    registerFactory 'param', factoryParam
    registerFactory 'pojo', factoryPojo
    registerFactory 'prop', factoryProp
    registerFactory 'lit', factoryLiteral
    registerFactory 'service', factoryService
    registerFactory 'update', factoryUpdate
  }
}

