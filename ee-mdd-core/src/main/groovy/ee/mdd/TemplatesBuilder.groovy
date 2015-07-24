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
import ee.mdd.builder.MethodCallFactory
import ee.mdd.builder.TemplateGroupFactory
import ee.mdd.generator.Generator
import ee.mdd.generator.Processor
import ee.mdd.generator.Template
import ee.mdd.model.Body

/**
 *
 * @author Eugen Eisler
 */
class TemplatesBuilder extends AbstractFactoryBuilder {

  void registerAll() {
    def factoryGenerator = new CompositeFactory(beanClass: Generator, childFactories: ['templates', 'processor'])

    def templates = new TemplateGroupFactory(childFactories: ['templates', 'template', 'processor'])

    def processor = new CompositeFactory(beanClass: Processor, childFactories: [])
    def template = new CompositeFactory(beanClass: Template, childFactories: ['type', 'processor'])
    def body = new CompositeFactory(beanClass: Body, childFactories: ['type'])
    def methodCall = new MethodCallFactory()

    registerFactory 'generator', factoryGenerator
    registerFactory 'templates', templates
    registerFactory 'template', template
    registerFactory 'processor', processor
    registerFactory 'body', body
  }

  void useMacros(String alias, String fullName = null) {
    current.useMacros(alias, fullName)
  }
}

