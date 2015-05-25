package ee.mdd.gradle

import ee.mdd.generator.java.GeneratorForJava
import ee.mdd.model.Element

class Mdd {
  def target = './temp/'
  def modelSource = 'model.groovy'
  Element model
  GeneratorForJava generator = new GeneratorForJava()
}
