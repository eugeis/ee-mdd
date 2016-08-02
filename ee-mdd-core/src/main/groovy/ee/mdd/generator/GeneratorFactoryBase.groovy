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
import ee.mdd.model.Composite
import ee.mdd.model.Element
import ee.mdd.model.component.Facet
import ee.mdd.model.component.OperationRef
import ee.mdd.model.component.Prop
import ee.mdd.model.component.StructureUnit

/**
 *
 * @author Eugen Eisler
 */
class GeneratorFactoryBase {

    ModelBuilder builder = new ModelBuilder()

    StructureUnit load(URL modelSource, Closure facetClosure = null) {
        StructureUnit ret
        if (facetClosure) {
            Facet facet = builder.build(facetClosure)
            ret = builder.build(modelSource)
            if (facet) {
                ret.add(facet)
                facet.extend(ret)
            }
        } else {
            ret = builder.build(modelSource)
        }

        builder.typeResolver.printNotResolved()
        ret
    }

    StructureUnit derive(StructureUnit item) {
        //create props for delegates
        if (Composite.isInstance(item)) {
            ((Composite) item).findAllDown { OperationRef.isInstance(it) }.each { OperationRef d ->
                if (d.ref) {
                    d.parent.add(new Prop(name: d.ref.parent.uncap, type: d.ref.parent))
                } else {
                    println "Delegate $d.name was not resolved"
                }
            }
        }
        item
    }


    void generate(StructureUnit item, File target, Closure targetModuleResolver, String targetLayout) {
        FacetTemplateLoader templateLoader = new FacetTemplateLoader()

        Generator generator = new Generator()
        generator.add(templateLoader.loadFacetTemplates(item))

        def processorFactory = new ProcessorsFactory()

        extendGenerator(generator, processorFactory, templateLoader, targetModuleResolver)

        generator.add(processorFactory.printProcessor())
        generator.add(processorFactory.fileProcessor(target))


        Context c = new Context(name: item.name,
                outputType: OutputType.LOGIC, outputPurpose: OutputPurpose.PRODUCTION)
        c.model = item
        c.targetLayout = targetLayout

        generator.initialize(c)

        generator.generate(c)
    }

    void extendGenerator(generator, processorFactory, templateLoader) {
    }
}
