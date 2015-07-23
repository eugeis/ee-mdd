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
package ee.mdd.generator

import ee.mdd.ModelBuilder
import ee.mdd.model.component.Facet
import ee.mdd.model.component.Model
import ee.mdd.model.component.OperationRef
import ee.mdd.model.component.Prop

/**
 *
 * @author Eugen Eisler
 */
class GeneratorFactoryBase {

  ModelBuilder builder = new ModelBuilder()

  Model loadModel(URL modelSource, Closure facetClosure = null) {
    Model model
    if(facetClosure) {
      Facet facet =  builder.build(facetClosure)
      model =  builder.build(modelSource)
      if(facet) {
        model.add(facet)
        facet.extendModel(model)
      }
    } else {
      model =  builder.build(modelSource)
    }

    builder.typeResolver.printNotResolved()
    model
  }

  Model deriveModel(Model model) {
    //create props for delegates
    model.findAllRecursiveDown { OperationRef.isInstance(it) }.each { OperationRef d ->
      d.parent.add( new Prop(name: d.ref.parent.uncap, type: d.ref.parent) ) }
    model
  }


  void generate(Model model, File target) {
    FacetTemplateLoader templateLoader = new FacetTemplateLoader()

    Generator generator = new Generator()
    generator.add(templateLoader.loadFacetTemplates(model))

    def processorFactory = new ProcessorsFactory()

    extendGenerator(generator, processorFactory, templateLoader)

    generator.add(processorFactory.printProcessor())
    generator.add(processorFactory.fileProcessor(target))


    Context c = new Context(name: model.name)
    c.model = model

    generator.generate(c)
  }

  void extendGenerator(generator, processorFactory, templateLoader) {
  }
}
