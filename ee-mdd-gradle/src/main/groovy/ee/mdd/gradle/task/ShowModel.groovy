package ee.mdd.gradle.task

import java.awt.print.PrinterAbortException

import org.gradle.api.tasks.TaskAction

import ee.mdd.model.component.Facet;
import ee.mdd.util.ElementPrinter

class ShowModel extends MddTask {
  ElementPrinter printer = new ElementPrinter()

  ShowModel() {
    description = 'Show the loaded model.'
    dependsOn( buildName(DeriveModel) )
  }

  @TaskAction
  void show() {
    validateState()
    printer.print(mdd.model, { !Facet.isInstance(it) })
  }

  private validateState() {
    if ( mdd.model == null ) {
      throw new IllegalStateException( 'Required model property is not set.' )
    }
  }
}
