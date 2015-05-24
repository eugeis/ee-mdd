package ee.mdd.gradle.task

import org.gradle.api.tasks.TaskAction

class LoadModel extends MddTask {

  LoadModel() {
    description = 'Load model from source.'
    onlyIf { !mdd.model }
  }

  @TaskAction
  void load() {
    validateState()

    def modelFile = new File(mdd.modelSource)
    if(modelFile.exists()) {
      mdd.model = mdd.generator.loadModel(modelFile.toURI().toURL())
    } else {
      throw new IllegalStateException( 'Model file does not exists $modelFile.' )
    }
  }

  private validateState() {
    if ( mdd.modelSource == null ) {
      throw new IllegalStateException( 'Required modelSource property is not set.' )
    }
  }
}
