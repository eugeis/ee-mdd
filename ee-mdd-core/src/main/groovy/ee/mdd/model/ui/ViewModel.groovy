package ee.mdd.model.ui;

import ee.mdd.model.Names

class ViewModel extends PresentationLogicUnit {
  Names n

  String deriveName() {
    "${view.domainName}Model"
  }

  Names getN() {
    if (!n) {
      n = new Names(this, name)
    }
    n
  }
}