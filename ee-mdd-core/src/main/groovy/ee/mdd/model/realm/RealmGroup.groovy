package ee.mdd.model.realm

import ee.mdd.model.Composite


class RealmGroup extends Composite {
  String getGroupDescription() {
    description ?: name
  }
}
