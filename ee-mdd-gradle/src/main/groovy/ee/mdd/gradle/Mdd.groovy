package ee.mdd.gradle

import ee.mdd.generator.java.GeneratorForJava
import ee.mdd.model.Element

class Mdd {
  def target = './temp/'
  def targetLayout = 'standard'
  Closure targetModuleResolver
  def modelSource = 'model.groovy'
  Closure facet
  Element model
  GeneratorForJava generator = new GeneratorForJava()
}
