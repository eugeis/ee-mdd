package ee.mdd.model.ui;
class TextField extends Control {
  OnChange onChange
  String getWidgetInterface() { 'TextInput' }
  String getWidgetTypeShort() { 'Field' }
  def add(OnChange item) { onChange = item; onChange.eventValueType = 'String'; super.add(item) }
}