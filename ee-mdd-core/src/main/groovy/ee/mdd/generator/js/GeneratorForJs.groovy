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
package ee.mdd.generator.js




import ee.mdd.generator.FacetTemplateLoader
import ee.mdd.generator.Generator
import ee.mdd.generator.GeneratorFactoryBase
import ee.mdd.generator.ProcessorsFactory
import ee.mdd.model.component.Model



/**
 *
 * @author Eugen Eisler
 */
class GeneratorForJs extends GeneratorFactoryBase {

  static {
    EnhancerForJs.enhanceClasses()
  }

  protected extendGenerator(Generator generator, ProcessorsFactory processorFactory, FacetTemplateLoader templateLoader, Closure targetModuleResolver) {
    def jsProcessorFactory = new ProcessorsForJs()
    if(targetModuleResolver) {
      jsProcessorFactory.targetModuleResolver = targetModuleResolver
    }
    generator.add(jsProcessorFactory.jsPathProcessor())
  }
}
