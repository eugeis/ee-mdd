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
}
