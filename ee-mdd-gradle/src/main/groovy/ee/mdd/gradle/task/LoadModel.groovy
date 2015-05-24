package ee.mdd.gradle.task

import org.gradle.api.tasks.TaskAction

class LoadModel extends MddTask {

  LoadModel() {
    description = 'Load model from source.'
    onlyIf { !mdd.model }
  }

  @TaskAction
  void load() {
    mdd.model = [:]
    validateState()
  }

  private validateState() {
    if ( mdd.modelSource == null ) {
      throw new IllegalStateException( 'Required modelSource property is not set.' )
    }
  }
}
