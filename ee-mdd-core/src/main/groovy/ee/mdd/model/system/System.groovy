package ee.mdd.model.system

import ee.mdd.model.Element

class System extends Element {
  List<Machine> machines = []

  def add(Machine item) {
    machines << item; super.add(item)
  }
}