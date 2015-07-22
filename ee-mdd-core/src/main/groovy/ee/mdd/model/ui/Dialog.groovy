package ee.mdd.model.ui

import ee.mdd.model.Base

class Dialog extends Widget {
  String deriveName(Base p = parent ) {
    "${view.domainName}Dialog"
  }
}