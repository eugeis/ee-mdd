package ee.mdd.gradle.task

import org.gradle.api.tasks.TaskAction

class Generate extends MddTask {

  Generate() {
    description = 'Start generation for configured generator and loaded model.'
    dependsOn( buildName(DeriveModel) )
  }
  
  @TaskAction
  void generate() {
    validateState()
    
  }

  private validateState() {
    if ( mdd.model == null ) {
      throw new IllegalStateException( 'Required model property is not set.' )
    }
  }
}
