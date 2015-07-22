package ee.mdd.model.ui;
class ComboBox extends Control {
  OnSelect onSelect
  void add(OnSelect item) { super.add(item); onSelect = item }
}