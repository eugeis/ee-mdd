package ee.mdd.model.ui

import ee.mdd.model.component.Prop

class PropTextField extends TextField {
  Prop prop

  String deriveName() {
    prop.name
  }
}