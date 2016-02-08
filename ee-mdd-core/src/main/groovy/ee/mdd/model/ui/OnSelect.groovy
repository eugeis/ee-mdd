package ee.mdd.model.ui
class OnSelect extends Listener {
  boolean multi = false

  boolean init() {
    super.init()
    eventType = 'SelectionEvent'
    eventValueType = 'String'
    callbackBaseName = "${parent.cap}Selected"
    true
  }

  void buildMe() {
    super.buildMe()
    if(multi) {
      receiverCallValue = "$receiverName(asListOfType(${eventDomainType}.class, event.getSelectedItems()))"
      signatureValue = "List<$eventDomainType> value"
      eventValueType = "List<$eventDomainType>"
      eventDefaultValue = "asList(defaultValue(${eventDomainType}.class))"
    } else {
      signatureValue = "$eventDomainType value"
      receiverCallValue = "$receiverName(($eventDomainType)event.getSelectedItem())"
    }
  }
}