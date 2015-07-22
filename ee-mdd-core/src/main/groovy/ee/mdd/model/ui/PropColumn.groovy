package ee.mdd.model.ui;
class PropColumn extends Column {
  EntityProp prop
  String deriveName() { prop.name }
}