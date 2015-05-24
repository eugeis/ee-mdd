package ee.mdd.gradle.task

import org.gradle.api.tasks.TaskAction

class ShowModel extends MddTask {

  ShowModel() {
    description = 'Show the loaded model.'
    dependsOn( buildName(DeriveModel) )
  }
  
  @TaskAction
  void show() {
    println "############## $mdd.model"
    validateState()
  }

  private validateState() {
    if ( mdd.model == null ) {
      throw new IllegalStateException( 'Required model property is not set.' )
    }
  }
}
