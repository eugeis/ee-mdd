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
package ee.mdd.templates.java

import ee.mdd.builder.ModelBuilder
import ee.mdd.generator.Processors
import ee.mdd.model.component.Model
import ee.mdd.model.component.OperationRef
import ee.mdd.model.component.Prop
import ee.mdd.model.component.java.Cdi
import ee.mdd.model.component.java.Cg
import ee.mdd.model.component.java.Java
import ee.mdd.model.component.java.Jms
import ee.mdd.model.component.java.Jpa
import ee.mdd.model.component.java.Test
import ee.mdd.templates.java.cg.TemplatesForJavaCg

/**
 *
 * @author Eugen Eisler
 */
class GeneratorForJava {

  static void main(def args) {
    String target = args ? new File("${args[0]}/../ee-mdd-example_java") : '/Users/eugeis/git/ee-mdd/ee-mdd-example_java'

    println args
    EnhancerForJava.enhanceClasses()

    def builder = new ModelBuilder()
    registerFacets(builder)

    Model model =  ModelBuilderExample.build (builder)

    //create props for delegates
    model.findAllRecursiveDown { OperationRef.isInstance(it) }.each { OperationRef d ->
      d.parent.add( new Prop(name: d.ref.parent.uncap, type: d.ref.parent) ) }

    //model.findAllRecursiveDown { Component.isInstance(it) }.each { it.add(new Init) }

    builder.refAttrResolver.printNotResolved()

    def generator = TemplatesForJavaCg.build()
    def commonProcessorFactory = new Processors()
    def javaProcessorFactory = new ProcessorsForJava(refToElement: builder.refAttrResolver.refToElement)

    generator.add(commonProcessorFactory.macrosProcessor(MacrosForJava.build()))
    generator.add(javaProcessorFactory.javaImportsPathProcessor())
    generator.add(commonProcessorFactory.printProcessor())
    generator.add(commonProcessorFactory.fileProcessor(target))
    generator.generate(model)
  }

  private static registerFacets(ModelBuilder builder) {
    builder.registerFacet(Java)
    builder.registerFacet(Jms)
    builder.registerFacet(Jpa)
    builder.registerFacet(Cdi)
    builder.registerFacet(Test)
    builder.registerFacet(Cg)
  }
}
