package ee.mdd.model.ui

import ee.mdd.model.component.DataType

class DataTypeTable extends Table {
  DataType type
  String deriveName() {
    "${entity.name}s"
  }
}