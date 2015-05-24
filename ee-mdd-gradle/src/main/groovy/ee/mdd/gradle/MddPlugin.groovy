package ee.mdd.gradle

import java.beans.Introspector

import org.gradle.api.Plugin
import org.gradle.api.Project

import ee.mdd.gradle.task.DeriveModel;
import ee.mdd.gradle.task.Generate
import ee.mdd.gradle.task.LoadModel
import ee.mdd.gradle.task.ShowModel

class MddPlugin implements Plugin<Project> {
  private Project p
  private Mdd config

  @Override
  void apply( final Project p ) {
    this.p = p
    this.config = addExtension( Mdd )

    addTask LoadModel
    addTask DeriveModel
    addTask ShowModel
    addTask Generate
  }

  private def addTask( Class type ) {
    p.extensions.extraProperties.set( type.getSimpleName(), type )
    p.tasks.create( buildName(type), type )
  }

  private def addExtension( Class type ) {
    p.extensions.create( buildName(type), type )
  }

  protected String buildName(Class type) {
    Introspector.decapitalize(type.simpleName)
  }
}
