package ee.mdd.generator.java

import org.junit.Test

import ee.mdd.model.component.Model

class GeneratorForJavaTest {
    GeneratorForJava generator = new GeneratorForJava()

    @Test
    void testExtendModel() {
        Model model = generator.builder.model('Test') {
            model('g1') {
                component('Test1') {
                    //moduleGroup('base', modules: ['module1', 'module2'])
                    module('module1')
                    module('module2')
                    module('module3')
                    module('module4') {
                        dependencies(modules: ['module2'])
                    }
                }
            }

            model('g2') {
                component('Test2') {
                    //moduleGroup('base', modules: ['module1', 'module2'])
                    module('module1')
                    module('module2')
                    module('module3')
                    module('module4') {
                        dependencies(modules: ['g1.Test1.module1'])
                    }
                }
            }

            component('Test3') {
                //moduleGroup('base', modules: ['module1', 'module2'])
                module('module1')
                module('module2')
                module('module3')
                module('module4') {
                    dependencies(modules: ['Test1.module1'])
                }
            }

            component('Test4') {
                //moduleGroup('base', modules: ['module1', 'module2'])
                module('module1')
                module('module2')
                module('module3')
                module('module4') {
                    dependencies(modules: ['Test3.module1'])
                }
            }
        }

        c.name(entity.n.cap.implBuilder).

        generator.builder.typeResolver.printNotResolved()

        model.extend {
            component('Foo') {
            }
        }

        println model
    }

    @Test
    void testLoadTemplates() {

        def modelFile = new File('D:/views/git/ee-mdd_example/model.groovy')
        def model = generator.load(modelFile.toURI().toURL(), {
            java {
                common()
                cdi()
                ejb()
                jms()
                jpa()
                test()
                //ee()
                cg()
                ui()
                sm()
            }
        })

        model = generator.derive(model)

        generator.generate(model, new File('D:/CG/src/cg-pl'), null, 'shared')

        println model
    }
}
