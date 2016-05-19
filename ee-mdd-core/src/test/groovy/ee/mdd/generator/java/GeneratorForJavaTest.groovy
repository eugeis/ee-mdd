package ee.mdd.generator.java

import org.junit.Test

import ee.mdd.model.component.Model

class GeneratorForJavaTest {
  GeneratorForJava generator = new GeneratorForJava()

  @Test
  void testExtendModel() {
    Model model = generator.builder.model('Test') {
    }

    model.extend {
      component('Foo') {
      }
    }

    println model
  }

  @Test
  void testLoadTemplates() {

    def modelFile = new File('D:/views/git/ee-mdd_example/model.groovy')
    def model = generator.loadModel(modelFile.toURI().toURL(), {
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

    model = generator.deriveModel(model)

    generator.generate(model, new File('D:/CG/src/cg-pl'), null, 'shared')
    
    println model
  }
}
