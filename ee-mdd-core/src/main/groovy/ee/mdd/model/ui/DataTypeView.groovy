package ee.mdd.model.ui

import ee.mdd.model.component.DataType
import ee.mdd.model.component.Prop

class DataTypeView extends View {
  DataType type
  String details = false
  def excludeProps = []

  String deriveName() {
    details? "${entityRef}View" : "${entityRef}sView"
  }

  void buildMe() {

    super.buildMe()

    excludeProps = excludeProps as Set

    //create controls for entity
    if(details) {
      type.props.each { Prop prop ->
        if (excludeProps.contains(prop.name)) {
          this.add(new PropTextField(prop: prop))
        }
      }
    } else {
      Table table = new DataTypeTable(type: type)
      type.props.each { Prop prop ->
        if (excludeProps.contains(prop.name)) {
          table.add(new PropColumn(prop: prop))
        }
      }
    }
  }
}