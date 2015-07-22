package ee.mdd.model.ui;
class CheckBox extends Control {
  OnChange onChange
  def add(OnChange item) { onChange = item; onChange.eventValueType = 'ToggleState'; super.add(item) }
}