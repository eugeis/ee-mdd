package ee.mdd.model.ui;
class Spinner extends Control {
  OnChange onChange
  String getWidgetInterface() { 'NumberInput' }
  String getWidgetTypeShort() { 'Field' }
  def add(OnChange item) { onChange = item; onChange.eventValueType = 'Integer'; super.add(item) }
}