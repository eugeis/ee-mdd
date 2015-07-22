package ee.mdd.model.ui;
class DateField extends Control {
  OnChange onChange
  String getWidgetInterface() { 'DateInput' }
  String getWidgetTypeShort() { "Field" }
  def add(OnChange item) { onChange = item; onChange.eventValueType = 'Date'; super.add(item) }
}