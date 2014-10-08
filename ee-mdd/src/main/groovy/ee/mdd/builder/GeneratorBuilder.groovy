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

import java.util.logging.Logger

import ee.mdd.factory.*
import ee.mdd.generator.CategoryGenerator;
import ee.mdd.generator.Generator;
import ee.mdd.generator.ItemsGenerator;
import ee.mdd.generator.Processor;
import ee.mdd.generator.TemplateGenerator;
import ee.mdd.model.*
import ee.mdd.model.component.Body
import ee.mdd.model.component.Type

/**
 *
 * @author Eugen Eisler
 */
class GeneratorBuilder extends AbstractFactoryBuilder {

	GeneratorBuilder() {
		super([
			'generator',
			'category',
			'items',
			'template'] as Set)
	}

	void registerAll() {
		def factoryGenerator = new CompositeFactory(beanClass: Generator, childFactories: [
			'category',
			'items',
			'processor'
		])
		def factoryItems = new CompositeFactory(beanClass: ItemsGenerator, childFactories: ['template', 'processor'])
		def factoryCategory = new CompositeFactory(beanClass: CategoryGenerator, childFactories: ['template', 'processor'])
		def factoryProcessor = new CompositeFactory(beanClass: Processor, childFactories: [])
		def factoryTemplate = new CompositeFactory(beanClass: TemplateGenerator, childFactories: ['type', 'processor'])
		def factoryBody = new CompositeFactory(beanClass: Body, childFactories: ['type'])
		def factoryType = new CompositeFactory(beanClass: Type, childFactories: [])


		registerFactory 'generator', factoryGenerator
		registerFactory 'category', factoryCategory
		registerFactory 'items', factoryItems
		registerFactory 'template', factoryTemplate
		registerFactory 'processor', factoryProcessor
		registerFactory 'body', factoryBody
		registerFactory 'type', factoryType
	}
}

