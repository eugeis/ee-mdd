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
import ee.mdd.generator.FacetTemplateLoader
import ee.mdd.generator.Generator
import ee.mdd.generator.ProcessorsFactory
import ee.mdd.model.component.Model
import ee.mdd.model.component.OperationRef
import ee.mdd.model.component.Prop

/**
 *
 * @author Eugen Eisler
 */
class GeneratorForJava {

  static void main(def args) {
    String mainResources = "${args[0]}/../ee-mdd-core/src/main/resources"
    String testResources = "${args[0]}/../ee-mdd-core/src/test/resources"

    String target = args ? new File("${args[0]}/../ee-mdd-example_java") : '/Users/eugeis/git/ee-mdd/ee-mdd-example_java'

    ModelBuilder builder = new ModelBuilder()
    Model model =  builder.buildFromClasspath("/model.groovy" )

    println args
    EnhancerForJava.enhanceClasses()


    if(model) {
      //create props for delegates
      model.findAllRecursiveDown { OperationRef.isInstance(it) }.each { OperationRef d ->
        d.parent.add( new Prop(name: d.ref.parent.uncap, type: d.ref.parent) ) }

      //model.findAllRecursiveDown { Component.isInstance(it) }.each { it.add(new Init) }

      builder.refAttrResolver.printNotResolved()


      FacetTemplateLoader templateLoader = new FacetTemplateLoader()

      Generator generator = new Generator()
      generator.add(templateLoader.loadFacetTemplates(model))

      def commonProcessorFactory = new ProcessorsFactory()
      def javaProcessorFactory = new ProcessorsForJava(refToElement: builder.refAttrResolver.refToElement)

      generator.add(commonProcessorFactory.macrosProcessor(templateLoader.load('/java/', 'macros')))

      generator.add(javaProcessorFactory.javaImportsPathProcessor())
      generator.add(commonProcessorFactory.printProcessor())
      generator.add(commonProcessorFactory.fileProcessor(target))

      Context c = new Context(name: model.name)
      c.model = model

      generator.generate(c)
    }
  }
}
