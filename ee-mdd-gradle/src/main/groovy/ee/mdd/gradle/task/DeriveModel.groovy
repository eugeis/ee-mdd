package ee.mdd.gradle.task

import org.gradle.api.tasks.TaskAction

class DeriveModel extends MddTask {

  DeriveModel() {
    description = 'Start generation for configured generator and loaded model.'
    dependsOn( buildName(LoadModel) )
  }

  @TaskAction
  void generate() {
    validateState()
    mdd.model = mdd.generator.deriveModel(mdd.model)
  }

  private validateState() {
    if ( mdd.model == null ) {
      throw new IllegalStateException( 'Required model property is not set.' )
    }
  }
}
