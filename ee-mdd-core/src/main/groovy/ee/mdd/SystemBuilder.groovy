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
import ee.mdd.builder.MddFactory
import ee.mdd.model.component.Service
import ee.mdd.model.system.Content
import ee.mdd.model.system.Machine
import ee.mdd.model.system.Workspace

/**
 *
 * @author Eugen Eisler
 */
class SystemBuilder extends AbstractFactoryBuilder {

  SystemBuilder(Closure postInstantiateDelegate = null) {
    super(postInstantiateDelegate)

    refAttrResolver.addGlobalResolver('machine', Machine)
    refAttrResolver.addGlobalResolver('workspace', Workspace)
    refAttrResolver.addGlobalResolver('service', Service)

    refAttrResolver.addGlobalTypes([Machine, Workspace, Service])

    if(postInstantiateDelegate) {
      super.addPostInstantiateDelegate(postInstantiateDelegate)
    }
  }

  void registerAll() {
    def factorySystem = new CompositeFactory(beanClass: System, childFactories: ['machine'])
    def factoryMachine = new CompositeFactory(beanClass: Machine, childFactories: ['workspace', 'service'])
    def factoryWorkspace = new CompositeFactory(beanClass: Workspace, childFactories: ['content', 'service'])
    def factoryContent = new MddFactory(beanClass: Content, childFactories: ['content'])
    def factoryService = new MddFactory(beanClass: Service, childFactories: ['service'])

    registerFactory 'system', factorySystem
    registerFactory 'machine', factoryMachine
    registerFactory 'workspace', factoryWorkspace
    registerFactory 'service', factoryService
  }
}

