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
    mdd.generator.generate( mdd.model, new File(mdd.target), mdd.targetModuleResolver, mdd.targetLayout )
  }

  private validateState() {
    if ( mdd.model == null ) {
      throw new IllegalStateException( 'Required model property is not set.' )
    }
  }
}
