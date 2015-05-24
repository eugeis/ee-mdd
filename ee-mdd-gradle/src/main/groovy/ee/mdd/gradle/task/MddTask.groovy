package ee.mdd.gradle.task

import java.beans.Introspector

import org.gradle.api.DefaultTask

import ee.mdd.gradle.Mdd

class MddTask extends DefaultTask {
  Mdd mdd = project.mdd

  MddTask() {
    group = 'Model Driven Development'
  }

  protected static String buildName(Class type) {
    Introspector.decapitalize(type.simpleName)
  }
}
