package ee.mdd.model.ui;
class TimeField extends Control {
  OnChange onChange
  String getWidgetInterface() { 'TimeInput' }
  String getWidgetTypeShort() { 'Field' }
  def add(OnChange item) { onChange = item; onChange.eventValueType = 'Date'; super.add(item)}
}