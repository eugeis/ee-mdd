package ee.mdd.model.realm

import ee.mdd.model.Element

class RealmWorkstationType extends Element {
  String getWorkstationTypeDescription() {
    description ? description : name
  }
}
